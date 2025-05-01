package com.MTGCollectionApp.demo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * main method/starting point of application
 *
 * @author timmonsevan
 */

@SpringBootApplication
public class MtgCollectionApplication {

	public static void main(String[] args) {
		SpringApplication.run(MtgCollectionApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

}
