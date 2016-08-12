package com.guxl.upload.service;

public class FileSizeExceedLimitException extends Exception {

	static final long serialVersionUID = -1L;
	
	public FileSizeExceedLimitException() {
		super("文件大小已超过限定字节数");
	}

	public FileSizeExceedLimitException(String message) {
		super(message);
	}

	public FileSizeExceedLimitException(Throwable cause) {
		super(cause);
	}

	public FileSizeExceedLimitException(String message, Throwable cause) {
		super(message, cause);
	}

	public FileSizeExceedLimitException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
