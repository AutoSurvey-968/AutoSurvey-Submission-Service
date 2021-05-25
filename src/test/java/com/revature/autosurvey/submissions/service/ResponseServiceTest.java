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

import com.datastax.oss.driver.api.core.uuid.Uuids;
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

	@MockBean
	private ResponseRepository responseRepository;

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

	@Test
	void bigSplitReturnsCorrectList() {
		String string = "this is a value,\"This is a, value wi,th a ,b,unch ,of com,mas ,i,n it\",and this is another value,and so is this";
		List<String> result = new ArrayList<>();
		result.add("this is a value");
		result.add("\"This is a, value wi,th a ,b,unch ,of com,mas ,i,n it\"");
		result.add("and this is another value");
		result.add("and so is this");
		assertEquals(result, responseService.bigSplit(string));
	}

	@Test
	void timeLongFromStringReturnsTimeLong() {
		String string = "3/3/2020  14:08:17";
		Long timeLong = 1583244497000L;

		assertEquals(timeLong, responseService.timeLongFromString(string));
	}

	@Test
	void buildResponseFromCsvLineReturnsResponse() {
		Response res = new Response();
		UUID surveyId = UUID.fromString("11111111-1111-1111-1111-111111111001");
		Map<String, String> questions = new HashMap<>();
		res.setSurveyUuid(surveyId);
		questions.put("question1", "answer1");
		questions.put("question2", "answer2");
		questions.put("Timestamp", "3/3/2020  14:08:17");
		questions.put("What batch are you in?", "Mock Batch 45");
		questions.put(
				"\"What was your most recently completed week of training? (Extended batches start with Week A, normal batches start with Week 1)\"",
				"Week A");
		res.setResponses(questions);
		res.setWeek(TrainingWeek.A);
		res.setBatch("Mock Batch 45");
		res.setUuid(Uuids.startOf(1583244497000L));

		String csvLine = "answer1,answer2,,3/3/2020  14:08:17,Mock Batch 45,Week A";
		String questionLine = "question1,question2,question3,Timestamp,What batch are you in?,\"What was your most recently completed week of training? (Extended batches start with Week A, normal batches start with Week 1)\"";

		assertEquals(res, responseService.buildResponseFromCsvLine(csvLine, questionLine, surveyId));
	}

	@Test
	void addResponsesFromFileReturns() {
		// I do not know how to generate a Flux<FilePart> so I gotta figure that out to
		// write this test
	}

	@Test
	void getTrainingWeekFromStringReturnsTrainingWeekEnumA() {
		String weekString = "Week A";
		assertEquals(TrainingWeek.A, responseService.getTrainingWeekFromString(weekString));
	}

	@Test
	void getTrainingWeekFromStringReturnsTrainingWeekEnumB() {
		String weekString = "Week B";
		assertEquals(TrainingWeek.B, responseService.getTrainingWeekFromString(weekString));
	}

	@Test
	void getTrainingWeekFromStringReturnsTrainingWeekEnum1() {
		String weekString = "Week 1";
		assertEquals(TrainingWeek.ONE, responseService.getTrainingWeekFromString(weekString));
	}

	@Test
	void getTrainingWeekFromStringReturnsTrainingWeekEnum2() {
		String weekString = "Week 2";
		assertEquals(TrainingWeek.TWO, responseService.getTrainingWeekFromString(weekString));
	}

	@Test
	void getTrainingWeekFromStringReturnsTrainingWeekEnum3() {
		String weekString = "Week 3";
		assertEquals(TrainingWeek.THREE, responseService.getTrainingWeekFromString(weekString));
	}

	@Test
	void getTrainingWeekFromStringReturnsTrainingWeekEnum4() {
		String weekString = "Week 4";
		assertEquals(TrainingWeek.FOUR, responseService.getTrainingWeekFromString(weekString));
	}

	@Test
	void getTrainingWeekFromStringReturnsTrainingWeekEnum5() {
		String weekString = "Week 5";
		assertEquals(TrainingWeek.FIVE, responseService.getTrainingWeekFromString(weekString));
	}

	@Test
	void getTrainingWeekFromStringReturnsTrainingWeekEnum6() {
		String weekString = "Week 6";
		assertEquals(TrainingWeek.SIX, responseService.getTrainingWeekFromString(weekString));
	}

	@Test
	void getTrainingWeekFromStringReturnsTrainingWeekEnum7() {
		String weekString = "Week 7";
		assertEquals(TrainingWeek.SEVEN, responseService.getTrainingWeekFromString(weekString));
	}

	@Test
	void getTrainingWeekFromStringReturnsTrainingWeekEnum8() {
		String weekString = "Week 8";
		assertEquals(TrainingWeek.EIGHT, responseService.getTrainingWeekFromString(weekString));
	}

	@Test
	void getTrainingWeekFromStringReturnsTrainingWeekEnum9() {
		String weekString = "Week 9";
		assertEquals(TrainingWeek.NINE, responseService.getTrainingWeekFromString(weekString));
	}

	@Test
	void getTrainingWeekFromStringReturnsTrainingWeekEnum10() {
		String weekString = "Week 10";
		assertEquals(TrainingWeek.TEN, responseService.getTrainingWeekFromString(weekString));
	}

	@Test
	void getTrainingWeekFromStringReturnsNull() {
		String weekString = "Any Other Value";
		assertEquals(null, responseService.getTrainingWeekFromString(weekString));
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
	public void testGetAllResponsesByWeek() {
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
