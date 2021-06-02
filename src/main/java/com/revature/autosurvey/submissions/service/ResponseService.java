package com.revature.autosurvey.submissions.service;

import java.text.ParseException;
import java.util.UUID;

import org.springframework.http.codec.multipart.FilePart;

import com.revature.autosurvey.submissions.beans.Response;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ResponseService {

	Mono<Response> updateResponse(UUID uuid, Response response);

	Mono<Response> deleteResponse(UUID uuid);

	Flux<Response> addResponses(Flux<Response> responses);

	Response buildResponseFromCsvLine(String csvLine, String questionLine, UUID surveyId);

	Flux<Response> addResponsesFromFile(Flux<FilePart> fileFlux, UUID surveyId);

	Flux<Response> getAllResponses();
	
	Flux<Response> getResponsesByWeek(String date) throws ParseException;
	
	Flux<Response> getResponsesByBatch(String batch);

	Flux<Response> getResponsesByBatchAndWeek(String batch, String date) throws ParseException;
	
	Mono<Response> getResponse(UUID uuid);

}
