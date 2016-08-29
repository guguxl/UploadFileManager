package com.guxl.upload.common;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.springframework.web.multipart.MultipartFile;


public interface UploadManager {
	
	/**
	 * 设置上传文件所保存的文件系统的基本路径。所有上传的文件都保存在该基本路径下的子路径中。
	 * @param fsBasePath
	 */
	void setFsBasePath(String fsBasePath);
	
	
	/**
	 * 设置保存上传文件的基本路径所映射出来的虚拟路径。保存上传文件后同时会利用该参数生成文件访问的虚拟路径，以供后续下载
	 * @param urlBasePath
	 */
	void setUrlBasePath(String urlBasePath);
	
	
	/**
	 * 设置文件上传的大小限制。
	 * <p>注意：所允许上传的文件的大小，除了受该参数的限制外，还同时会受到web服务器（如内嵌的tomcat）的配置参数multipart.maxFileSize和multipart.maxRequestSize
	 * 的影响，对于springboot所内置的tomcat服务器，如果不设置这两个参数，则默认参数值为10Mb。
	 * @param sizeLimit 形式如：100kb，10mb等，单位只取“Kb”和“Mb”两种
	 */
	void setFileSizeLimit(String sizeLimit);
	
	
	/**
	 * 设置UploadManager保存文件时所使用的writer
	 * @param writer
	 */
	void setFileWriter(UploadedFileWriter writer);
	
	
	
	
	
	String getFsBasePath();
	String getUrlBasePath();
	String getSizeLimit();
	long getSizeLimitBytes();
	UploadedFileWriter getFileWriter();
	
	
	
	
	
	
	/**
	 * 保存上传文件的方法。指定所要保存的子目录及保存时所使用的文件名，并根据overwrite参数决定是否覆盖旧文件。
	 * @param multipartFile 上传的文件的MultipartFile对象
	 * @param subDir 文件要保存所在的子目录。文件最终将保存在 “/{fsBasePath}/{subDir}/” 目录下
	 * @param newFileName 保存文件时所使用的文件名称，可以不同于原始文件名称。注意，传递该参数不要包含扩展名部分。
	 * @param overwrite 如果要保存的路径下存在同名文件时，是否覆盖。如果为false，但存在同名文件时，该方法可能抛出SameFileNameExistException异常
	 * @return 包含上传文件保存信息的UploadedFileInfo对象
	 */
	UploadedFileInfo saveFile(MultipartFile multipartFile, String subDir, String newFileName, boolean overwrite) 
			throws IOException;
	
	
	/**
	 * 保存上传文件的方法。指定所要保存的子目录及保存时所使用的文件名，当子目录下存在同名文件时，默认覆盖旧文件。
	 * <p> 该方法默认实现为调用 saveFile(MultipartFile multipartFile, String subDir, String newFileName, true). 
	 * @param multipartFile 上传的文件的MultipartFile对象
	 * @param subDir 文件要保存所在的子目录。文件最终将保存在 “/{fsBasePath}/{subDir}/” 目录下
	 * @param newFileName 保存文件时所使用的文件名称，可以不同于原始文件名称。注意，传递该参数不要包含扩展名部分。
	 * @return 包含上传文件保存信息的UploadedFileInfo对象
	 */
	default UploadedFileInfo saveFile(MultipartFile multipartFile, String subDir, String newFileName) 
			throws IOException{
		return this.saveFile(multipartFile, subDir, newFileName, true);
	}
	
	
	
	/**
	 * 保存上传文件的方法。保存时使用的子目录和文件名称为程序随机生成。当子目录下存在同名文件时，默认覆盖旧文件。
	 * @param multipartFile 上传的文件的MultipartFile对象
	 * @return 包含上传文件保存信息的UploadedFileInfo对象
	 */
	UploadedFileInfo saveFile(MultipartFile multipartFile) 
			throws NoSuchAlgorithmException,IOException;
	
	
	
	/**
	 * @param fullPathFileName 要删除的已上传的文件的完整的路径
	 */
	void removeFile(String fullPathFileName) throws IOException;
	
	
	/**
	 * @param fileInfo 要删除的已上传的文件的UploadedFileInfo对象
	 */
	default void removeFile(UploadedFileInfo fileInfo) throws IOException {
		this.removeFile(fileInfo.getFilePath());
	}
	
	
}
