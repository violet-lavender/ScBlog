package com.wyz.blog.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wyz.blog.mapper.*;
import com.wyz.blog.pojo.bo.ActionStatusBO;
import com.wyz.blog.pojo.bo.BlogInfoBO;
import com.wyz.blog.pojo.bo.BlogStatusBO;
import com.wyz.blog.pojo.domain.Blog;
import com.wyz.blog.pojo.domain.BlogView;
import com.wyz.blog.pojo.domain.ViewBlog;
import com.wyz.blog.pojo.vo.BlogContentVO;
import com.wyz.blog.pojo.vo.BlogInfoListVO;
import com.wyz.blog.pojo.vo.BlogStatusListVO;
import com.wyz.blog.sdk.BlogOperateDTO;
import com.wyz.blog.service.BlogViewService;
import com.wyz.blog.type.BlogStatusType;
import com.wyz.common.result.RestResult;
import com.wyz.common.web.utils.RequestUtils;
import com.wyz.user.client.UserClient;
import com.wyz.user.dto.UserDTO;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.wyz.blog.sdk.BlogMqConstants.BLOG_OPERATE_READ_KEY;
import static com.wyz.blog.sdk.BlogMqConstants.BLOG_TOPIC_EXCHANGE;

/**
 * 博客视图服务实现类
 */
@Slf4j
@Service
public class BlogViewServiceImpl extends ServiceImpl<BlogViewMapper, BlogView> implements BlogViewService {

	@Resource
	private BlogViewMapper blogViewMapper;

	@Resource
	private BlogMapper blogMapper;

	@Resource
	private BlogGeneralMapper blogGeneralMapper;

	@Resource
	private LikeBlogMapper likeBlogMapper;

	@Resource
	private CollectBlogMapper collectBlogMapper;

	@Resource
	private BlogContentHtmlMapper blogContentHtmlMapper;

	@Resource
	private ViewBlogMapper viewBlogMapper;

	@Resource
	private UserClient userClient;

	@Resource
	private RabbitTemplate rabbitTemplate;

	@Resource
	private RedisTemplate<String, Object> redisTemplate;

	@Override
	public BlogStatusListVO getRecommendBlogList(Integer userId, int page, int pageSize) {
		BlogInfoListVO blogInfoListVO = searchBlog(null, page, pageSize);
		BlogStatusListVO blogStatusListVO = BeanUtil.copyProperties(blogInfoListVO, BlogStatusListVO.class);
		addActionStatusToList(blogStatusListVO.getRecords(), userId);
		return blogStatusListVO;
	}

	@Override
	public BlogStatusListVO getNewBlogList(Integer userId, int page, int pageSize) {
		// 设置查询条件：状态为公开，并以id倒序（表示发布时间最新）
		LambdaQueryWrapper<BlogView> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(BlogView::getStatus, BlogStatusType.PUBLISH.getValue()).orderByDesc(BlogView::getId);
		// 设置分页
		IPage<BlogView> iPage = new Page<>(page, pageSize);
		blogViewMapper.selectPage(iPage, wrapper);
		// 整合信息
		return viewListVoToActionStatusListVO(iPage, userId);
	}

	@Override
	public BlogStatusListVO getFollowBlogList(@Valid @NotNull Integer userId, int page, int pageSize) {
		// 设置查询条件：作者为关注的对象，状态为公开，并以id倒序（表示发布时间最新）
		LambdaQueryWrapper<BlogView> wrapper = new LambdaQueryWrapper<>();
		RestResult<List<Integer>> result = userClient.getFollowIdList(userId);
		wrapper.in(result.getStatus() && result.getData().size() > 0, BlogView::getAuthorId, result.getData());
		wrapper.eq(BlogView::getStatus, BlogStatusType.PUBLISH.getValue()).orderByDesc(BlogView::getId);
		IPage<BlogView> iPage = new Page<>(page, pageSize);
		blogViewMapper.selectPage(iPage, wrapper);
		return viewListVoToActionStatusListVO(iPage, userId);
	}

	@Override
	public BlogInfoListVO searchBlog(String search, int page, int pageSize) {
		// 设置查询条件：公开的，并以得分排序
		LambdaQueryWrapper<BlogView> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(BlogView::getStatus, BlogStatusType.PUBLISH.getValue()).orderByDesc(BlogView::getScore);
		// 设置模糊查询
		wrapper.like(search != null, BlogView::getTitle, search);
		// 设置分页
		IPage<BlogView> iPage = new Page<>(page, pageSize);
		blogViewMapper.selectPage(iPage, wrapper);
		// 开始整合信息
		return viewListToInfoListVO(iPage);
	}

	@Override
	public Map<Integer, ActionStatusBO> getBlogActionStatus(Integer userId, Integer... blogIds) {
		HashMap<Integer, ActionStatusBO> map = new HashMap<>(blogIds.length);
		// 空列表不查数据，会报错
		if (blogIds.length == 0) {
			return map;
		}
		Set<Integer> likeBlogIds = likeBlogMapper.selectMapByUserIdAndBlogIdList(userId, blogIds);
		Set<Integer> collectBlogIds = collectBlogMapper.selectMapByUserIdAndBlogIdList(userId, blogIds);
		for (Integer blogId : blogIds) {
			ActionStatusBO statusDTO = new ActionStatusBO();
			statusDTO.setIsLike(likeBlogIds.contains(blogId));
			statusDTO.setIsCollect(collectBlogIds.contains(blogId));
			map.put(blogId, statusDTO);
		}
		return map;
	}

