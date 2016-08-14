package com.guxl.upload.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.guxl.upload.UploadApplication;
import com.guxl.upload.common.FileSystemUtil;
import com.guxl.upload.common.SimpleUploadManager;


/**
 * 配置SimpleUploadManager对象的 java Config类。
 * @author xiangligu
 *
 */
@Configuration
@ComponentScan(basePackageClasses = {UploadApplication.class})
public class UploadManagerConfiguration extends WebMvcConfigurerAdapter{
	
	@Autowired
	Environment env;
	
	
	/**
	 * 获取系统环境变量，用来实例化SimpleUploadManager对象，供后续代码注入或自动绑定。
	 * @return
	 */
	@Bean
	@Qualifier("simpleUploadManager")
	public SimpleUploadManager getUploadManager(){
		SimpleUploadManager simpleUploadManager=new SimpleUploadManager();
		
		String fsBasePath=this.getRegularFsBasePath();
		String urlBasePath=this.getRegularUrlBasePath();
		String sizeLimit=env.getProperty("upload.sizeLimit", "10Mb");
		
		simpleUploadManager.setFileSizeLimit(sizeLimit);
		simpleUploadManager.setFsBasePath(fsBasePath);
		simpleUploadManager.setUrlBasePath(urlBasePath);
		
		return simpleUploadManager;
	}
	
	
	//将文件系统路径fsBasePath映射到虚拟路径urlBasePath
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry
			.addResourceHandler(this.getRegularUrlBasePath() + "**")
			.addResourceLocations("file:///" + this.getRegularFsBasePath());
	}
	
	
	//获取格式化后的fsBasePath
	private String getRegularFsBasePath(){
		String userDir=System.getProperty("user.dir");
		String fsBasePath=env.getProperty("upload.fsBasePath", userDir + "/uploaded/");
		return FileSystemUtil.getRegularFullPath(fsBasePath);
	}
	
	//获取格式化后的urlBasePath
	private String getRegularUrlBasePath(){
		String urlBasePath=env.getProperty("upload.urlBasePath", "/upload/");
		return FileSystemUtil.getRegularFullUrlPath(urlBasePath);
	}

}
