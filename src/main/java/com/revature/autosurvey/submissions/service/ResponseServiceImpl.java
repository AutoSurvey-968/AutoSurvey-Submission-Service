package com.revature.autosurvey.submissions.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;

import com.revature.autosurvey.submissions.beans.Response;
import com.revature.autosurvey.submissions.beans.TrainingWeek;
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

	@Override
	public Mono<Response> getResponse(UUID id) {
		return responseRepository.findById(id).switchIfEmpty(Mono.error(new Exception()));
	}

	@Override
	public Mono<Response> updateResponse(UUID id, Response response) {
		return responseRepository.findById(id).flatMap(foundResponse -> {
			if (foundResponse != null) {
				return responseRepository.save(response);
			} else {
				return Mono.error(new Exception());
			}
		});
	}

	@Override
	public Mono<Response> deleteResponse(UUID uuid) {
		return responseRepository.deleteByUuid(uuid);
	}

	private Flux<String> readStringFromFile(FilePart file) {
		return file.content().map(buffer -> {
			byte[] bytes = new byte[buffer.readableByteCount()];
			buffer.read(bytes);
			DataBufferUtils.release(buffer);

			return new String(bytes);
		});
	}

	@Override
	public Response buildResponseFromCsvLine(String csvLine, String questionLine, UUID surveyId) {
		Response response = new Response();
		Map<String, String> responseMap = new HashMap<>();
		String[] questions = questionLine.split(",");
		String[] answers = csvLine.split(",");
		for (int i = 0; i < answers.length; i++) {
			if (!answers[i].equals("")) {
				responseMap.put(questions[i], answers[i]);
			}
			response.setBatch(responseMap.get("What batch are you in?"));
			response.setSurveyUuid(surveyId);
			String weekString = responseMap.get(
					"What was your most recently completed week of training? (Extended batches start with Week A, normal batches start with Week 1)");

			response.setWeek(null);
		}
		return null;
	}

	@Override
	public Flux<Response> getResponsesByBatch(String batchName) {
		return responseRepository.findAllByBatch(batchName);
	}

	@Override
	public Flux<Response> getResponsesByWeek(TrainingWeek eight) {
		// TODO Auto-generated method stub
		return null;
	}

}
