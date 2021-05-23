package com.revature.autosurvey.submissions.service;

import java.util.UUID;

import com.revature.autosurvey.submissions.beans.Response;
import com.revature.autosurvey.submissions.data.ResponseRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ResponseService {

	public void setResponseRepository(ResponseRepository responseRepository);

	public Mono<Response> getResponse(UUID uuid);

	public Mono<Response> updateResponse(UUID uuid, Response response);

	public Flux<Response> getResponsesByBatch(String batch);

	public Mono<Response> deleteResponse(UUID uuid);

	public Response buildResponseFromCsvLine(String csvLine, String questionLine, UUID surveyUuid);
}
