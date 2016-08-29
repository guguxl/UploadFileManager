package com.guxl.upload.common;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import com.google.common.base.Preconditions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.guxl.upload.common.exception.SameFileNameExistException;


@Component
@Qualifier("simpleUploadedFileWriter")
public class SimpleUploadedFileWriter implements UploadedFileWriter {

	
	@Override
	public void writeFile(MultipartFile multipartFile, String pathFileName, boolean overwrite)
			throws IOException{
		Preconditions.checkNotNull(multipartFile, "参数multipartFile不允许为null");
		Preconditions.checkNotNull(pathFileName, "参数pathFileName不允许为null");
		Preconditions.checkArgument(pathFileName.trim().length()>0, "参数pathFileName不允许为空格或空字符串");
		
		//if file exists and overwriting not allowed, throw customized exception
		if(!overwrite && Files.exists(Paths.get(pathFileName))){
			throw new SameFileNameExistException();
		}
		
		Files.write(Paths.get(pathFileName), 
					multipartFile.getBytes(), 
					StandardOpenOption.WRITE, 
					StandardOpenOption.TRUNCATE_EXISTING, 
					StandardOpenOption.CREATE);
	}
	
	
	@Override
	public void removeFile(String pathFileName) throws IOException {
		Preconditions.checkNotNull(pathFileName, "参数pathFileName不允许为null");
		Preconditions.checkArgument(pathFileName.trim().length()>0, "参数pathFileName不允许为空格或空字符串");
		
		Files.deleteIfExists(Paths.get(pathFileName));
	}

}
