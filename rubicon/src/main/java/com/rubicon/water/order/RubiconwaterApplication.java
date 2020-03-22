package com.rubicon.water.order;

import org.quartz.SchedulerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication	
public class RubiconwaterApplication {
	SchedulerFactory stdSchedulerFactory;
	public static void main(String[] args) {
		SpringApplication.run(RubiconwaterApplication.class, args);
		System.out.println("Hello Rubicon");
	}		
}