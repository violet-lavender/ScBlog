package com.wyz.common.web.exception;

import com.wyz.common.exception.BusinessException;

/**
 * 访问频繁异常
 */
public class FrequentVisitsException extends BusinessException {

	public FrequentVisitsException(String errorMessage) {
		super(errorMessage);
	}

	public FrequentVisitsException() {
		super("访问频繁");
	}

}
