package com.deeplogics.mobilecloud.app;
 
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
 



import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
 
/**
 * Spring MongoDB configuration file
 * 
 */
@Configuration
public class SpringMongoConfig extends AbstractMongoConfiguration {
 
	@Bean
	public GridFsTemplate gridFsTemplate() throws Exception {
		return new GridFsTemplate(mongoDbFactory(), mappingMongoConverter());
	}
 
	@Override
	protected String getDatabaseName() {
		try {
			Properties prop = getAppProperties();
			return prop.getProperty("database.name");
		} catch (IOException e) {
			return null;
		}
	}
 
	
	@Bean
	@Override
	public Mongo mongo() throws Exception {
		Properties prop = getAppProperties();
		MongoClientURI uri = new MongoClientURI(prop.getProperty("spring.data.mongodb.uri"));
		return new MongoClient(uri);
	}
	
	public Properties getAppProperties() throws IOException{
		Properties prop = new Properties();
		String propFileName = "application.properties";
		
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
		 
		if (inputStream != null) {
			prop.load(inputStream);
		} else {
			throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
		}
		
		return prop;
	}
 
}
