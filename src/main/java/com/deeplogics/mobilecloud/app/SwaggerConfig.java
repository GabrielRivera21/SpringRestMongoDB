package com.deeplogics.mobilecloud.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mangofactory.swagger.configuration.SpringSwaggerConfig;
import com.mangofactory.swagger.models.GenericTypeNamingStrategy;
import com.mangofactory.swagger.models.dto.ApiInfo;
import com.mangofactory.swagger.plugin.EnableSwagger;
import com.mangofactory.swagger.plugin.SwaggerSpringMvcPlugin;

@Configuration
@EnableSwagger
public class SwaggerConfig {
	private SpringSwaggerConfig springSwaggerConfig;

	@Autowired
	public void setSpringSwaggerConfig(SpringSwaggerConfig springSwaggerConfig) {
		this.springSwaggerConfig = springSwaggerConfig;
	}

	@Bean 
	public SwaggerSpringMvcPlugin customImplementation(){
		return new SwaggerSpringMvcPlugin(this.springSwaggerConfig)
		.apiInfo(apiInfo())
		.genericTypeNamingStrategy(new SimpleGenericNamingStrategy())
		.includePatterns("/users.*");
	}

	private ApiInfo apiInfo() {
		ApiInfo apiInfo = new ApiInfo(
				"SpringMongoDB REST API",
				"SpringMongoDB.",
				"Guess What",
				"foo@example.com",
				"No Licence",
				"No Licence URL"
				);
		return apiInfo;
	}

	private class SimpleGenericNamingStrategy implements GenericTypeNamingStrategy {
		private final static String OPEN = "Of";
		private final static String CLOSE = "";
		private final static String DELIM = "And";

		@Override
		public String getOpenGeneric() {
			return OPEN;
		}

		@Override
		public String getCloseGeneric() {
			return CLOSE;
		}

		@Override
		public String getTypeListDelimiter() {
			return DELIM;
		}
	}
}
