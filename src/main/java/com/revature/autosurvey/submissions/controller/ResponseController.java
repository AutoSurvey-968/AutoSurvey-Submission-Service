package com.revature.autosurvey.submissions.controller;

import java.text.ParseException;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import com.revature.autosurvey.submissions.beans.Response;
import com.revature.autosurvey.submissions.service.ResponseService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class ResponseController {
	private ResponseService responseService;

	@Autowired
	public void setResponseService(ResponseService responseService) {
		this.responseService = responseService;
	}

	@GetMapping
	public Flux<Response> getResponses(@RequestParam Optional<String> batch, @RequestParam Optional<String> weekStart,
			@RequestParam Optional<UUID> id) throws ParseException {
		if (batch.isPresent() && weekStart.isPresent()) {
			return responseService.getResponsesByBatchAndWeek(batch.get(), weekStart.get());
		}

		if (batch.isPresent()) {

			return responseService.getResponsesByBatch(batch.get());
		}

		if (weekStart.isPresent()) {
			return responseService.getResponsesByWeek(weekStart.get());
		}

		if (id.isPresent()) {
			return responseService.getResponse(id.get()).flux();
		}
		return responseService.getAllResponses();
	}

	@PostMapping(consumes = "multipart/form-data")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<Flux<Response>> addResponses(@RequestPart("file") Flux<FilePart> fileFlux,
			@RequestPart("surveyId") String surveyId) {
		if(surveyId == null || fileFlux == null) {
			return ResponseEntity.badRequest().build();
		}
		UUID surveyUuid = UUID.fromString(surveyId);
		Flux<Response> responses = responseService.addResponsesFromFile(fileFlux, surveyUuid);
		return ResponseEntity.ok().body(responses);
	}

	@PostMapping(consumes = "application/json")
	@PreAuthorize("isAuthenticated()")
	public Flux<Response> addResponses(@RequestBody Flux<Response> responses) {
		return responseService.addResponsesWithUuids(responses);
	}

	@PutMapping("{id}")
	@PreAuthorize("isAuthenticated()")
	public Mono<ResponseEntity<Response>> updateResponse(@PathVariable UUID id, @RequestBody Response response) {
		if(id == null || response == null || response.getUuid() == null || !response.getUuid().equals(id)) {
			return Mono.just(ResponseEntity.badRequest().build());
		}
		return responseService.updateResponse(id, response).map(updatedResponse -> ResponseEntity.ok().body(updatedResponse))
				.onErrorReturn(ResponseEntity.notFound().build());
	}

	@DeleteMapping("{id}")
	@PreAuthorize("isAuthenticated()")
	public Mono<ResponseEntity<Object>> deleteResponse(@PathVariable("id") UUID uuid) {
		return responseService.deleteResponse(uuid).map(response -> ResponseEntity.noContent().build())
				.onErrorResume(error -> Mono.just(ResponseEntity.notFound().build()));
	}
}
