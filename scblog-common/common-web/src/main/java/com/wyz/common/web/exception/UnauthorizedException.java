package com.wyz.common.web.exception;

import com.wyz.common.exception.BusinessException;

/**
 * 用户未登录异常
 */
public class UnauthorizedException extends BusinessException {

	public UnauthorizedException() {
		super("用户未登录");
	}

}
