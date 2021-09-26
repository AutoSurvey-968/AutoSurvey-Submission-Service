package com.revature.autosurvey.submissions.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
	void testGetResponse() throws ParseException {
		UUID id = UUID.randomUUID();
		when(responseService.getResponse(id)).thenReturn(Mono.just(new Response()));
		StepVerifier.create(responseController.getResponses(Optional.empty(), Optional.empty(), Optional.of(id)))
				.expectNext(new Response()).verifyComplete();
	}

	@Test
	void testGetEmptyResponse() throws ParseException {
		UUID id = UUID.randomUUID();
		when(responseService.getResponse(id)).thenReturn(Mono.empty());
		StepVerifier.create(responseController.getResponses(Optional.empty(), Optional.empty(), Optional.of(id)))
				.verifyComplete();
	}

	@Test
	void testGetErrorResponse() throws ParseException {
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
		response.setUuid(id);
		when(responseService.updateResponse(id, response)).thenReturn(Mono.error(new Exception()));
		StepVerifier.create(responseController.updateResponse(null, response))
				.expectNext(ResponseEntity.badRequest().build()).expectComplete().verify();
	}

	@Test
	void testUpdateResponseNullResponse() {
		UUID id = UUID.randomUUID();
		Response response = new Response();
		response.setUuid(id);
		when(responseService.updateResponse(id, response)).thenReturn(Mono.error(new Exception()));
		StepVerifier.create(responseController.updateResponse(id, null))
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
	void testUpdateResponseNullIdAndResponse() {
		UUID id = UUID.randomUUID();
		Response response = new Response();
		when(responseService.updateResponse(id, response)).thenReturn(Mono.error(new Exception()));
		StepVerifier.create(responseController.updateResponse(null, null))
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
	void testGetAllResponsesByBatch() throws ParseException {
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
	void testGetAllResponsesByWeek() throws ParseException {
		Response testResponse1 = new Response();
		Response testResponse2 = new Response();
		Optional<String> testDate = Optional.of("2021-03-29");
		Date format = new SimpleDateFormat("yyyy-MM-dd").parse(testDate.get());
		testResponse1.setDate(format);
		testResponse2.setDate(format);
		when(responseService.getResponsesByWeek(any())).thenReturn(Flux.just(testResponse1, testResponse2));
		StepVerifier.create(responseController.getResponses(Optional.empty(), testDate, Optional.empty()))
				.expectNext(testResponse1, testResponse2).verifyComplete();
	}

	/*
	 * @Test void testAddResponsesCSV() { FilePart filePart =
	 * Mockito.mock(FilePart.class); UUID id = UUID.randomUUID(); String idString =
	 * id.toString(); Flux<FilePart> fileFlux = Flux.just(filePart);
	 * when(responseService.addResponsesFromFile(fileFlux, id))
	 * .thenReturn(Flux.fromArray(new Response[] { new Response(), new Response(),
	 * new Response() })); Flux<Response> body =
	 * responseController.addResponses(fileFlux, idString).getBody();
	 * StepVerifier.create(body).expectNext(new Response()).expectNext(new
	 * Response()).expectNext(new Response()) .expectComplete().verify(); }
	 * 
	 * @Test void testAddResponsesCSVError() { FilePart filePart =
	 * Mockito.mock(FilePart.class); UUID id = UUID.randomUUID(); String idString =
	 * id.toString(); Flux<FilePart> fileFlux = Flux.just(filePart);
	 * when(responseService.addResponsesFromFile(fileFlux,
	 * id)).thenReturn(Flux.error(new Exception())); Flux<Response> body =
	 * responseController.addResponses(fileFlux, idString).getBody();
	 * StepVerifier.create(body).verifyError(); }
	 * 
	 * @Test void testAddResponsesCSVNoFile() { UUID id = UUID.randomUUID(); String
	 * idString = id.toString(); ResponseEntity<Flux<Response>> body =
	 * responseController.addResponses(null, idString); assertEquals(400,
	 * body.getStatusCodeValue(),
	 * " responseController.addResponses(null, value) should return 400"); }
	 * 
	 * @Test void testAddResponsesCSVNoSurveyId() { FilePart filePart =
	 * Mockito.mock(FilePart.class); Flux<FilePart> fileFlux = Flux.just(filePart);
	 * ResponseEntity<Flux<Response>> body =
	 * responseController.addResponses(fileFlux, null); assertEquals(400,
	 * body.getStatusCodeValue(),
	 * " responseController.addResponses(value, null) should return 400"); }
	 */

	@Test
	void testAddResponsesFluxEmpty() {
		Flux<Response> emtpyFlux = Flux.empty();
		when(responseService.addResponses(emtpyFlux)).thenReturn(emtpyFlux);
		StepVerifier.create(responseController.addResponses(emtpyFlux)).expectComplete().verify();

	}

	@Test
	void testAddResponsesFluxOneResponse() {
		Flux<Response> responseFlux = Flux.just(new Response());
		when(responseService.addResponses(responseFlux)).thenReturn(responseFlux);
		StepVerifier.create(responseController.addResponses(responseFlux)).expectNext(new Response()).verifyComplete();

	}

	@Test
	void testAddResponsesFluxMultipleResponses() {
		Flux<Response> responseFlux = Flux.fromArray(new Response[] { new Response(), new Response(), new Response() });
		when(responseService.addResponses(responseFlux)).thenReturn(responseFlux);
		StepVerifier.create(responseController.addResponses(responseFlux)).expectNext(new Response())
				.expectNext(new Response()).expectNext(new Response()).verifyComplete();

	}

	@Test
	void testAddResponsesFluxError() {
		Flux<Response> responseFlux = Flux.just(new Response());
		when(responseService.addResponses(responseFlux)).thenReturn(Flux.error(new Exception()));
		StepVerifier.create(responseController.addResponses(responseFlux)).expectError().verify();

	}

	@Test
	void testGetAllResponsesByBatchAndDate() throws ParseException {
		Response testResponse1 = new Response();
		Response testResponse2 = new Response();
		Optional<String> testBatch = Optional.of("Batch 23");
		Optional<String> testDate = Optional.of("2021-03-29");
		Date format = new SimpleDateFormat("yyyy-MM-dd").parse(testDate.get());
		testResponse1.setBatch(testBatch.get());
		testResponse2.setBatch(testBatch.get());
		testResponse1.setDate(format);
		testResponse2.setDate(format);
		when(responseService.getResponsesByBatchAndWeek(any(), any()))
				.thenReturn(Flux.just(testResponse1, testResponse2));
		StepVerifier.create(responseController.getResponses(testBatch, testDate, Optional.empty()))
				.expectNext(testResponse1, testResponse2).verifyComplete();
	}

	@Test
	void testGetAllResponsesGetsResponses() throws ParseException {
		Response testResponse = new Response();
		when(responseService.getAllResponses()).thenReturn(Flux.just(testResponse));
		StepVerifier.create(responseController.getResponses(Optional.empty(), Optional.empty(), Optional.empty()))
				.expectNextCount(1L);
	}
	
	@Test
	void addResponsesFromFile() {
		UUID id = UUID.randomUUID();
		FilePart filePart = Mockito.mock(FilePart.class);
		Response testResponse = new Response();
		
		when(responseService.addResponsesFromFile(Flux.just(filePart), id)).thenReturn(Flux.just(testResponse));
		
		Mono<ResponseEntity<Flux<Response>>> monoResponse = responseController.addResponses(Flux.just(filePart), id.toString());
		
		StepVerifier.create(monoResponse).expectNextMatches(res -> res.getStatusCodeValue() == 200).verifyComplete();
	}
	
	@Test
	void addResponsesFromFileNullFile() {
		UUID id = UUID.randomUUID();
		
		Mono<ResponseEntity<Flux<Response>>> monoResponse = responseController.addResponses(null, id.toString());
		
		StepVerifier.create(monoResponse).expectNextMatches(res -> res.getStatusCodeValue() == 400).verifyComplete();
	}
	
	@Test
	void addResponsesFromFileNullId() {
		FilePart filePart = Mockito.mock(FilePart.class);
		
		Mono<ResponseEntity<Flux<Response>>> monoResponse = responseController.addResponses(Flux.just(filePart), null);
		
		StepVerifier.create(monoResponse).expectNextMatches(res -> res.getStatusCodeValue() == 400).verifyComplete();
	}

}
