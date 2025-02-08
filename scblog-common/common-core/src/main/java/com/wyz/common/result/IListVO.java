package com.wyz.common.result;

import java.io.Serializable;
import java.util.List;

/**
 * 分页数据封装接口
 */
@SuppressWarnings("AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc")  // 抑制编译器/代码检查工具警告的注解
public interface IListVO<T> extends Serializable {

	/**
	 * 当前分页总页数
	 */
	default long getPages() {
		if (getSize() == 0) {
			return 0L;
		}
		long pages = getTotal() / getSize();
		if (getTotal() % getSize() != 0) {
			pages++;
		}
		return pages;
	}

	/**
	 * 内部什么也不干
	 * <p>只是为了 json 反序列化时不报错</p>
	 */
	default void setPages(long pages) {
	}

	/**
	 * 分页记录列表
	 */
	List<T> getRecords();

	/**
	 * 设置分页记录列表
	 */
	void setRecords(List<T> records);

	/**
	 * 当前满足条件总行数
	 */
	long getTotal();

	/**
	 * 设置当前满足条件总行数
	 */
	void setTotal(long total);

	/**
	 * 获取每页显示条数
	 */
	long getSize();

	/**
	 * 设置每页显示条数
	 */
	void setSize(long size);

	/**
	 * 当前页
	 */
	long getCurrent();

	/**
	 * 设置当前页
	 */
	void setCurrent(long current);

}
