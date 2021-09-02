package com.revature.autosurvey.submissions.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("submissionserror")
public class ErrorController {

  @GetMapping
  public ResponseEntity<Flux<Object>> handleGet() {
    return ResponseEntity.ok(Flux.empty());
  }

  @PostMapping
  public Mono<ResponseEntity<Object>> handlePost() {
    return Mono.just(ResponseEntity.status(503).build());
  }

  @PutMapping
  public Mono<ResponseEntity<Object>> handlePut() {
      return Mono.just(ResponseEntity.status(503).build());
  }
}