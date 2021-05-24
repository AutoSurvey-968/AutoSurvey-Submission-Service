package com.revature.autosurvey.submissions.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.assertj.core.util.Arrays;
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
	public void testGetResponse() {
		UUID id = UUID.randomUUID();
		when(responseRepository.findByUUID(id)).thenReturn(Mono.just(new Response()));
		StepVerifier.create(responseService.getResponse(id)).expectNext(new Response()).expectComplete().verify();
	}

	@Test
	public void testGetResponseNoResponse() {
		UUID id = UUID.randomUUID();
		when(responseRepository.findByUUID(id)).thenReturn(Mono.empty());
		StepVerifier.create(responseService.getResponse(id)).expectComplete().verify();
	}
	
	@Test
	public void testGetResponseError() {
		UUID id = UUID.randomUUID();
		when(responseRepository.findByUUID(id)).thenReturn(Mono.error(new Exception()));
		StepVerifier.create(responseService.getResponse(id)).expectError().verify();
	}

	@Test
	public void testUpdateResponseExists() {
		UUID id = UUID.randomUUID();
		Response response = new Response();
		when(responseRepository.findByUUID(id)).thenReturn(Mono.just(new Response()));
		when(responseRepository.save(response)).thenReturn(Mono.just(new Response()));
		StepVerifier.create(responseService.getResponse(id)).expectNext(new Response()).expectComplete().verify();
	}

	@Test
	public void testUpdateResponseDoesNotExists() {
		UUID id = UUID.randomUUID();
		Response response = new Response();
		when(responseRepository.findByUUID(id)).thenReturn(Mono.empty());
		when(responseRepository.save(response)).thenReturn(Mono.just(new Response()));
		StepVerifier.create(responseService.getResponse(id)).expectError().verify();
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
	public void testGetAllResponsesByBatch() {
		Response testResponse1 = new Response();
		Response testResponse2 = new Response();
		testResponse1.setBatch("Batch 23");
		testResponse2.setBatch("Batch 23");
		Mockito.when(responseRepository.findAllByBatch("Batch 23")).thenReturn(Flux.just(testResponse1, testResponse2));
		StepVerifier.create(responseService.getResponsesByBatch("Batch 23"))
		.expectNext(testResponse1)
		.expectNext(testResponse2)
		.verifyComplete();
	}
}
