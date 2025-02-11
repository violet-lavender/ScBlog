package com.wyz.resource.exception;

import com.wyz.common.exception.BusinessException;

/**
 * 上传异常
 */
public class UploadException extends BusinessException {

	public UploadException(String errorMessage) {
		super(errorMessage);
	}

}
