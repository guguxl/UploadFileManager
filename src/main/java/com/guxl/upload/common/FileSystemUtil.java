package com.guxl.upload.common;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

public class FileSystemUtil {

	/**
	 * 获取当前java程序所运行在的操作系统名称
	 * @return
	 */
	public final static String getOsName(){
		return System.getProperties().getProperty("os.name");
	}
	
	
	/**
	 * 判断当前操作系统是否为微软公司的windows操作系统，是返回true，不是返回false
	 * @return
	 */
	public final static boolean isMicrosoftWindows(){
		String osName = FileSystemUtil.getOsName();
		if(osName.toUpperCase().indexOf("WINDOWS") != -1){
			return true;
		}else{
			return false;
		}
	}
	
	
	
	/**
	 * 获取文件扩展名。截取文件名中最后一个“.”分割的右侧部分字符串。如果文件名中不包含“.”，则返回空串
	 * @param fileName 不包含路径的文件名称
	 * @return 文件扩展名
	 */
	public final static String getFileExtent(String fileName){
		Preconditions.checkNotNull(fileName,"参数fileName不允许为null");
		Preconditions.checkArgument(fileName.trim().length()>0, "参数fileName不允许为空格或空字符串");
		
		String[] nameSplit = fileName.trim().split("\\.");
		String extName = nameSplit.length > 0 ? nameSplit[nameSplit.length - 1] : "";
		return extName;
	}
	
	
	
	/**
	 * 将windows系统中的路径中的反斜杠转为unix系统路径形式的斜杠。本接口已提供default实现。
	 * @param windowsPath windows系统格式的路径
	 * @return unix系统格式的路径
	 */
	public final static String convertWindowsPathToUnixPath(String windowsPath){
		Preconditions.checkNotNull(windowsPath,"参数windowsPath不允许为null");
		return windowsPath.replace("\\", "/");
	}
	
	
	
	/**
	 * 将绝对路径参数进行格式化，主要是删除目录名称中包含的首尾空格，对于连续的多个路径分隔符（“/”或“\”）只当成一个处理，最后返回规范的绝对路径
	 * @param fullPath 待处理的绝对路径
	 * @return 处理后的绝对路径
	 */
	public final static String getRegularFullPath(String fullPath){
		ArrayList<String> pathPartList=FileSystemUtil.getFullPathParts(fullPath);
		String pathSplitChar=FileSystemUtil.getPathSplitChar();
		
		String tempStr=Joiner.on(pathSplitChar).skipNulls().join(pathPartList);
		if(FileSystemUtil.isMicrosoftWindows()){
			return tempStr + pathSplitChar;
		}else{
			return pathSplitChar + tempStr + pathSplitChar;
		}
	}
	
	
	
	/**
	 * 将映射的网络路径参数进行格式化，主要是删除路径名称中包含的首尾空格，对于连续的多个路径分隔符（“/”）只当成一个处理，最后返回规范的绝对虚拟路径
	 * @param fullUrlPath 待处理的绝对的虚拟路径
	 * @return 处理后的绝对虚拟路径
	 */
	public final static String getRegularFullUrlPath(String fullUrlPath){
		ArrayList<String> pathPartList=FileSystemUtil.getFullPathParts(fullUrlPath);
		String tempStr=Joiner.on("/").skipNulls().join(pathPartList);
		return "/" + tempStr + "/";
	}
	
	
	
	/**
	 * 当文件系统中不存在fullPathDir指定的目录时，则创建该目录。注意，该函数假设上级目录存在。
	 * <br>假设，fullPathDir为“/data/foo/bar/”，则该函数假设“/data/foo/”已经存在。
	 * @param fullPathDir 待创建的（包含完整路径的）目录名称
	 * @throws IOException
	 */
	public final static void createDirectoryIfNotExist(String fullPathDir) throws IOException{
		Preconditions.checkNotNull(fullPathDir, "参数fullPathDir不允许为null");
		Preconditions.checkArgument(fullPathDir.trim().length()>0, "参数fullPathDir不允许为空格或空字符串");
		
		if (!Files.exists(Paths.get(fullPathDir.trim()))){
			Files.createDirectories(Paths.get(fullPathDir.trim()));
		}
	}
	
	
	
	/**
	 * 当文件系统中不存在fullPathDir指定的目录时，则创建该目录。当上级目录也不存在，该函数会先创建上级目录。
	 * @param fullPathDir 待创建的（包含完整路径的）目录名称
	 * @throws IOException
	 */
	public final static void createDirectoryIfNotExistRecursively(String fullPathDir) throws IOException{
		String pathSplitChar=FileSystemUtil.getPathSplitChar();
		ArrayList<String> pathPartList=FileSystemUtil.getFullPathParts(fullPathDir);
		
		String tempDirStr="";
		//处理根路径
		if(FileSystemUtil.isMicrosoftWindows()){
			//根路径形式：“盘符:\”，如：“c:\”
			tempDirStr = pathPartList.get(0) + pathSplitChar;
		}else{
			//根路径形式：“/根目录/”，如：“/root/”
			tempDirStr = pathSplitChar + pathPartList.get(0) + pathSplitChar;
		}
		FileSystemUtil.createDirectoryIfNotExist(tempDirStr);
		
		//处理根路径以下的各级目录
		for(int i=1; i<pathPartList.size(); i++){
			tempDirStr = tempDirStr + pathPartList.get(i) + pathSplitChar;
			FileSystemUtil.createDirectoryIfNotExist(tempDirStr);
		}
		
	}

	
	
	/**
	 * 根据不同的系统类型，返回系统中路径所使用的分隔符是正斜杠还是反斜杠
	 * @return
	 */
	private final static String getPathSplitChar(){
		if(FileSystemUtil.isMicrosoftWindows()){
			return "\\";
		}else{
			return "/";
		}
	}
	
	
	/**
	 * 将绝对路径拆分成逐级目录名称，方便后续使用，各级目录名称按顺序存放在ArrayList中。
	 * <p> 拆分过程中对于目录名称中包含的前后的空格进行删除，对于连续两个路径分隔符（“/”或“\”）也忽略处理，只当成一个路径分隔符
	 * @param fullPath 待拆分的绝对路径
	 * @return 拆分后的各级目录名称构成的数组列表
	 */
	private final static ArrayList<String> getFullPathParts(String fullPath){
		Preconditions.checkNotNull(fullPath, "参数fullPath不允许为null");
		Preconditions.checkArgument(fullPath.trim().length()>0, "参数fullPath不允许为空格或空字符串");
		
		String tempPath=FileSystemUtil.convertWindowsPathToUnixPath(fullPath);
		ArrayList<String> pathPartList=Lists.newArrayList(Splitter.on("/").trimResults().omitEmptyStrings().split(tempPath));
		if(pathPartList.size()==0){
			throw new IllegalArgumentException("参数fullPath："+fullPath+"不是合法的路径");
		}
		return pathPartList;
	}
	
	
	
	
}
