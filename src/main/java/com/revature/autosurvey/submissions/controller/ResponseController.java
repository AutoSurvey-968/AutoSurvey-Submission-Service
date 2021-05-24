package com.revature.autosurvey.submissions.controller;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
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
	public Mono<ResponseEntity<Response>> getResponse(
			@RequestParam(required = false) String batch,
			@RequestParam(required = false) String week,
			@RequestParam(required = false) UUID id){
		if(id != null) {
			return responseService.getResponse(id).map(response -> ResponseEntity.ok().body(response))
					.onErrorReturn(ResponseEntity.badRequest().body(new Response()));
		}
		else {
			return Mono.just(ResponseEntity.badRequest().build());
		}
	}
	
	@PostMapping()
	public Mono<ResponseEntity<Response>> addResponses(@RequestParam ("csv") Boolean csv, 
			@RequestPart("file") Flux<FilePart> fileFlux, @RequestPart("surveyId") UUID surveyId, 
			@RequestBody Flux<Response> responses) {
		if (csv == true) {
			responseService.addResponsesFromFile(fileFlux, surveyId);
		} else {
			responseService.addResponses(responses);
		}
		return null;
	}
	
//	@PostMapping(params = {"csv"})
//	public Mono<ResponseEntity<Response>> addedResponses(@RequestParam ("csv") String csv, 
//			@RequestPart("file") Flux<FilePart> fileFlux, @RequestPart("surveyId") UUID surveyId){
//		return responseService.addResponsesFromFile(fileFlux, surveyId);	
//	}
//	
//	@PostMapping(params = {"single"})
//	public Mono<ResponseEntity<Response>> addedResponse(@RequestParam ("single") String single, 
//			@RequestBody Flux<Response> response){
//		return responseService.addResponses(responses);	
//	}
	
	@PutMapping("/{id}")
	public Mono<ResponseEntity<Response>> updateResponse(@PathVariable UUID id, @RequestBody Response response){
		return responseService.updateResponse(id, response).map(updatedResponse -> ResponseEntity.ok().body(response))
				.onErrorReturn(ResponseEntity.badRequest().body(new Response()));
	}
	
//	@DeleteMapping("/{id}")
//	public Mono<ResponseEntity<Object>> deleteResponse(@PathVariable UUID id){
//		return responseService.deleteResponse(id).thenReturn(ResponseEntity.noContent().build())
//				.onErrorReturn(ResponseEntity.badRequest().build());
//	}
	
	@DeleteMapping("{id}")
    public Mono<ResponseEntity<Object>> deleteResponse(@PathVariable("id") UUID uuid) {
        return responseService.deleteResponse(uuid)
                .map(response -> ResponseEntity.noContent().build())
                .onErrorResume(error -> Mono.just(ResponseEntity.notFound().build()));
    }
}

