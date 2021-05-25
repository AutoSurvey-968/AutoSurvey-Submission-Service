package com.revature.autosurvey.submissions.controller;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
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
import com.revature.autosurvey.submissions.utils.Utilities;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class ResponseController {
	private ResponseService responseService;
	private Utilities util;

	@Autowired
	public void setResponceService(ResponseService responseService) {
		this.responseService = responseService;
	}

	@Autowired
	public void setUtilities(Utilities utilities) {
		this.util = utilities;
	}

	@GetMapping
	public Flux<ResponseEntity<Response>> getResponses(@RequestParam Optional<String> batch,
			@RequestParam Optional<String> week, @RequestParam Optional<UUID> id) {
		System.out.println("butts");
		System.out.println(batch);
		System.out.println(week);
		if (batch.isPresent() && week.isPresent()) {
			return responseService.getResponsesByBatchForWeek(batch.get(), week.get())
					.map(responses -> ResponseEntity.ok(responses)).onErrorReturn(ResponseEntity.badRequest().build());
		}

		if (batch.isPresent()) {
			return responseService.getResponsesByBatch(batch.get()).map(responses -> ResponseEntity.ok(responses))
					.onErrorReturn(ResponseEntity.badRequest().build());
		}

		if (week.isPresent()) {
			return responseService.getResponsesByWeek(util.getTrainingWeekFromString(week.get()))
					.map(responses -> ResponseEntity.ok(responses)).onErrorReturn(ResponseEntity.badRequest().build());
		}

		if (id.isPresent()) {
			return responseService.getResponse(id.get()).map(response -> ResponseEntity.ok().body(response))
					.switchIfEmpty(Mono.just(ResponseEntity.notFound().build()))
					.onErrorReturn(ResponseEntity.badRequest().body(new Response())).flux();
		}
		return Flux.just(ResponseEntity.badRequest().build());
	}

	@PostMapping(consumes = "text/csv")
	public Flux<ResponseEntity<Response>> addResponses(@RequestPart("file") Flux<FilePart> fileFlux,
			@RequestPart("surveyId") UUID surveyId) {
		return responseService.addResponsesFromFile(fileFlux, surveyId)
				.map(response -> ResponseEntity.ok().body(response))
				.onErrorReturn(ResponseEntity.badRequest().body(new Response()));
	}

	@PostMapping(consumes = "application/json")
	public Flux<ResponseEntity<Response>> addResponses(@RequestBody Flux<Response> responses) {
		return responseService.addResponses(responses).map(resp -> ResponseEntity.ok().body(resp))
				.onErrorReturn(ResponseEntity.badRequest().body(new Response()));
	}

	@PutMapping("/{id}")
	public Mono<ResponseEntity<Response>> updateResponse(@PathVariable UUID id, @RequestBody Response response) {
		return responseService.updateResponse(id, response).map(updatedResponse -> ResponseEntity.ok().body(response))
				.onErrorReturn(ResponseEntity.badRequest().body(new Response()));
	}

	@DeleteMapping("{id}")
	public Mono<ResponseEntity<Object>> deleteResponse(@PathVariable("id") UUID uuid) {
		return responseService.deleteResponse(uuid).map(response -> ResponseEntity.noContent().build())
				.onErrorResume(error -> Mono.just(ResponseEntity.notFound().build()));
	}
}
