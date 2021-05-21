package com.revature.autosurvey.submissions.service;

import java.util.List;
import java.util.UUID;

import com.revature.autosurvey.submissions.beans.Response;
import com.revature.autosurvey.submissions.data.ResponseRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ResponseService {
	
	public void setResponseRepository(ResponseRepository responseRepository);
	
	public Mono<Response> addResponse(Response response);
	public Flux<Response> addResponses(List<Response> responses, UUID surveyId);
	public Mono<Response> getResponse(UUID id);
	public Flux<Response> getResponses();
	public Mono<Response> updateResponse(UUID id, Response response);
	public Flux<Response> getResponsesByBatch(String batchName);
	public Mono<Void> deleteResponse(UUID id);
}
