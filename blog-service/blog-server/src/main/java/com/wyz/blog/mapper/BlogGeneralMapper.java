package com.wyz.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wyz.blog.pojo.domain.BlogGeneral;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 博客概述Mapper
 */
@Mapper
public interface BlogGeneralMapper extends BaseMapper<BlogGeneral> {

	/**
	 * 博客阅读量+1
	 *
	 * @param blogId 博客id
	 */
	@Update("update blog_general set view_num = view_num + 1 where blog_id = #{blogId};")
	void increaseViewNum(Integer blogId);

	/**
	 * 博客点赞数+1
	 *
	 * @param blogId 博客id
	 */
	@Update("update blog_general set like_num = like_num + 1 where blog_id = #{blogId};")
	void increaseLikeNum(Integer blogId);

	/**
	 * 博客点赞数-1
	 *
	 * @param blogId 博客id
	 */
	@Update("update blog_general set like_num = like_num - 1 where blog_id = #{blogId} and like_num > 0;")
	void decreaseLikeNum(Integer blogId);

	/**
	 * 博客收藏数+1
	 *
	 * @param blogId 博客id
	 */
	@Update("update blog_general set collection_num = collection_num + 1 where blog_id = #{blogId};")
	void increaseCollectionNum(Integer blogId);

	/**
	 * 博客收藏数-1
	 *
	 * @param blogId 博客id
	 */
	@Update("update blog_general set collection_num = collection_num - 1 where blog_id = #{blogId} and collection_num > 0;")
	void decreaseCollectionNum(Integer blogId);

	/**
	 * 博客评论数+1
	 *
	 * @param blogId 博客id
	 */
	@Update("update blog_general set comment_num = comment_num + 1 where blog_id = #{blogId};")
	void increaseCommentNum(Integer blogId);

	/**
	 * 博客评论数-1
	 *
	 * @param blogId 博客id
	 */
	@Update("update blog_general set comment_num = comment_num - 1 where blog_id = #{blogId} and comment_num > 0;")
	void decreaseCommentNum(Integer blogId);

	/**
	 * 批量更新分数
	 *
	 * @param list
	 */
	void batchUpdateScore(@Param("list") List<BlogGeneral> list);

}
