package com.dasa.splitspends;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.dasa.splitspends.config.AppProperties;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class SplitspendsApplication {
    
    

	public static void main(String[] args) {
		SpringApplication.run(SplitspendsApplication.class, args);
	}
}
