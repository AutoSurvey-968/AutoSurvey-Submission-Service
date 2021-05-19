package com.revature.autosurvey.submissionservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class SubmissionserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SubmissionserviceApplication.class, args);
	}

}
