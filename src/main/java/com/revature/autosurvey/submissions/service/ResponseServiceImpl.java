package com.revature.autosurvey.submissions.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
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
	public Flux<Response> addResponses(List<Response> responses, UUID surveyId) {
		
		return null;
	}
	
	public Mono<Response> getResponse(UUID id){
		System.out.println(3);
		return responseRepository.findById(id).switchIfEmpty(Mono.error(new Exception()));
	}

	// needs arguments
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
	public Mono<Void> deleteResponse(UUID id) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private Flux<String> readStringFromFile(FilePart file){
		return file.content().map(buffer -> {
			byte[] bytes = new byte[buffer.readableByteCount()];
             buffer.read(bytes);
             DataBufferUtils.release(buffer);

             return new String(bytes);
		});
	}
	
	private Response buildResponseFromCsvLine(String csvLine) {
		return null;
	}

}
