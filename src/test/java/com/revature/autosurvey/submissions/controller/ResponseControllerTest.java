package com.revature.autosurvey.submissions.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.revature.autosurvey.submissions.beans.Response;
import com.revature.autosurvey.submissions.beans.TrainingWeek;
import com.revature.autosurvey.submissions.service.ResponseService;
import com.revature.autosurvey.submissions.utils.Utilities;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
public class ResponseControllerTest {

	@TestConfiguration
	static class Configuration {
		@Bean
		public ResponseController getResponseController(ResponseService responseService, Utilities utilities) {
			ResponseController responseController = new ResponseController();
			responseService.setUtilities(utilities);
			responseController.setResponceService(responseService);
			return responseController;
		}
	}

	@Autowired
	private ResponseController responseController;

	@MockBean
	private ResponseService responseService;

	@MockBean
	private Utilities util;

	@Test
	void testGetResponse() {
		UUID id = UUID.randomUUID();
		when(responseService.getResponse(id)).thenReturn(Mono.just(new Response()));
		StepVerifier.create(responseController.getResponses(Optional.empty(), Optional.empty(), Optional.of(id)))
				.expectNext(ResponseEntity.ok().body(new Response())).expectComplete().verify();
	}

	@Test
	void testGetEmptyResponse() {
		UUID id = UUID.randomUUID();
		when(responseService.getResponse(id)).thenReturn(Mono.empty());
		StepVerifier.create(responseController.getResponses(Optional.empty(), Optional.empty(), Optional.of(id)))
				.expectNext(ResponseEntity.notFound().build()).expectComplete().verify();
	}

	@Test
	void testGetErrorResponse() {
		UUID id = UUID.randomUUID();
		when(responseService.getResponse(id)).thenReturn(Mono.error(new Exception()));
		StepVerifier.create(responseController.getResponses(Optional.empty(), Optional.empty(), Optional.of(id)))
				.expectNext(ResponseEntity.badRequest().body(new Response())).expectComplete().verify();
	}

	@Test
	void testUpdateResponse() {
		UUID id = UUID.randomUUID();
		Response response = new Response();
		when(responseService.updateResponse(id, response)).thenReturn(Mono.just(new Response()));
		StepVerifier.create(responseController.updateResponse(id, response))
				.expectNext(ResponseEntity.ok().body(new Response())).expectComplete().verify();
	}

	@Test
	void testUpdateResponseThatDoesNotExist() {
		UUID id = UUID.randomUUID();
		Response response = new Response();
		when(responseService.updateResponse(id, response)).thenReturn(Mono.error(new Exception()));
		StepVerifier.create(responseController.updateResponse(id, response))
				.expectNext(ResponseEntity.badRequest().body(new Response())).expectComplete().verify();
	}

	@Test
	void testDeleteResponse() {
		Response response = new Response();

		when(responseService.deleteResponse(any())).thenReturn(Mono.just(response));

		Mono<ResponseEntity<Object>> result = responseController.deleteResponse(null);

		StepVerifier.create(result).expectNext(ResponseEntity.noContent().build()).verifyComplete();
	}

	@Test
	void testGetAllResponsesByBatch() {
		System.out.println("I'm in the batch test");
		Response testResponse1 = new Response();
		Response testResponse2 = new Response();
		Optional<String> testBatch = Optional.of("Batch 23");
		testResponse1.setBatch(testBatch.get());
		testResponse2.setBatch(testBatch.get());
		when(responseService.getResponsesByBatch(testBatch.get())).thenReturn(Flux.just(testResponse1, testResponse2));
		StepVerifier.create(responseController.getResponses(testBatch, Optional.empty(), Optional.empty()))
				.expectNext(ResponseEntity.ok(testResponse1)).expectNext(ResponseEntity.ok(testResponse2))
				.verifyComplete();
	}

	@Test
	void testGetAllResponsesByWeek() {
		Response testResponse1 = new Response();
		Response testResponse2 = new Response();
		testResponse1.setWeek(TrainingWeek.EIGHT);
		testResponse2.setWeek(TrainingWeek.EIGHT);
		System.out.println("I'm in the week test");
		when(responseService.getResponsesByWeek(any())).thenReturn(Flux.just(testResponse1, testResponse2));
		StepVerifier.create(responseController.getResponses(Optional.empty(), Optional.of("Week 8"), Optional.empty()))
				.expectNext(ResponseEntity.ok(testResponse1)).expectNext(ResponseEntity.ok(testResponse2))
				.verifyComplete();
	}

	@Test
	void testGetAllResponsesByBatchAndWeek() {
		System.out.println("I'm in the batchweek test");
		Response testResponse1 = new Response();
		Response testResponse2 = new Response();
		Optional<String> testBatch = Optional.of("Batch 23");
		testResponse1.setBatch(testBatch.get());
		testResponse2.setBatch(testBatch.get());
		testResponse1.setWeek(TrainingWeek.EIGHT);
		testResponse2.setWeek(TrainingWeek.EIGHT);
		when(responseService.getResponsesByBatchForWeek(any(), any()))
				.thenReturn(Flux.just(testResponse1, testResponse2));
		StepVerifier.create(responseController.getResponses(testBatch, Optional.of("Week 8"), Optional.empty()))
				.expectNext(ResponseEntity.ok(testResponse1)).expectNext(ResponseEntity.ok(testResponse2))
				.verifyComplete();
	}

}
