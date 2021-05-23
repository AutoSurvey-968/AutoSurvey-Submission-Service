package com.revature.autosurvey.submissions.service;

import java.util.List;
import java.util.UUID;

import org.reactivestreams.Publisher;
import org.springframework.http.codec.multipart.FilePart;

import com.revature.autosurvey.submissions.beans.Response;
import com.revature.autosurvey.submissions.data.ResponseRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ResponseService {
	
	public void setResponseRepository(ResponseRepository responseRepository);
	
	public Mono<Response> addResponse(Response response);
	public Flux<Response> addResponses(List<Response> responses);
	public Mono<Response> getResponse(UUID uuid);
	public Flux<Response> getResponses();
	public Mono<Response> updateResponse(UUID uuid, Response response);
	public Flux<Response> getResponsesByBatch(String batch);
	public Mono<Response> deleteResponse(UUID uuid);
	public Response buildResponseFromCsvLine(String csvLine, String questionLine, UUID surveyUuid);
	public Flux<Response> addResponsesFromFile (Flux<FilePart> fileFlux, UUID surveyUuid);
}
