package com.wyz.blog.pojo.vo;

import com.wyz.user.dto.UserDTO;
import lombok.Data;

/**
 * 用户排行榜VO
 */
@Data
public class RankAuthorVO {

	/**
	 * 用户信息
	 */
	UserDTO author;

	/**
	 * 排行榜热度
	 */
	Double hot;

	/**
	 * 粉丝数
	 */
	Integer fansNum;

	/**
	 * 获赞数
	 */
	Integer likeNum;

}
