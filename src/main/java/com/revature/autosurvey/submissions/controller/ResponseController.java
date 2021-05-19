package com.revature.autosurvey.submissions.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
	public Flux<Object> getResponses(){
		return null;
	}
	
	@GetMapping("/{id}")
	public Mono<ResponseEntity<Object>> getResponse(){
		return null;
	}
	
	@PostMapping("/{id}")
	public Mono<ResponseEntity<Object>> addedResponse(){
		return null;	
	}
	
	@PutMapping("/{id}")
	public Mono<ResponseEntity<Object>> updateResponse(){
		return null;	
	}
	
	@DeleteMapping("/{id}")
	public Mono<ResponseEntity<Void>> deleteResponse(){
		return null;	
	}
}
