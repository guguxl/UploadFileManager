package com.guxl.upload.app;

import java.util.Hashtable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.guxl.upload.common.UploadManager;

@RestController
public class TestController {
	@Autowired
	UploadManager uploadManager;
	
	@RequestMapping(path="/test")
	public Hashtable<String, Object> test(){
		Hashtable<String, Object> result=new Hashtable<>();
		
		result.put("fsBasePath", uploadManager.getFsBasePath());
		result.put("urlBasePath", uploadManager.getUrlBasePath());
		result.put("sizeLimit", uploadManager.getSizeLimit());
		result.put("sizeLimitBytes", (Long)uploadManager.getSizeLimitBytes());
		result.put("writer inited", uploadManager.getFileWriter()!=null);
		
		return result;
	}
	
}
