package com.revature.autosurvey.submissions.controller;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.intuit.karate.junit5.Karate.Test;
import com.revature.autosurvey.submissions.service.ResponseService;

@ExtendWith(SpringExtension.class)
public class ResponseControllerTest {
	
	@TestConfiguration
	static class Configuration {
		@Bean
		public ResponseController getResponseController(ResponseService responseService) {
			ResponseController responseController = new ResponseController();
			responseController.setResponceService(responseService);
			return responseController;
		}
		
		@Bean
		public ResponseService getResponseService() {
			return Mockito.mock(ResponseService.class);
		}
	}
	
	@Autowired
	private ResponseController responseController;
	@Autowired
	private ResponseService responseService;
	
	@Test
	public void testGetResponse() {
		
	}

}
