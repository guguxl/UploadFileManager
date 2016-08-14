package com.guxl.upload.common;

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

import com.guxl.upload.common.exception.SameFileNameExistException;


public interface UploadedFileWriter {
	
	/**
	 * 将实现multipartFile接口的上传文件对象保存到文件系统中
	 * @param multipartFile 上传文件的MultipartFile对象
	 * @param pathFileName 保存到文件系统时使用的文件名称（应包含完整路径）
	 * @param overwrite 当给定pathFileName在文件系统下存在同名文件时，是否要覆盖旧的文件
	 * @throws SameFileNameExistException 如果文件系统中已经存在pathFileName指定的文件，且overwrite参数值为false时，抛出该异常
	 * @throws IOException 可能抛出IOException
	 */
	void writeFile(MultipartFile multipartFile, String pathFileName, boolean overwrite) throws SameFileNameExistException, IOException;
	
	
	/**
	 * 从文件系统中删除pathFileName所指定的文件
	 * @param pathFileName （包含完整路径的）待删除的文件名称
	 * @throws IOException
	 */
	void removeFile(String pathFileName) throws IOException;
	
	

}
