package com.guxl.upload.common;

import java.time.LocalDateTime;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class UploadedFileInfo {

	String fileName;			//文件名
	String fileType;			//文件类型(扩展名取lower)
	String filePath;			//保存的文件路径（含完整目录和文件名）
	String urlPath;				//保存的文件的url访问路径（含完整的虚拟路径和文件名），供后续下载使用
	Long   fileSize;			//文件大小(bytes)
	LocalDateTime saveTime;		//文件保存时间
	
	
	
	public String getFileName() {
		return fileName;
	}


	public void setFileName(String fileName) {
		this.fileName = fileName;
	}


	public String getFileType() {
		return fileType;
	}


	public void setFileType(String fileType) {
		this.fileType = fileType;
	}


	public String getFilePath() {
		return filePath;
	}


	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}


	public String getUrlPath() {
		return urlPath;
	}


	public void setUrlPath(String urlPath) {
		this.urlPath = urlPath;
	}


	public Long getFileSize() {
		return fileSize;
	}


	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}


	public LocalDateTime getSaveTime() {
		return saveTime;
	}


	public void setSaveTime(LocalDateTime saveTime) {
		this.saveTime = saveTime;
	}


	public UploadedFileInfo() {
	}
	

}
