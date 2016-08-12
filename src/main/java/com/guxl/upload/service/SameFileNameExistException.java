package com.guxl.upload.service;

public class SameFileNameExistException extends Exception {

	static final long serialVersionUID = -1L;
	
	public SameFileNameExistException() {
		super("保存路径下已存在同名文件");
	}

	public SameFileNameExistException(String message) {
		super(message);
	}

	public SameFileNameExistException(Throwable cause) {
		super(cause);
	}

	public SameFileNameExistException(String message, Throwable cause) {
		super(message, cause);
	}

	public SameFileNameExistException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
