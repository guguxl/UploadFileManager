package com.guxl.upload.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface FileManagerInterface {
	/**
	 * @param file 上传的文件的MultipartFile对象
	 * @param remainOriginName 写入文件系统的文件名称是否保留原始文件名
	 * @param overwrite 如果要保存的路径下存在同名文件时，是否覆盖。如果为false，但存在同名文件时，该方法可能抛出SameFileNameExistException异常
	 * @param sizeLimit 文件大小限制，单位为字节数byte。如果为-1表示无限制
	 * @return
	 */
	FileInfo saveFile(MultipartFile file, boolean remainOriginName, boolean overwrite, long sizeLimit) 
			throws SameFileNameExistException,FileSizeExceedLimitException,IOException;
	
	/**
	 * @param fileInfo 要删除的已上传的文件
	 */
	void removeFile(FileInfo fileInfo);
}
