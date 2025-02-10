package com.wyz.blog.pojo.vo;

import com.wyz.blog.pojo.domain.Blog;
import com.wyz.user.dto.UserDTO;
import lombok.Data;

/**
 * 博客排行榜VO
 */
@Data
public class RankHotVO {

	/**
	 * 博客信息
	 */
	Blog blog;

	/**
	 * 用户信息
	 */
	UserDTO author;

	/**
	 * 排行榜热度
	 */
	Double hot;

}
