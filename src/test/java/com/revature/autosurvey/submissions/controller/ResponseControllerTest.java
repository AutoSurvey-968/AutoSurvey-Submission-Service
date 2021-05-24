package com.revature.autosurvey.submissions.controller;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;


import com.revature.autosurvey.submissions.beans.Response;
import com.revature.autosurvey.submissions.service.ResponseService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

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
	@MockBean
	private ResponseService responseService;
	
	@Test
	public void testGetResponse() {
		UUID id = UUID.randomUUID();
		when(responseService.getResponse(id)).thenReturn(Mono.just(new Response()));
		StepVerifier.create(responseController.getResponses(null, null, id))
			.expectNext(ResponseEntity.ok().body(new Response()))
			.expectComplete()
			.verify();
	}
	
	@Test
	public void testGetErrorResponse() {
		UUID id = UUID.randomUUID();
		when(responseService.getResponse(id)).thenReturn(Mono.error(new Exception()));
		StepVerifier.create(responseController.getResponses(null, null, id))
			.expectNext(ResponseEntity.badRequest().body(new Response()))
			.expectComplete()
			.verify();
	}
	
	@Test
	public void testUpdateResponse() {
		UUID id = UUID.randomUUID();
		Response response = new Response();
		when(responseService.updateResponse(id, response)).thenReturn(Mono.just(new Response()));
		StepVerifier.create(responseController.updateResponse(id, response))
		.expectNext(ResponseEntity.ok().body(new Response()))
		.expectComplete()
		.verify();
	}
	
	@Test
	public void testUpdateResponseThatDoesNotExist() {
		UUID id = UUID.randomUUID();
		Response response = new Response();
		when(responseService.updateResponse(id, response)).thenReturn(Mono.error(new Exception()));
		StepVerifier.create(responseController.updateResponse(id, response))
		.expectNext(ResponseEntity.badRequest().body(new Response()))
		.expectComplete()
		.verify();
	}
	
	@Test
	public void testDeleteResponse() {
		Response response = new Response();
		
		when(responseService.deleteResponse(any())).thenReturn(Mono.just(response));
		
		Mono<ResponseEntity<Object>> result = responseController.deleteResponse(null);
		
		StepVerifier.create(result)
				.expectNext(ResponseEntity.noContent().build())
				.verifyComplete();
	}
	
	@Test
	public void testGetAllResponsesByBatch() {
		Response testResponse1 = new Response();
		Response testResponse2 = new Response();
		String testBatch = "Batch 23";
		testResponse1.setBatch(testBatch);
		testResponse2.setBatch(testBatch);
		String batch = "Batch 23";
		when(responseService.getResponsesByBatch(batch)).thenReturn(Flux.just(testResponse1, testResponse2));
		StepVerifier.create(responseController.getResponses(batch , null, null))
		.expectNext(ResponseEntity.ok(testResponse1))
		.expectNext(ResponseEntity.ok(testResponse2))
		.verifyComplete();
	}
}
