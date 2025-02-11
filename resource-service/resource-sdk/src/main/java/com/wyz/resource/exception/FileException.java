package com.wyz.resource.exception;

import com.wyz.common.exception.BusinessException;

/**
 * 文件异常类
 */
public class FileException extends BusinessException {

	public FileException() {
		super("文件异常");
	}

	public FileException(String errorMessage) {
		super(errorMessage);
	}

}
