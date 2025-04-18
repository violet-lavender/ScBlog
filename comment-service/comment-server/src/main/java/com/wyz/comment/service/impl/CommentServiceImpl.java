package com.wyz.comment.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wyz.blog.client.BlogClient;
import com.wyz.blog.dto.BlogDTO;
import com.wyz.comment.mapper.CommentMapper;
import com.wyz.comment.pojo.Comment;
import com.wyz.comment.pojo.CommentBO;
import com.wyz.comment.pojo.CommentListVO;
import com.wyz.comment.pojo.CommentVO;
import com.wyz.comment.sdk.CommentDTO;
import com.wyz.comment.service.CommentService;
import com.wyz.common.exception.BusinessException;
import com.wyz.common.result.RestResult;
import com.wyz.user.client.UserClient;
import com.wyz.user.dto.UserDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

import static com.wyz.comment.sdk.MqConstants.*;

/**
 * 评论服务实现类
 */
@Slf4j
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Resource
    private CommentMapper commentMapper;

    @Resource
    private UserClient userClient;

    @Resource
    private BlogClient blogClient;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Override
    public void create(Comment comment) {
        // 统一强制获取博客信息，确保后续逻辑可用
        RestResult<BlogDTO> blogResult = blogClient.getBlogInfo(comment.getBlogId());
        if (blogResult == null || !blogResult.getStatus() || blogResult.getData() == null) {
            throw new BusinessException("关联博客不存在或获取失败");
        }
        BlogDTO blog = blogResult.getData();

        // 处理父评论逻辑（次级评论）
        if (comment.getParentId() != null) {
            LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Comment::getBlogId, comment.getBlogId())
                    .eq(Comment::getId, comment.getParentId());
            Comment parentComment = commentMapper.selectOne(wrapper);
            if (parentComment == null) {
                throw new BusinessException("父评论不存在");
            }
            // 确保父评论的博客ID与当前评论一致
            if (!parentComment.getBlogId().equals(comment.getBlogId())) {
                throw new BusinessException("评论与博客不匹配");
            }
            // 设置父评论关系
            comment.setParentId(parentComment.getParentId() != null ?
                    parentComment.getParentId() : parentComment.getId());
            comment.setParentUserId(parentComment.getUserId());
        }

        // 保存评论
        comment.setId(null);
        comment.setCreateTime(new Timestamp(System.currentTimeMillis()));
        commentMapper.insert(comment);

        // 发送消息（统一使用已获取的博客信息）
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setBlogId(comment.getBlogId());
        commentDTO.setContent(comment.getContent());
        commentDTO.setUserId(comment.getUserId());
        commentDTO.setAuthorId(blog.getAuthorId()); // 直接使用博客作者ID
        rabbitTemplate.convertAndSend(COMMENT_TOPIC_EXCHANGE, BLOG_COMMENT_INCREASE_KEY, commentDTO);

        log.info("评论创建成功: blogId={}, commentId={}", comment.getBlogId(), comment.getId());
    }

    @Override
    public void checkAndDelete(int userId, int commentId) {
        // 1. 查询评论信息是否存在
        Comment comment = lambdaQuery().eq(Comment::getUserId, userId).eq(Comment::getId, commentId).one();
        if (comment == null) {
            // 2.1 不存在，抛出异常
            throw new BusinessException("非法删除评论");
        }
        // 2.2 存在，删除评论
        commentMapper.deleteById(commentId);
        // 3. 查询博客作者id，发送消息，减少评论数量
        RestResult<BlogDTO> result = blogClient.getBlogInfo(comment.getBlogId());
        // 封装评论对象发送消息
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setAuthorId(result.getData().getAuthorId());
        commentDTO.setUserId(userId);
        commentDTO.setBlogId(comment.getBlogId());
        rabbitTemplate.convertAndSend(COMMENT_TOPIC_EXCHANGE, BLOG_COMMENT_DECREASE_KEY, commentDTO);
    }

    @Override
    public CommentListVO getList(int blogId, int page, int pageSize) {
        CommentListVO vo = new CommentListVO();
        // 先查总数量
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        // 只要博客id一致即可
        wrapper.eq(Comment::getBlogId, blogId);
        Long count = commentMapper.selectCount(wrapper);
        vo.setAllCount(count);
        if (count == 0) {
            // 若总数为0，直接返回
            return vo;
        }

        // 以时间排序(也可以按id倒序)，只查该博客下没有父评论id的，存入父评论组数中
        wrapper.orderByDesc(Comment::getId).isNull(Comment::getParentId);
        IPage<Comment> iPage = new Page<>(page, pageSize);
        commentMapper.selectPage(iPage, wrapper);
        BeanUtil.copyProperties(iPage, vo, "records");
        // 收集有关评论的用户id，便于等会查询用户信息
        Set<Integer> userIdList = new HashSet<>();
        // 收集父评论id，用于查子评论
        List<Integer> parentIdList = new ArrayList<>();
        // 复制父评论
        List<CommentVO> dtoList = new ArrayList<>();
        for (Comment record : iPage.getRecords()) {
            CommentVO commentVO = new CommentVO();
            commentVO.setInfo(BeanUtil.copyProperties(record, CommentBO.class));
            dtoList.add(commentVO);
            // 不用获取parentId，因为它们必为null
            userIdList.add(record.getUserId());
            parentIdList.add(record.getId());
        }

        // 查询子评论
        // 以父评论id查询所有子评论，按时间正序(也可以按id正序)，父评论倒序，子评论正序，避免不连贯
        wrapper.clear();
        wrapper.eq(Comment::getBlogId, blogId).in(Comment::getParentId, parentIdList).orderByAsc(Comment::getId);
        // 查询所有子评论
        log.debug("查询子评论sql,{}", wrapper.getSqlSelect());
        List<Comment> subList = commentMapper.selectList(wrapper);
        List<CommentBO> subListBO = new ArrayList<>();
        for (Comment comment : subList) {
            subListBO.add(BeanUtil.copyProperties(comment, CommentBO.class));
        }
        // 将子评论列表转为map list，便于填充到父评论下，顺便获取用户id列表
        Map<Integer, List<CommentBO>> listMap = new HashMap<>(20);
        for (CommentBO commentBO : subListBO) {
            if (!listMap.containsKey(commentBO.getParentId())) {
                listMap.put(commentBO.getParentId(), new ArrayList<>());
            }
            listMap.get(commentBO.getParentId()).add(commentBO);
            userIdList.add(commentBO.getUserId());
            userIdList.add(commentBO.getParentUserId());
        }

        // 将子评论填充到父评论下
        for (CommentVO commentVO : dtoList) {
            Integer parentId = commentVO.getInfo().getId();
            if (listMap.containsKey(parentId)) {
                commentVO.setSub(listMap.get(parentId));
                commentVO.setSubCount((long) commentVO.getSub().size());
            } else {
                commentVO.setSub(new ArrayList<>());
                commentVO.setSubCount(0L);
            }
        }

        // 获取并设置用户属性, 垃圾代码,,,
        RestResult<Map<Integer, UserDTO>> result = userClient.getUserList(userIdList);
        if (result.getStatus()) {
            Map<Integer, UserDTO> userMap = result.getData();
            for (CommentVO commentVO : dtoList) {
                CommentBO info = commentVO.getInfo();
                info.setNickname(userMap.get(info.getUserId()).getNickname());
                info.setAvatarUrl(userMap.get(info.getUserId()).getAvatarUrl());
                if (commentVO.getSub() == null) {
                    continue;
                }
                for (CommentBO sub : commentVO.getSub()) {
                    sub.setNickname(userMap.get(sub.getUserId()).getNickname());
                    sub.setAvatarUrl(userMap.get(sub.getUserId()).getAvatarUrl());
                    sub.setParentNickname(userMap.get(sub.getParentUserId()).getNickname());
                }
            }
        }
        vo.setRecords(dtoList);
        return vo;
    }

}
