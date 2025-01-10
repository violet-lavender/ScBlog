package com.wyz.common.result;

import lombok.Data;

import java.util.List;

/**
 * @author 阿杆
 */
@SuppressWarnings({"AlibabaPojoNoDefaultValue", "AlibabaPojoMustUsePrimitiveField"})
@Data
public class ListVO<T> implements IListVO<T> {

	/**
	 * 查询数据列表
	 */
	List<T> records;

	/**
	 * 总数
	 */
	long total = 0;

	/**
	 * 每页显示条数，默认 20
	 */
	long size = 20;

	/**
	 * 当前页
	 */
	long current = 1;

}
