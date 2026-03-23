package com.cambio_earth.symbiosis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SymbiosisApplication {

	public static void main(String[] args) {
		SpringApplication.run(SymbiosisApplication.class, args);
	}

}
