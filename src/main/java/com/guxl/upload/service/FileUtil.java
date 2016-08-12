package com.guxl.upload.service;


public class FileUtil {

	//取文件扩展名
	public static String getFileExtent(String fileName) {
		String[] nameSplit = fileName.split("\\.");
		String extName = nameSplit.length > 0 ? nameSplit[nameSplit.length - 1] : "";
		return extName;
	}
	
}
