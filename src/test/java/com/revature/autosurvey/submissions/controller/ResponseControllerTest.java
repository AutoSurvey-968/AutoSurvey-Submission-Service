package com.revature.autosurvey.submissions.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
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
	void testGetResponse() {
		UUID id = UUID.randomUUID();
		when(responseService.getResponse(id)).thenReturn(Mono.just(new Response()));
		StepVerifier.create(responseController.getResponses(null, null, id))
			.expectNext(ResponseEntity.ok().body(new Response()))
			.expectComplete()
			.verify();
	}
	
	@Test
	void testGetEmptyResponse() {
		UUID id = UUID.randomUUID();
		when(responseService.getResponse(id)).thenReturn(Mono.empty());
		StepVerifier.create(responseController.getResponses(null, null, id))
			.expectNext(ResponseEntity.notFound().build())
			.expectComplete()
			.verify();
	}
	
	@Test
	void testGetErrorResponse() {
		UUID id = UUID.randomUUID();
		when(responseService.getResponse(id)).thenReturn(Mono.error(new Exception()));
		StepVerifier.create(responseController.getResponses(null, null, id))
			.expectNext(ResponseEntity.badRequest().body(new Response()))
			.expectComplete()
			.verify();
	}
	
	@Test
	void testUpdateResponse() {
		UUID id = UUID.randomUUID();
		Response response = new Response();
		when(responseService.updateResponse(id, response)).thenReturn(Mono.just(new Response()));
		StepVerifier.create(responseController.updateResponse(id, response))
		.expectNext(ResponseEntity.ok().body(new Response()))
		.expectComplete()
		.verify();
	}
	
	@Test
	void testUpdateResponseThatDoesNotExist() {
		UUID id = UUID.randomUUID();
		Response response = new Response();
		when(responseService.updateResponse(id, response)).thenReturn(Mono.error(new Exception()));
		StepVerifier.create(responseController.updateResponse(id, response))
		.expectNext(ResponseEntity.badRequest().body(new Response()))
		.expectComplete()
		.verify();
	}
	
	@Test
	void testDeleteResponse() {
		Response response = new Response();
		
		when(responseService.deleteResponse(any())).thenReturn(Mono.just(response));
		
		Mono<ResponseEntity<Object>> result = responseController.deleteResponse(null);
		
		StepVerifier.create(result)
				.expectNext(ResponseEntity.noContent().build())
				.verifyComplete();
	}
	
	@Test
	void testGetAllResponsesByBatch() {
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
	
	@Test
	void testAddResponsesCSV() {
		FilePart filePart = Mockito.mock(FilePart.class);
		UUID id = UUID.randomUUID();
		Flux<FilePart> fileFlux = Flux.just(filePart);
		when(responseService.addResponsesFromFile(fileFlux, id)).thenReturn(Flux.fromArray(new Response[]{new Response(), new Response(), new Response()}));
		StepVerifier.create(responseController.addResponses(fileFlux, id))
		.expectNext(ResponseEntity.ok().body(new Response()))
		.expectNext(ResponseEntity.ok().body(new Response()))
		.expectNext(ResponseEntity.ok().body(new Response()))
		.expectComplete()
		.verify();
	}
	
	@Test
	void testAddResponsesCSVError() {
		FilePart filePart = Mockito.mock(FilePart.class);
		UUID id = UUID.randomUUID();
		Flux<FilePart> fileFlux = Flux.just(filePart);
		when(responseService.addResponsesFromFile(fileFlux, id)).thenReturn(Flux.error(new Exception()));
		StepVerifier.create(responseController.addResponses(fileFlux, id))
		.expectNext(ResponseEntity.badRequest().body(new Response()))
		.expectComplete()
		.verify();
	}
	
	@Test
	void testAddResponsesFluxEmpty() {
		Flux<Response> emtpyFlux = Flux.empty();
		when(responseService.addResponses(emtpyFlux)).thenReturn(emtpyFlux);
		StepVerifier.create(responseController.addResponses(emtpyFlux))
		.expectComplete()
		.verify();
		
	}
	
	@Test
	void testAddResponsesFluxOneResponse() {
		Flux<Response> responseFlux = Flux.just(new Response());
		when(responseService.addResponses(responseFlux)).thenReturn(responseFlux);
		StepVerifier.create(responseController.addResponses(responseFlux))
		.expectNext(ResponseEntity.ok().body(new Response()))
		.expectComplete()
		.verify();
		
	}
	
	@Test
	void testAddResponsesFluxMultipleResponses() {
		Flux<Response> responseFlux = Flux.fromArray(new Response[] {new Response(), new Response(), new Response()});
		when(responseService.addResponses(responseFlux)).thenReturn(responseFlux);
		StepVerifier.create(responseController.addResponses(responseFlux))
		.expectNext(ResponseEntity.ok().body(new Response()))
		.expectNext(ResponseEntity.ok().body(new Response()))
		.expectNext(ResponseEntity.ok().body(new Response()))
		.expectComplete()
		.verify();
		
	}
	
	@Test
	void testAddResponsesFluxError() {
		Flux<Response> responseFlux = Flux.just(new Response());
		when(responseService.addResponses(responseFlux)).thenReturn(Flux.error(new Exception()));
		StepVerifier.create(responseController.addResponses(responseFlux))
		.expectNext(ResponseEntity.badRequest().body(new Response()))
		.expectComplete()
		.verify();
		
	}
	
}
