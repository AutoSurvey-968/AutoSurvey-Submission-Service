package com.revature.autosurvey.submissions.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.revature.autosurvey.submissions.beans.Response;
import com.revature.autosurvey.submissions.service.ResponseService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/submission")
public class ResponseController {
	private ResponseService responseService;
	
	@Autowired
	public void setResponceService(ResponseService responseService) {
		this.responseService = responseService;
	}
	
	@GetMapping
	public Flux<Response> getResponses(){
		return null;
	}
	
	@GetMapping("/{id}")
	public Mono<ResponseEntity<Response>> getResponse(@PathVariable UUID id){
		return responseService.getResponse(id).map(response -> ResponseEntity.ok().body(response))
				.onErrorReturn(ResponseEntity.badRequest().body(new Response()));
	}
	
	@PostMapping("/{id}")
	public Mono<ResponseEntity<Response>> addedResponse(){
		return null;	
	}
	
	@PutMapping("/{id}")
	public Mono<ResponseEntity<Response>> updateResponse(){
		return null;	
	}
	
	@DeleteMapping("/{id}")
	public Mono<ResponseEntity<Void>> deleteResponse(){
		return null;	
	}
}
