package com.revature.autosurvey.submissions.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
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
import com.revature.autosurvey.submissions.beans.TrainingWeek;
import com.revature.autosurvey.submissions.data.ResponseRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
public class ResponseServiceTest {

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
		
//		@Bean
//		public Utilities getUtilities() {
//			return Mockito.mock(Utilities.class);
//		}
	}

	@Autowired
	private ResponseServiceImpl responseService;

	@MockBean
	private ResponseRepository responseRepository;
	
//	@MockBean
//	private Utilities utilities;

	private static List<Response> responses;

	@BeforeAll
	public static void mockResponses() {
		responses = new ArrayList<>();
		Response response1 = new Response();
		response1.setBatch("1");
		response1.setWeek(TrainingWeek.ONE);
		response1.setUuid(UUID.fromString("11111111-1111-1111-1111-111111111101"));
		responses.add(response1);
		Response response2 = new Response();
		response1.setBatch("2");
		response1.setWeek(TrainingWeek.TWO);
		response1.setUuid(UUID.fromString("11111111-1111-1111-1111-111111111102"));
		responses.add(response2);
	}

	@Test
	void addResponsesReturnsFluxResponses() {
		Response response = responses.get(0);
		Mono<Response> responseMono = Mono.just(response);
		Flux<Response> responseFlux = responseMono.flux();

		when(responseRepository.saveAll(responses)).thenReturn(responseFlux);

		assertEquals(responseFlux, responseService.addResponses(responses));
	}

	@Test
	void addResponseReturnsMonoResponse() {
		Response response = responses.get(0);
		Mono<Response> responseMono = Mono.just(response);

		when(responseRepository.save(response)).thenReturn(responseMono);

		assertEquals(responseMono, responseService.addResponse(response));
	}

	
	 @Test void buildResponseFromCsvLineReturnsResponse() { 
		 String csvLine = "answer1,answer2,,3/3/2020  14:08:17,Mock Batch 45,Week A"; 
		 String questionLine = "question1,question2,question3,Timestamp,What batch are you in?,\"What was your most recently completed week of training? (Extended batches start with Week A, normal batches start with Week 1)\""; 
		 UUID surveyId = UUID.fromString("11111111-1111-1111-1111-111111111001");
		 
		 Response responseFromMethod = responseService.buildResponseFromCsvLine(csvLine, questionLine, surveyId);

		 Response expectedResponse = new Response(); Map<String, String>
		 questions = new HashMap<>(); expectedResponse.setSurveyUuid(surveyId);
		 questions.put("question1", "answer1"); questions.put("question2", "answer2");
		 questions.put("Timestamp", "3/3/2020  14:08:17");
		 questions.put("What batch are you in?", "Mock Batch 45"); questions.put(
		 "\"What was your most recently completed week of training? (Extended batches start with Week A, normal batches start with Week 1)\""
		 , "Week A"); expectedResponse.setResponses(questions); expectedResponse.setWeek(TrainingWeek.A);
		 expectedResponse.setBatch("Mock Batch 45");
		 expectedResponse.setUuid(responseFromMethod.getUuid());
		 
		 assertEquals(expectedResponse, responseFromMethod);
	 }
		 
//	 @Test void buildResponseFromCsvLineReturnsResponseOld() { 
//		 String csvLine = "answer1,answer2,,3/3/2020  14:08:17,Mock Batch 45,Week A"; 
//		 String questionLine = "question1,question2,question3,Timestamp,What batch are you in?,\"What was your most recently completed week of training? (Extended batches start with Week A, normal batches start with Week 1)\""; 
//		 UUID surveyId = UUID.fromString("11111111-1111-1111-1111-111111111001"); 
//			 
//		 //Expected response 
//		 Response res = new Response(); Map<String, String>
//		 questions = new HashMap<>(); res.setSurveyUuid(surveyId);
//		 questions.put("question1", "answer1"); questions.put("question2", "answer2");
//		 questions.put("Timestamp", "3/3/2020  14:08:17");
//		 questions.put("What batch are you in?", "Mock Batch 45"); questions.put(
//		 "\"What was your most recently completed week of training? (Extended batches start with Week A, normal batches start with Week 1)\""
//		 , "Week A"); res.setResponses(questions); res.setWeek(TrainingWeek.A);
//		 res.setBatch("Mock Batch 45");
//		 res.setUuid(UUID.fromString("13814000-1dd2-11b2-8080-808080808080"));
//		 
//		 // Mocking 
//		 String weekString = "Week A"; 
//		 String timeString = "3/3/2020 14:08:17"; 
//		 List<String> questionList = new ArrayList<>();
//		 questionList.add("question1"); questionList.add("question2");
//		 questionList.add("question3"); questionList.add("Timestamp");
//		 questionList.add("What batch are you in?"); questionList.add("\"What was your most recently completed week of training? (Extended batches start with Week A, normal batches start with Week 1)\""); 
//		 List<String> answerList = new ArrayList<>(); answerList.add("answer1");
//		 answerList.add("answer2"); answerList.add("");
//		 answerList.add("3/3/2020  14:08:17"); answerList.add("Mock Batch 45");
//		 answerList.add("Week A");
//		 
//		 when(Utilities.getTrainingWeekFromString(weekString)).thenReturn(TrainingWeek.A);
//		 when(Utilities.timeLongFromString(timeString)).thenReturn(1583244497000L);
//		 when(Utilities.bigSplit(questionLine)).thenReturn(questionList);
//		 when(Utilities.bigSplit(csvLine)).thenReturn(answerList);
//		 
//		 assertEquals(res, responseService.buildResponseFromCsvLine(csvLine,questionLine, surveyId));
//	}
	 

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
	void testGetAllResponsesByBatch() {
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
	void testGetAllResponsesByWeek() {
		Response testResponse1 = new Response();
		Response testResponse2 = new Response();
		testResponse1.setWeek(TrainingWeek.EIGHT);
		testResponse2.setWeek(TrainingWeek.EIGHT);
		when(responseRepository.findAllByWeek(TrainingWeek.EIGHT)).thenReturn(Flux.just(testResponse1, testResponse2));
		StepVerifier.create(responseService.getResponsesByWeek(TrainingWeek.EIGHT))
				.expectNextMatches(response -> response.getWeek().equals(TrainingWeek.EIGHT))
				.expectNextMatches(response -> response.getWeek().equals(TrainingWeek.EIGHT)).verifyComplete();
	}

}
