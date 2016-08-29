package com.guxl.upload.common;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.regex.Pattern;

import com.google.common.base.Preconditions;

import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import com.guxl.upload.common.exception.FileSizeExceedLimitException;
import com.guxl.upload.common.exception.SameFileNameExistException;


/**
 * 用于上传文件管理的简单实现类。该类定义了几个属性：
 * <br> fsBasePath：用于保存文件上传的文件系统完整路径。
 * <br> urlBasePath：将fsBasePath路径映射出可供web访问的虚拟路径，后续通过浏览器访问文件时将会使用到该参数。
 * <br> sizeLimit和sizeLimitBytes：上传文件的大小，只接受kb或mb单位的设值。
 * <br> fileWriter：将上传的文件写入或删除的UploadedFileWriter的实现。
 * 
 * <p> 以上四个属性，除了fileWriter属性，在本程序中使用Autowired实现自动注入，其余三个属性留待更高层次的调用代码在使用时按需进行配置。
 * @author xiangligu
 *
 */
public class SimpleUploadManager implements UploadManager {

	private String fsBasePath;
	private String urlBasePath;
	private String sizeLimit;
	private long sizeLimitBytes;
	private UploadedFileWriter fileWriter;
	
	
	//================setters===================
	@Override
	public void setFsBasePath(String fsBasePath) {
		Preconditions.checkNotNull(fsBasePath, "参数fsBasePath不允许为null");
		Preconditions.checkArgument(fsBasePath.trim().length()>0, "参数fsBasePath不允许为空格或空字符串");
		this.fsBasePath = fsBasePath;
	}

	@Override
	public void setUrlBasePath(String urlBasePath) {
		Preconditions.checkNotNull(urlBasePath, "参数urlBasePath不允许为null");
		Preconditions.checkArgument(urlBasePath.trim().length()>0, "参数urlBasePath不允许为空格或空字符串");
		this.urlBasePath = urlBasePath;
	}

	@Override
	public void setFileSizeLimit(String sizeLimit) {
		Preconditions.checkNotNull(sizeLimit, "参数sizeLimit不允许为null");
		Preconditions.checkArgument(sizeLimit.trim().length()>0, "参数sizeLimit不允许为空格或空字符串");
		
		sizeLimit=sizeLimit.trim();
		String limitValue=sizeLimit.substring(0, sizeLimit.length()-2);
		String limitUnit=sizeLimit.substring(sizeLimit.length()-2);
		
		Preconditions.checkArgument(limitUnit.toLowerCase().equals("kb")||limitUnit.toLowerCase().equals("mb"), "参数sizeLimit单位应为Kb或Mb");
		Preconditions.checkArgument(Pattern.compile("^[0-9]+$").matcher(limitValue).matches() && Integer.parseInt(limitValue)>0, "参数sizeLimit的数值部分必须为正整数");
		
		this.sizeLimit = sizeLimit;
		if(limitUnit.toLowerCase().equals("kb")){
			this.sizeLimitBytes = Integer.parseInt(limitValue) * 1024;
		}else{
			this.sizeLimitBytes = Integer.parseInt(limitValue) * 1024 * 1024;
		}
	}

	
	/**
	 * 注意：该set方法使用了@Autowired注解，表示fileWriter不需要人为设置，将会由spring自动注入。且required=true，表示必须被注入。
	 */
	@Override
	@Autowired(required=true)
	public void setFileWriter(UploadedFileWriter writer) {
		Preconditions.checkNotNull(writer, "参数writer不允许为null");
		this.fileWriter = writer;
	}
	
	
	
	
	//=================getters====================
	public String getFsBasePath() {
		return this.fsBasePath;
	}

	public String getUrlBasePath() {
		return this.urlBasePath;
	}

	public String getSizeLimit() {
		return this.sizeLimit;
	}

	public long getSizeLimitBytes() {
		return this.sizeLimitBytes;
	}

	public UploadedFileWriter getFileWriter() {
		return this.fileWriter;
	}
	
	
	
	
	//==============save and remove implementation================
	@Override
	public UploadedFileInfo saveFile(MultipartFile multipartFile, String subDir, String newFileName, boolean overwrite)
			throws IOException{
		Preconditions.checkNotNull(multipartFile, "参数multipartFile不允许为null");
		Preconditions.checkNotNull(subDir, "参数subDir不允许为null");
		Preconditions.checkArgument(subDir.trim().length()>0, "参数subDir不允许为空格或空字符串");
		Preconditions.checkNotNull(newFileName, "参数newFileName不允许为null");
		Preconditions.checkArgument(newFileName.trim().length()>0, "参数newFileName不允许为空格或空字符串");
		
		if(multipartFile.getSize()>this.sizeLimitBytes){
			throw new FileSizeExceedLimitException();
		}
		
		String originFileName=multipartFile.getOriginalFilename();
		String fileType=FileSystemUtil.getFileExtent(originFileName);
		
		//判断fsBasePath下的subDir目录是否存在，不存在则创建
		String regularFullPath=FileSystemUtil.getRegularFullPath(this.fsBasePath + "/" + subDir.trim());
		if (!Files.exists(Paths.get(regularFullPath))){
			FileSystemUtil.createDirectoryIfNotExistRecursively(regularFullPath);
		}
		
		//如果要保存的路径下存在同名的文件，且不允许覆盖，则抛出异常
		String fullPathFileName = regularFullPath + newFileName.trim() + "." + fileType;
		if(Files.exists(Paths.get(fullPathFileName)) && !overwrite){
			throw new SameFileNameExistException();
		}
		
		this.fileWriter.writeFile(multipartFile, fullPathFileName, overwrite);
		
		UploadedFileInfo fileInfo=new UploadedFileInfo();
		fileInfo.setFileName(originFileName);
		fileInfo.setFilePath(fullPathFileName);
		fileInfo.setFileSize(multipartFile.getSize());
		fileInfo.setFileType(fileType);
		fileInfo.setSaveTime(LocalDateTime.now());
		fileInfo.setUrlPath(FileSystemUtil.getRegularFullUrlPath(this.urlBasePath + "/" + subDir.trim()) + newFileName.trim() + "." + fileType);
		return fileInfo;
	}

	
	@Override
	public UploadedFileInfo saveFile(MultipartFile multipartFile)
			throws NoSuchAlgorithmException, IOException{
		String subDir=this.generateSubDirRandomly(multipartFile);
		String newFileName=this.generateFileNameWithUuid();
		return this.saveFile(multipartFile, subDir, newFileName, true);
	}

	
	@Override
	public void removeFile(String fullPathFileName) throws IOException {
		this.fileWriter.removeFile(fullPathFileName);
	}
	
	
	
	//==================private functions===================
	//根据文件上传时间、文件原始名称、文件大小，使用md5算法随机生成两位字符（a~z, A~Z, 0~9）组成的子目录
	private String generateSubDirRandomly(MultipartFile multipartFile) throws NoSuchAlgorithmException {
		MessageDigest digest=MessageDigest.getInstance("MD5");
		String randomSeedStr = LocalDateTime.now().toString() + multipartFile.getOriginalFilename() + multipartFile.getSize();
		String subDir=new String(Hex.encodeHex(digest.digest(randomSeedStr.getBytes()))).substring(0,2);
		return subDir;
	}
	
	//使用uuid算法随机生成字符串作为保存时使用的文件名称
	private String generateFileNameWithUuid(){
		return UUID.randomUUID().toString();
	}
	

}
