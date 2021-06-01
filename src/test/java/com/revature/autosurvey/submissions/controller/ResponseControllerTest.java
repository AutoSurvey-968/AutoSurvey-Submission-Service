package com.revature.autosurvey.submissions.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;
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
import com.revature.autosurvey.submissions.beans.TrainingWeek;
import com.revature.autosurvey.submissions.service.ResponseService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
class ResponseControllerTest {

	@TestConfiguration
	static class Configuration {
		@Bean
		public ResponseController getResponseController(ResponseService responseService) {
			ResponseController responseController = new ResponseController();
			responseController.setResponseService(responseService);
			return responseController;
		}

		@Bean
		public ResponseService getService() {
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
		StepVerifier.create(responseController.getResponses(Optional.empty(), Optional.empty(), Optional.of(id)))
				.expectNext(new Response()).verifyComplete();
	}

	@Test
	void testGetEmptyResponse() {
		UUID id = UUID.randomUUID();
		when(responseService.getResponse(id)).thenReturn(Mono.empty());
		StepVerifier.create(responseController.getResponses(Optional.empty(), Optional.empty(), Optional.of(id)))
				.verifyComplete();
	}

	@Test
	void testGetErrorResponse() {
		UUID id = UUID.randomUUID();
		when(responseService.getResponse(id)).thenReturn(Mono.error(new Exception()));
		StepVerifier.create(responseController.getResponses(Optional.empty(), Optional.empty(), Optional.of(id)))
				.expectError();
	}

	@Test
	void testUpdateResponse() {
		UUID id = UUID.randomUUID();
		Response response = new Response();
		response.setUuid(id);
		when(responseService.updateResponse(id, response)).thenReturn(Mono.just(new Response()));
		StepVerifier.create(responseController.updateResponse(id, response))
				.expectNext(ResponseEntity.ok().body(new Response())).expectComplete().verify();
	}

	@Test
	void testUpdateResponseThatDoesNotExist() {
		UUID id = UUID.randomUUID();
		Response response = new Response();
		response.setUuid(id);
		when(responseService.updateResponse(id, response)).thenReturn(Mono.error(new Exception()));
		StepVerifier.create(responseController.updateResponse(id, response))
				.expectNext(ResponseEntity.notFound().build()).expectComplete().verify();
	}
	@Test
	void testUpdateResponseNotMatchingUuids() {
		UUID id = UUID.randomUUID();
		Response response = new Response();
		response.setUuid(UUID.randomUUID());
		when(responseService.updateResponse(id, response)).thenReturn(Mono.error(new Exception()));
		StepVerifier.create(responseController.updateResponse(id, response))
				.expectNext(ResponseEntity.badRequest().build()).expectComplete().verify();
	}
	@Test
	void testUpdateResponseNullId() {
		UUID id = UUID.randomUUID();
		Response response = new Response();
		response.setUuid(UUID.randomUUID());
		when(responseService.updateResponse(null, response)).thenReturn(Mono.error(new Exception()));
		StepVerifier.create(responseController.updateResponse(id, response))
				.expectNext(ResponseEntity.badRequest().build()).expectComplete().verify();
	}
	@Test
	void testUpdateResponseNullResponse() {
		UUID id = UUID.randomUUID();
		Response response = new Response();
		response.setUuid(UUID.randomUUID());
		when(responseService.updateResponse(id, null)).thenReturn(Mono.error(new Exception()));
		StepVerifier.create(responseController.updateResponse(id, response))
				.expectNext(ResponseEntity.badRequest().build()).expectComplete().verify();
	}
	@Test
	void testUpdateResponseNullResponseId() {
		UUID id = UUID.randomUUID();
		Response response = new Response();
		when(responseService.updateResponse(id, response)).thenReturn(Mono.error(new Exception()));
		StepVerifier.create(responseController.updateResponse(id, response))
				.expectNext(ResponseEntity.badRequest().build()).expectComplete().verify();
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
		Response testResponse1 = new Response();
		Response testResponse2 = new Response();
		Optional<String> testBatch = Optional.of("Batch 23");
		testResponse1.setBatch(testBatch.get());
		testResponse2.setBatch(testBatch.get());
		when(responseService.getResponsesByBatch(testBatch.get())).thenReturn(Flux.just(testResponse1, testResponse2));
		StepVerifier.create(responseController.getResponses(testBatch, Optional.empty(), Optional.empty()))
				.expectNext(testResponse1, testResponse2).verifyComplete();
	}

