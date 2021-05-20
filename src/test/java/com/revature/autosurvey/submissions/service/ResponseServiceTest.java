package com.revature.autosurvey.submissions.service;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.revature.autosurvey.submissions.beans.Response;
import com.revature.autosurvey.submissions.data.ResponseRepository;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

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
	
	@Test
	public void sanityCheck() {
		assertTrue(true);
	}
	
	@Test
	public void testGetResponse() {
		UUID id = UUID.randomUUID();
		when(responseRepository.findById(id)).thenReturn(Mono.just(new Response()));
		StepVerifier.create(responseService.getResponse(id))
			.expectNext(new Response())
			.expectComplete()
			.verify();
	}
	
	@Test
	public void testGetResponseNoResponse() {
		UUID id = UUID.randomUUID();
		when(responseRepository.findById(id)).thenReturn(Mono.empty());
		StepVerifier.create(responseService.getResponse(id))
			.expectError()
			.verify();
	}
	
	@Test
	public void testUpdateResponseDoesNotExist() {
		UUID id = UUID.randomUUID();
		Response response = new Response();
		when(responseRepository.findById(id)).thenReturn(Mono.just(new Response()));
		when(responseRepository.save(response)).thenReturn(Mono.just(new Response()));
		StepVerifier.create(responseService.getResponse(id))
			.expectNext(new Response())
			.expectComplete()
			.verify();
	}
	
	@Test
	public void testUpdateResponseExists() {
		UUID id = UUID.randomUUID();
		Response response = new Response();
		when(responseRepository.findById(id)).thenReturn(Mono.empty());
		when(responseRepository.save(response)).thenReturn(Mono.just(new Response()));
		StepVerifier.create(responseService.getResponse(id))
			.expectError()
			.verify();
	}

}
