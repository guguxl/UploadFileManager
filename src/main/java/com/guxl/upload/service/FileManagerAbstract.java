package com.guxl.upload.service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;

import org.springframework.web.multipart.MultipartFile;

import com.google.common.base.Splitter;

/**
 * 上传文件管理类的基类。
 * <p> 
 * osBasePath：保存上传文件的基本路径。上传的文件以该路径为基路径，（可能）在该目录下创建子目录，最终保存在创建的子目录中。
 * <br> 
 * urlBasePath：保存上传文件的基本路径所映射出来的虚拟路径。该路径后续可以作为下载文件时url的基本路径。
 * @author guxl
 */
public abstract class FileManagerAbstract implements FileManagerInterface{

	private String osBasePath;
	private String urlBasePath;
	
	public final String getOsBasePath(){
		return this.osBasePath;
	}
	
	public final String getUrlBasePath(){
		return this.urlBasePath;
	}
	
	
	//如果{osBasePath/subDir/}目录不存在，在自动创建subDir
	//支持subDir为“foo/bar/baz/”多级目录形式
	public final void createSubDirIfNotExist(String subDir) throws IOException{
		String dir=this.osBasePath;
		if(!this.osBasePath.endsWith("/")){
			dir=dir+"/";
		}
		this.createDirIfNotExist(dir);
		
		Iterable<String> subDirIterable=Splitter.on("/").trimResults().omitEmptyStrings().split(subDir);
		Iterator<String> iter=subDirIterable.iterator();
		while(iter.hasNext()){
			dir=dir+iter.next()+"/";
			this.createDirIfNotExist(dir);
		}
	}
	
	
	//保存文件的模板实现
	public final FileInfo saveFile(MultipartFile file, boolean remainOriginName, boolean overwrite, long sizeLimit) 
			throws SameFileNameExistException,FileSizeExceedLimitException,IOException{
		//如果有大小限制，且超过大小限制
		if(sizeLimit!=-1 && file.getSize()>sizeLimit){
			throw new FileSizeExceedLimitException();
		}
		
		//
		
		
		FileInfo fileInfo=new FileInfo();
		fileInfo.setFileName(file.getOriginalFilename());
		fileInfo.setFileSize(file.getSize());
		
		return new FileInfo();
	}
	
	
	//删除文件的模板实现
	public void removeFile(FileInfo fileInfo){
		
	}
	
	public FileManagerAbstract() {
	}
	
	
	//如果路径不存在，则创建路径
	private void createDirIfNotExist(String dir) throws IOException{
		if (!Files.exists(Paths.get(dir))){
			Files.createDirectories(Paths.get(dir));
		}
	}
	
	
	//将MultipartFile文件按照给定的文件名写入文件系统
	private void saveFile(MultipartFile file, String fsFileName) throws IOException{
		byte[] bytes = file.getBytes();
		BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(fsFileName)));
		stream.write(bytes);
		stream.close();
	}
	
	
	
	
	/**
	 * 设置上传文件保存的子目录，继承类通过实现该方法来达到不同的文件存放分布策略
	 * @param subDir
	 */
	abstract void setSubDir(String subDir);
	abstract String getSubDir();

}
