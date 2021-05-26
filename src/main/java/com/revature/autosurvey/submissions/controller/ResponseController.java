package com.revature.autosurvey.submissions.controller;

import java.util.Optional;
import java.util.UUID;

import org.apache.tinkerpop.shaded.minlog.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.revature.autosurvey.submissions.beans.Response;
import com.revature.autosurvey.submissions.service.ResponseService;
import com.revature.autosurvey.submissions.utils.Utilities;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/submissions")
public class ResponseController {
	private ResponseService responseService;

	@Autowired
	public void setResponseService(ResponseService responseService) {
		this.responseService = responseService;
	}

	@GetMapping
	public Flux<Response> getResponses(ServerWebExchange exchange, @RequestParam Optional<String> batch,
			@RequestParam Optional<String> week, @RequestParam Optional<UUID> id) {
		if (batch.isPresent() && week.isPresent()) {
			System.out.println("I'm in batchweek");
			return responseService.getResponsesByBatchForWeek(batch.get(), week.get());
		}

		if (batch.isPresent()) {
			System.out.println("I'm in batch");
			return responseService.getResponsesByBatch(batch.get());
		}

		if (week.isPresent()) {
			System.out.println("I'm in week");
			return responseService.getResponsesByWeek(Utilities.getTrainingWeekFromString(week.get()));
		}

		if (id.isPresent()) {
			System.out.println("I'm in ID");
			return responseService.getResponse(id.get()).flux();
		}
		System.out.println("I'm nowhere");
		return responseService.getAllResponses();
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
