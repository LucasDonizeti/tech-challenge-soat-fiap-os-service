package com.techchallenge.os;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.techchallenge.os.infrastructure.persistence.gateway.feign")
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class OsApplication {

	public static void main(String[] args) {
		SpringApplication.run(OsApplication.class, args);
	}

}
