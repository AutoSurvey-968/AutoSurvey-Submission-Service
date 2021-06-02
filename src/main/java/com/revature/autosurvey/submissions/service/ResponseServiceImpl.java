package com.revature.autosurvey.submissions.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.revature.autosurvey.submissions.beans.Response;
import com.revature.autosurvey.submissions.data.ResponseRepository;
import com.revature.autosurvey.submissions.utils.Utilities;

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
	public Flux<Response> addResponses(Flux<Response> responses) {
		return responseRepository.saveAll(responses);
	}

	@Override
	public Mono<Response> getResponse(UUID id) {
		return responseRepository.findByUuid(id);
	}

	@Override
	public Mono<Response> updateResponse(UUID id, Response response) {
		return responseRepository.findByUuid(id).switchIfEmpty(Mono.just(new Response())).flatMap(foundResponse -> {
			if (foundResponse.getUuid() != null) {
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

	@Override
	public Response buildResponseFromCsvLine(String csvLine, String questionLine, UUID surveyId) {
		Response response = new Response();
		Map<String, String> responseMap = new HashMap<>();

		List<String> questionsString = Utilities.bigSplit(questionLine);
		List<String> answersString = Utilities.bigSplit(csvLine);

		for (int i = 0; i < answersString.size(); i++) {
			if (!answersString.get(i).isEmpty()) {
				responseMap.put(questionsString.get(i), answersString.get(i));
			}
		}
		response.setBatch(responseMap.get("What batch are you in?"));
		response.setSurveyUuid(surveyId);
		// String weekString = responseMap.get(
		// "\"What was your most recently completed week of training? (Extended batches
		// start with Week A, normal batches start with Week 1)\"");
		response.setResponses(responseMap);
		Long timestamp = Utilities.timeLongFromString(responseMap.get("Timestamp"));
		response.setDate(new Date(timestamp));
		response.setUuid(Uuids.startOf(timestamp));
		return response;
	}

	@Override
	@PreAuthorize("isAuthenticated()")
	public Flux<Response> addResponsesFromFile(Flux<FilePart> fileFlux, UUID surveyId) {
		Flux<Response> responsesToAdd = fileFlux.flatMap(Utilities::readStringFromFile).map(string -> {
			List<Response> responses = new ArrayList<>();
			String[] lines = string.split("\\r?\\n");
			for (int i = 1; i < lines.length; i++) {
				try {
					Response response = buildResponseFromCsvLine(lines[i], lines[0], surveyId);
					responses.add(response);
				} catch (Exception e) {
//							logger.warn(e);
				}
			}
			return responses;
		}).flatMapIterable(Function.identity());
		return responseRepository.saveAll(responsesToAdd);
	}

	@Override
	public Flux<Response> getResponsesByBatch(String batchName) {
		return responseRepository.findAllByBatch(batchName);
	}

	@Override
	public Flux<Response> getResponsesByWeek(String date) throws ParseException {
		Date startDate = new SimpleDateFormat("yyy-MM-dd").parse(date);
		Calendar endCal = Calendar.getInstance();
		endCal.setTime(startDate);
		endCal.add(Calendar.DATE, 7);
		Date endDate = endCal.getTime();
		return responseRepository.findAllByWeek(startDate, endDate);
	}

	@Override
	public Flux<Response> getResponsesByBatchAndWeek(String batch, String date) throws ParseException {
		Date startDate = new SimpleDateFormat("yyy-MM-dd").parse(date);
		Calendar endCal = Calendar.getInstance();
		endCal.setTime(startDate);
		endCal.add(Calendar.DATE, 7);
		Date endDate = endCal.getTime();
		return responseRepository.findAllByBatchAndWeek(batch, startDate, endDate);
	}

	@Override
	public Flux<Response> getAllResponses() {
		return responseRepository.findAll();
	}

}
