package com.revature.autosurvey.submissions.service;

import java.util.UUID;

import com.revature.autosurvey.submissions.beans.Response;
import com.revature.autosurvey.submissions.data.ResponseRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ResponseServiceImpl implements ResponseService{
	private ResponseRepository responseRepository;
	
	public void setResponseRepository(ResponseRepository responseRepository) {
		this.responseRepository = responseRepository;
	}
	
	public Mono<Response> addResponse(Response response) {
		return null;
		
	}
	
	//needs arguments
	@Override
	public Flux<Response> addResponses() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Mono<Response> getResponase(UUID id){
		return null;	
	}

	//needs arguments
	@Override
	public Flux<Response> getResponses() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Mono<Response> updateResponse(Response response) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Mono<Void> deleteResponse(Response response) {
		// TODO Auto-generated method stub
		return null;
	}
	

}