	@Test
	void testGetAllResponsesByWeek() {
		Response testResponse1 = new Response();
		Response testResponse2 = new Response();
		testResponse1.setWeek(TrainingWeek.EIGHT);
		testResponse2.setWeek(TrainingWeek.EIGHT);
		when(responseService.getResponsesByWeek(any())).thenReturn(Flux.just(testResponse1, testResponse2));
		StepVerifier.create(responseController.getResponses(Optional.empty(), Optional.of("Week 8"), Optional.empty()))
				.expectNext(testResponse1, testResponse2).verifyComplete();
	}

	@Test
	void testAddResponsesCSV() {
		FilePart filePart = Mockito.mock(FilePart.class);
		UUID id = UUID.randomUUID();
		String idString = id.toString();
		Flux<FilePart> fileFlux = Flux.just(filePart);
		when(responseService.addResponsesFromFile(fileFlux, id))
				.thenReturn(Flux.fromArray(new Response[] { new Response(), new Response(), new Response() }));
		Flux<Response> body = responseController.addResponses(fileFlux, idString).getBody();
		StepVerifier.create(body)
			.expectNext(new Response())
			.expectNext(new Response())
			.expectNext(new Response()).expectComplete().verify();
	}

	@Test
	void testAddResponsesCSVError() {
		FilePart filePart = Mockito.mock(FilePart.class);
		UUID id = UUID.randomUUID();
		String idString = id.toString();
		Flux<FilePart> fileFlux = Flux.just(filePart);
		when(responseService.addResponsesFromFile(fileFlux, id)).thenReturn(Flux.error(new Exception()));
		Flux<Response> body = responseController.addResponses(fileFlux, idString).getBody();
		StepVerifier.create(body).verifyError();
	}

	@Test
	void testAddResponsesFluxEmpty() {
		Flux<Response> emtpyFlux = Flux.empty();
		when(responseService.addResponsesWithUuids(emtpyFlux)).thenReturn(emtpyFlux);
		StepVerifier.create(responseController.addResponses(emtpyFlux)).expectComplete().verify();

	}

	@Test
	void testAddResponsesFluxOneResponse() {
		Flux<Response> responseFlux = Flux.just(new Response());
		when(responseService.addResponsesWithUuids(responseFlux)).thenReturn(responseFlux);
		StepVerifier.create(responseController.addResponses(responseFlux)).expectNext(new Response()).verifyComplete();

	}

	@Test
	void testAddResponsesFluxMultipleResponses() {
		Flux<Response> responseFlux = Flux.fromArray(new Response[] { new Response(), new Response(), new Response() });
		when(responseService.addResponsesWithUuids(responseFlux)).thenReturn(responseFlux);
		StepVerifier.create(responseController.addResponses(responseFlux)).expectNext(new Response())
				.expectNext(new Response()).expectNext(new Response()).verifyComplete();

	}

	@Test
	void testAddResponsesFluxError() {
		Flux<Response> responseFlux = Flux.just(new Response());
		when(responseService.addResponsesWithUuids(responseFlux)).thenReturn(Flux.error(new Exception()));
		StepVerifier.create(responseController.addResponses(responseFlux)).expectError().verify();

	}

	@Test
	void testGetAllResponsesByBatchAndWeek() {
		Response testResponse1 = new Response();
		Response testResponse2 = new Response();
		Optional<String> testBatch = Optional.of("Batch 23");
		testResponse1.setBatch(testBatch.get());
		testResponse2.setBatch(testBatch.get());
		testResponse1.setWeek(TrainingWeek.EIGHT);
		testResponse2.setWeek(TrainingWeek.EIGHT);
		when(responseService.getResponsesByBatchAndWeek(any(), any()))
				.thenReturn(Flux.just(testResponse1, testResponse2));
		StepVerifier.create(responseController.getResponses(testBatch, Optional.of("Week 8"), Optional.empty()))
				.expectNext(testResponse1, testResponse2).verifyComplete();
	}

	@Test
	void testGetAllResponsesGetsResponses() {
		Response testResponse = new Response();
		when(responseService.getAllResponses()).thenReturn(Flux.just(testResponse));
		StepVerifier.create(responseController.getResponses(Optional.empty(), Optional.empty(), Optional.empty()))
				.expectNextCount(1L);
	}

}
