package com.wyz.blink.pojo;

import lombok.Data;

/**
 * 保存Blink业务对象 BO
 */
@Data
public class SaveBlinkBO {

	/**
	 * 用户id
	 */
	Integer userId;

	/**
	 * 学校代码
	 */
	Integer schoolCode;

	/**
	 * 动态内容
	 */
	String content;

}
