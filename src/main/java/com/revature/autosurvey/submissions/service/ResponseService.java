package com.revature.autosurvey.submissions.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.http.codec.multipart.FilePart;

import com.revature.autosurvey.submissions.beans.Response;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ResponseService {

	Mono<Response> updateResponse(UUID uuid, Response response);

	Mono<Response> deleteResponse(UUID uuid);

	Mono<Response> addResponse(Response response);

	Flux<Response> addResponses(Flux<Response> responses);

	Flux<Response> addResponses(List<Response> responses);

	Response buildResponseFromCsvLine(String csvLine, String questionLine, UUID surveyId);

	Flux<Response> addResponsesFromFile(Flux<FilePart> fileFlux, UUID surveyId);

	Flux<Response> getAllResponses();
	
	Flux<Response> getResponsesByDate(Date date);
	
	Flux<Response> getResponsesByBatch(String batch);

	Flux<Response> getResponsesByBatchAndDate(String batch, Date date);
	
	Mono<Response> getResponse(UUID uuid);

}
