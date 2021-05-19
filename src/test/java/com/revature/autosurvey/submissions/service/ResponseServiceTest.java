package com.revature.autosurvey.submissions.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.revature.autosurvey.submissions.data.ResponseRepository;

@ExtendWith(SpringExtension.class)
public class ResponseServiceTest {
	
	@TestConfiguration
	static class Configuration {
		@Bean
		public ResponseService getResponseService(ResponseRepository responseRepository) {
			ResponseService responseService = new ResponseServiceImpl();
			responseService.setResponseRepository(responseRepository);
			return responseService;
		}
		
		@Bean
		public ResponseRepository getResponseRepository() {
			return Mockito.mock(ResponseRepository.class);
		}
	}
	
	@Autowired
	private ResponseService responseService;
	@Autowired
	private ResponseRepository responseRepository;

}
