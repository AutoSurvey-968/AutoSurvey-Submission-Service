package com.revature.autosurvey.submissions.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.revature.autosurvey.submissions.beans.Response;
import com.revature.autosurvey.submissions.data.ResponseRepository;
import com.revature.autosurvey.submissions.utils.Utilities;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
class ResponseServiceTest {

	@TestConfiguration
	static class Configuration {
		@Bean
		public ResponseServiceImpl getResponseService(ResponseRepository responseRepository) {
			ResponseServiceImpl responseService = new ResponseServiceImpl();
			responseService.setResponseRepository(responseRepository);
			return responseService;
		}

		@Bean
		public ResponseRepository getResponseRepository() {
			return Mockito.mock(ResponseRepository.class);
		}

	}

	@Autowired
	private ResponseServiceImpl responseService;

	@MockBean
	private ResponseRepository responseRepository;

	private static List<Response> responses;

	@BeforeAll
	public static void mockResponses() {
		responses = new ArrayList<>();
		Response response1 = new Response();
		response1.setBatch("1");
		response1.setUuid(UUID.fromString("11111111-1111-1111-1111-111111111101"));
		responses.add(response1);
		Response response2 = new Response();
		response1.setBatch("2");
		response1.setUuid(UUID.fromString("11111111-1111-1111-1111-111111111102"));
		responses.add(response2);
	}

	@Test
	void buildResponseFromCsvLineReturnsResponse() {
		String csvLine = "answer1,answer2,,3/3/2020  14:08:17,Mock Batch 45,Week A";
		String questionLine = "question1,question2,question3,Timestamp,What batch are you in?,\"What was your most recently completed week of training? (Extended batches start with Week A, normal batches start with Week 1)\"";
		UUID surveyId = UUID.fromString("11111111-1111-1111-1111-111111111001");

		Response responseFromMethod = responseService.buildResponseFromCsvLine(csvLine, questionLine, surveyId);

		Response expectedResponse = new Response();
		Map<String, String> questions = new HashMap<>();
		expectedResponse.setSurveyUuid(surveyId);
		questions.put("question1", "answer1");
		questions.put("question2", "answer2");
		questions.put("Timestamp", "3/3/2020  14:08:17");
		questions.put("What batch are you in?", "Mock Batch 45");
		questions.put(
				"\"What was your most recently completed week of training? (Extended batches start with Week A, normal batches start with Week 1)\"",
				"Week A");
		expectedResponse.setResponses(questions);
		expectedResponse.setDate(new Date(Utilities.timeLongFromString(questions.get("Timestamp"))));
		expectedResponse.setBatch("Mock Batch 45");
		expectedResponse.setUuid(responseFromMethod.getUuid());

		assertEquals(expectedResponse, responseFromMethod);
	}

	@Test
	void addResponsesFromFileReturns() {
		// I do not know how to generate a Flux<FilePart> so I gotta figure that out to
		// write this test
	}

	@Test
	void testGetResponse() {
		UUID id = UUID.randomUUID();
		when(responseRepository.findByUuid(id)).thenReturn(Mono.just(new Response()));
		StepVerifier.create(responseService.getResponse(id)).expectNext(new Response()).expectComplete().verify();
	}

	@Test
	void testGetResponseNoResponse() {
		UUID id = UUID.randomUUID();
		when(responseRepository.findByUuid(id)).thenReturn(Mono.empty());
		StepVerifier.create(responseService.getResponse(id)).expectComplete().verify();
	}

	@Test
	void testGetResponseError() {
		UUID id = UUID.randomUUID();
		when(responseRepository.findByUuid(id)).thenReturn(Mono.error(new Exception()));
		StepVerifier.create(responseService.getResponse(id)).expectError().verify();
	}

