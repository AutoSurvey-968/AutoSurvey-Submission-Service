package com.revature.autosurvey.submissions.service;

import java.util.List;
import java.util.UUID;

import org.springframework.http.codec.multipart.FilePart;

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
	public Mono<Response> updateResponse(UUID id, Response response);
	public Flux<Response> getResponsesByBatch(String batchName);
	public Mono<Void> deleteResponse(UUID id);
	public Response buildResponseFromCsvLine(String csvLine, String questionLine, UUID surveyId);
	public Flux<Response> addResponsesFromFile (Flux<FilePart> fileFlux, UUID surveyId);
}
