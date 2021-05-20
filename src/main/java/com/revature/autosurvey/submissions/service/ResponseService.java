package com.revature.autosurvey.submissions.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.revature.autosurvey.submissions.beans.Response;
import com.revature.autosurvey.submissions.data.ResponseRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public interface ResponseService {
	
	public void setResponseRepository(ResponseRepository responseRepository);
	
	public Mono<Response> addResponse(Response response);
	public Flux<Response> addResponses();
	public Mono<Response> getResponase(UUID id);
	public Flux<Response> getResponses();
	public Mono<Response> updateResponse(Response response);
	public Mono<Void> deleteResponse(Response response);
}