	@Override
	public BlogContentVO getBlogContentHtml(Integer id, Integer userId) {
		BlogContentVO blog = new BlogContentVO();
		// 博客内容
		blog.setContent(blogContentHtmlMapper.selectById(id));
		// 博客基本信息
		BlogView blogView = blogViewMapper.selectById(id);
		if (blogView == null) {
			return null;
		}
		BlogStatusBO blogStatusBO = BeanUtil.copyProperties(blogView, BlogStatusBO.class);
		if (userId != null) {
			// 增加用户博客浏览记录
			ViewBlog(id, userId);
			// 获取该用户对该博客的状态
			Map<Integer, ActionStatusBO> status = getBlogActionStatus(userId, id);
			blogStatusBO.setActionStatus(status.get(id));
		}
		blog.setInfo(blogStatusBO);
		// 博客作者信息
		RestResult<UserDTO> result = userClient.getByUserId(blogView.getAuthorId());
		blog.setAuthor(result.getData());

		// 博客阅读量+1
		this.increaseViewNum(id, blogView.getAuthorId(), userId);
		return blog;
	}

	/**
	 * 新增博客浏览记录
	 *
	 * @param blogId 博客ID
	 * @param userId 用户ID
	 */
	public void ViewBlog(Integer blogId, Integer userId) {
		// 判断博客是否存在
		Blog blog = blogMapper.selectById(blogId);
		if (blog == null) {
			return;
		}
		LambdaQueryWrapper<ViewBlog> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(ViewBlog::getBlogId, blogId).eq(ViewBlog::getUserId, userId);
		ViewBlog selectOne = viewBlogMapper.selectOne(wrapper);
		if (selectOne != null) {
			// 浏览记录已存在, 修改时间
			selectOne.setCreateTime(new Timestamp(System.currentTimeMillis()));
			viewBlogMapper.updateById(selectOne);
		} else {
			// 新增浏览记录
			ViewBlog viewBlog = new ViewBlog();
			viewBlog.setBlogId(blogId);
			viewBlog.setUserId(userId);
			viewBlog.setCreateTime(new Timestamp(System.currentTimeMillis()));
			viewBlogMapper.insert(viewBlog);
		}
	}

	/**
	 * 增加博客阅读量，通过ip来限制用户刷访问量
	 *
	 * @param blogId 博客id
	 */
	private void increaseViewNum(Integer blogId, Integer authorId, Integer userId) {
		// 获取当前线程的ip
		String ip = RequestUtils.getCurIpAddress();
		String key = "blogServer:viewBlog:" + ip + ":" + blogId;
		Boolean success = redisTemplate.opsForValue().setIfAbsent(key, 1, 120, TimeUnit.SECONDS);
		if (Boolean.TRUE.equals(success)) {
			blogGeneralMapper.increaseViewNum(blogId);
			// 封装好请求体后，发送到MQ
			BlogOperateDTO blogOperateDTO = new BlogOperateDTO();
			blogOperateDTO.setBlogId(blogId);
			blogOperateDTO.setAuthorId(authorId);
			blogOperateDTO.setUserId(userId);
			rabbitTemplate.convertAndSend(BLOG_TOPIC_EXCHANGE, BLOG_OPERATE_READ_KEY, blogOperateDTO);
		}
	}

	/**
	 * 将查询出来的数据添加上其他信息（此处只有作者信息）
	 */
	private BlogInfoListVO viewListToInfoListVO(IPage<BlogView> blogListVO) {
		BlogInfoListVO blogInfoListVO = BeanUtil.copyProperties(blogListVO, BlogInfoListVO.class);
		addAuthorNameToList(blogInfoListVO.getRecords());
		return blogInfoListVO;
	}

	/**
	 * 将查询出来的数据添加上作者信息和用户点赞的状态
	 */
	private BlogStatusListVO viewListVoToActionStatusListVO(IPage<BlogView> blogListVO, Integer userId) {
		BlogInfoListVO blogInfoListVO = viewListToInfoListVO(blogListVO);
		BlogStatusListVO blogStatusListVO = BeanUtil.copyProperties(blogInfoListVO, BlogStatusListVO.class);
		if (userId != null) {
			addActionStatusToList(blogStatusListVO.getRecords(), userId);
		}
		return blogStatusListVO;
	}

	/**
	 * 批量添加作者信息数据
	 */
	private void addAuthorNameToList(List<BlogInfoBO> blogInfoList) {
		// 获取用户id列表
		Set<Integer> userIdList = blogInfoList.stream().map(BlogInfoBO::getAuthorId).collect(Collectors.toSet());
		// 查询用户信息
		RestResult<Map<Integer, UserDTO>> result = userClient.getUserList(userIdList);
		// 判断信息是否正确
		if (result.getStatus()) {
			// 添加用户昵称
			Map<Integer, UserDTO> userMap = result.getData();
			for (BlogInfoBO blogInfoBO : blogInfoList) {
				UserDTO author = userMap.get(blogInfoBO.getAuthorId());
				if (author != null) {
					blogInfoBO.setAuthorName(author.getNickname());
				}
			}
		}
		// return blogInfoList;
	}

	/**
	 * 批量添加用户点赞、收藏状态信息
	 *
	 * @param blogList 博客列表
	 * @param userId   用户id
	 */
	private void addActionStatusToList(List<BlogStatusBO> blogList, Integer userId) {
		// 构造博客id列表
		Integer[] blogIdList = blogList.stream().map(BlogStatusBO::getId).toArray(Integer[]::new);
		// 查询这些博客的点赞状态
		Map<Integer, ActionStatusBO> blogActionStatus = getBlogActionStatus(userId, blogIdList);

		for (BlogStatusBO record : blogList) {
			ActionStatusBO actionStatus = blogActionStatus.get(record.getId());
			record.setActionStatus(actionStatus);
		}
		// return blogList;
	}

}
