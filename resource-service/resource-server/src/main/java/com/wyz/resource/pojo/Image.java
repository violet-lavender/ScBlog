package com.wyz.resource.pojo;

import lombok.Data;

import java.sql.Timestamp;

/**
 * 图片对象
 */
@Data
public class Image {

	/**
	 * 图片id
	 */
	Integer id;

	/**
	 * 图片链接
	 */
	String url;

	/**
	 * 访问次数
	 */
	Integer visit;

	/**
	 * 修改时间
	 */
	Timestamp modifiedTime;

	/**
	 * 创建时间
	 */
	Timestamp createTime;

}
