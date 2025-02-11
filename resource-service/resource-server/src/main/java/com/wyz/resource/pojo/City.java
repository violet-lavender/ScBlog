package com.wyz.resource.pojo;

import lombok.Data;

/**
 * 城市实体类
 */
@Data
public class City {

	/**
	 * 城市id
	 */
	Integer id;

	/**
	 * 城市名称
	 */
	String name;

	/**
	 * 所属省份的id
	 */
	Integer provinceId;

}
