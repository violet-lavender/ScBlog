package com.wyz.blog.content.pojo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

/**
 * 搜索查询条件
 */
@Data
public class SearchQuery {

	/**
	 * 搜索的内容
	 */
	@NotNull
	String key;

	/**
	 * 判断字段
	 */
	String sortBy;

	/**
	 * 当前页
	 */
	@Range(min = 1, max = 50)
	int page = 1;

	/**
	 * 页大小
	 */
	@Range(min = 1, max = 20)
	int size = 10;

}
