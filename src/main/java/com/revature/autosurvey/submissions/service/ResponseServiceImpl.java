package com.revature.autosurvey.submissions.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.revature.autosurvey.submissions.beans.Response;
import com.revature.autosurvey.submissions.data.ResponseRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ResponseServiceImpl implements ResponseService {
	private ResponseRepository responseRepository;

	@Autowired
	public void setResponseRepository(ResponseRepository responseRepository) {
		this.responseRepository = responseRepository;
	}

	public Mono<Response> addResponse(Response response) {
		return null;

	}

	// needs arguments
	@Override
	public Flux<Response> addResponses(List<Response> responses) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Mono<Response> getResponse(UUID id){
		return responseRepository.findById(id).switchIfEmpty(Mono.error(new Exception()));
	}

	// needs arguments
	@Override
	public Flux<Response> getResponses() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Mono<Response> updateResponse(UUID id, Response response) {
		return responseRepository.findById(id).flatMap(foundResponse -> {
			if(foundResponse != null) {
				return responseRepository.save(response);
			} else {
				return Mono.error(new Exception());
			}
		});
	}

	@Override
	public Mono<Void> deleteResponse(UUID id) {
		// TODO Auto-generated method stub
		return null;
	}

}