	@Test
	void testUpdateResponseExists() {
		UUID id = UUID.randomUUID();
		Response response = new Response();
		response.setUuid(id);
		Mono<Response> responseMono = Mono.just(response);
		when(responseRepository.findByUuid(id)).thenReturn(responseMono);
		when(responseRepository.save(response)).thenReturn(Mono.just(new Response()));
		StepVerifier.create(responseService.updateResponse(id, response)).expectNext(new Response()).expectComplete()
				.verify();
	}

	@Test
	void testUpdateResponseDoesNotExists() {
		UUID id = UUID.randomUUID();
		Response response = new Response();
		when(responseRepository.findByUuid(id)).thenReturn(Mono.empty());
		when(responseRepository.save(response)).thenReturn(Mono.just(new Response()));
		StepVerifier.create(responseService.updateResponse(id, response)).expectError().verify();
	}

	@Test
	void testDeleteByUuid() {
		Response response = new Response();
		UUID id = UUID.randomUUID();
		response.setUuid(id);

		doReturn(Mono.just(response)).when(responseRepository).deleteByUuid(any());

		UUID idResult = responseService.deleteResponse(id).block().getUuid();
		assertEquals(id, idResult);
	}

	@Test
	void testGetAllResponsesGetsResponses() {
		when(responseRepository.findAll()).thenReturn(Flux.empty());
		StepVerifier.create(responseService.getAllResponses()).verifyComplete();
	}

	@Test
	void testGetAllResponsesByBatchReturnsProperBatch() {
		Response testResponse1 = new Response();
		Response testResponse2 = new Response();
		String testBatch = "Batch 23";
		testResponse1.setBatch(testBatch);
		testResponse2.setBatch(testBatch);
		when(responseRepository.findAllByBatch(testBatch)).thenReturn(Flux.just(testResponse1, testResponse2));
		StepVerifier.create(responseService.getResponsesByBatch(testBatch))
				.expectNextMatches(response -> response.getBatch().equals(testBatch))
				.expectNextMatches(response -> response.getBatch().equals(testBatch)).verifyComplete();
	}

	@Test
	void testAddResponsesOneResponse() {
		Flux<Response> responseFlux = Flux.just(new Response());
		when(responseRepository.saveAll(responseFlux)).thenReturn(responseFlux);
		StepVerifier.create(responseService.addResponses(responseFlux)).expectNext(new Response()).expectComplete()
				.verify();
	}

	@Test
	void testAddResponsesMultipleResponses() {
		Flux<Response> responseFlux = Flux.fromArray(new Response[] { new Response(), new Response(), new Response() });
		when(responseRepository.saveAll(responseFlux)).thenReturn(responseFlux);
		StepVerifier.create(responseService.addResponses(responseFlux)).expectNext(new Response())
				.expectNext(new Response()).expectNext(new Response()).expectComplete().verify();
	}

	@Test
	void testAddResponsesEmpty() {
		Flux<Response> emtpyFlux = Flux.empty();
		when(responseRepository.saveAll(emtpyFlux)).thenReturn(emtpyFlux);
		StepVerifier.create(responseService.addResponses(emtpyFlux)).expectComplete().verify();
	}

	@Test
	void testAddResponsesReturnError() {
		Flux<Response> responseFlux = Flux.just(new Response());
		when(responseRepository.saveAll(responseFlux)).thenReturn(Flux.error(new Exception()));
		StepVerifier.create(responseService.addResponses(responseFlux)).expectError().verify();
	}

	@Test
	void testGetAllResponsesByWeekReturnsProperWeek() throws ParseException {
		Response testResponse1 = new Response();
		Response testResponse2 = new Response();
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date testDate = format.parse("2021-03-29");
		testResponse1.setDate(testDate);
		testResponse2.setDate(testDate);
		when(responseRepository.findAllByWeek(any(), any())).thenReturn(Flux.just(testResponse1, testResponse2));
		StepVerifier.create(responseService.getResponsesByWeek(testDate))
				.expectNextMatches(response -> response.getDate().equals(testDate))
				.expectNextMatches(response -> response.getDate().equals(testDate)).verifyComplete();
	}

}
