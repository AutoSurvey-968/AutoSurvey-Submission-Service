package com.revature.autosurvey.submissions.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.revature.autosurvey.submissions.beans.Response;
import com.revature.autosurvey.submissions.beans.Response.WeekNum;
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
	
	private static List<Response> responses;
	
	@BeforeAll
	public static void mockResponses() {
		responses = new ArrayList<>();
		Response response1 = new Response();
		response1.setBatchName("1");
		response1.setWeek(TrainingWeek.ONE);
		response1.setResponseId(UUID.fromString("11111111-1111-1111-1111-111111111101"));
		responses.add(response1);
		Response response2 = new Response();
		response1.setBatchName("2");
		response1.setWeek(TrainingWeek.TWO);
		response1.setResponseId(UUID.fromString("11111111-1111-1111-1111-111111111102"));
		responses.add(response2);
		}
	
	@Test
	public void addResponsesReturnsFluxResponses() {
		Response response = responses.get(0);
		Mono<Response> responseMono = Mono.just(response);
		Flux<Response> responseFlux = responseMono.flux();
		
		when(responseRepository.saveAll(responseMono)).thenReturn(responseFlux);
		
		assertEquals(responseFlux, responseService.addResponses(responses));
	}
	
	@Test
	public void addResponseReturnsMonoResponse() {
		Response response = responses.get(0);
		Mono<Response> responseMono = Mono.just(response);
		
		when(responseRepository.save(response).thenReturn(responseMono));
		
		assertEquals(responseMono, responseService.addResponse(response));
	}
	
	@Test
	public void buildResponseFromCsvLineReturnsResponse() {
		Response res = new Response();
		UUID surveyId = UUID.fromString("11111111-1111-1111-1111-111111111111");
		Map<String,String> questions = new HashMap<>();
		res.setSurveyResponses(questions);
		res.setSurveyId(surveyId);
		
		questions.put("question1", "answer1");
		questions.put("question2", "answer2");
		questions.put("question4", "answer4");
		String csvLine = "answer1,answer2,,answer4";
		String questionLine = "question1, question 2, question 3, question 4";
		
		assertEquals(res, responseService.buildResponseFromCsvLine(csvLine, questionLine, surveyId));
	}
	
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
	
	@Test
	public void testGetAllResponseByBatch() {
		Response testResponse = new Response();
		String batchName = new String("Batch 43");
		testResponse.setBatchName(batchName);
		Mockito.when(responseRepository.findAllByBatch(batchName)).thenReturn(Flux.fromIterable(new ArrayList<Response>()));
		//StepVerifier.create(responseService.getResponsesByBatch(batchName)).expectNextMatches(name -> name.)
	}


}
