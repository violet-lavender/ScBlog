package com.wyz.comment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wyz.comment.pojo.Comment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 评论服务 Mapper
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

}
