package com.revature.autosurvey.submissions.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.github.andrewoma.dexx.collection.List;
import com.revature.autosurvey.submissions.beans.Response;
import com.revature.autosurvey.submissions.data.ResponseRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ResponseService {
	
	public void setResponseRepository(ResponseRepository responseRepository);
	
	public Mono<Response> addResponse(Response response);
	public Flux<Response> addResponses(List<Response> responses);
	public Mono<Response> getResponse(UUID id);
	public Flux<Response> getResponses();
	public Mono<Response> updateResponse(Response response);
	public Mono<Void> deleteResponse(UUID id);
}
