package com.revature.autosurvey.submissions.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;

import com.datastax.oss.driver.api.core.uuid.Uuids;
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
	public Mono<Response> addResponse(Response response) {
		return responseRepository.save(response);
	}

	@Override
	public Flux<Response> addResponses(Flux<Response> responses) {
		return responseRepository.saveAll(responses);
	}
	
	@Override
	public Flux<Response> addResponses(List<Response> responses) {
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

	private Flux<String> readStringFromFile(FilePart file) {
		return file.content().map(buffer -> {
			byte[] bytes = new byte[buffer.readableByteCount()];
			buffer.read(bytes);
			DataBufferUtils.release(buffer);

			return new String(bytes);
		});
	}

	@Override
	public List<String> bigSplit(String string) {
		String[] stringArr = string.split(",");
		List<String> stringList = new ArrayList<>(Arrays.asList(stringArr));
		Boolean truthFlag = true;
		while (Boolean.TRUE.equals(truthFlag)) {
			truthFlag = false;
			for (int i = 0; i < stringList.size(); i++) {
				if (!stringList.get(i).isEmpty() && stringList.get(i).charAt(0) == 34 && stringList.get(i).charAt(stringList.get(i).length()-1) != 34) {
					stringList.set(i, stringList.get(i) + "," + stringList.get(i + 1));
					stringList.remove(i + 1);
					truthFlag = true;
					break;
				}
			}
		}
		return stringList;
	}
	
//	@Override
//	public Long timeLongFromString(String timeString) {
//		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
//		Date date = null;
//		try {
//			date = format.parse(timeString);
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//		Long timeMs = date.getTime();
//		return timeMs;
//	}
	
	@Override

	public Long timeLongFromString(String timeString) {
		timeString = String.join(" ", timeString.split("\\s+"));
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("M/d/yyyy HH:mm:ss");
		LocalDateTime ldate = null;
			ldate = LocalDateTime.parse(timeString, dtf);
		Long timeMs = ldate.toInstant(ZoneOffset.ofHours(0)).toEpochMilli();
		return timeMs;
	}
	
	@Override
	public Response buildResponseFromCsvLine(String csvLine, String questionLine, UUID surveyId) {
		Response response = new Response();
		Map<String, String> responseMap = new HashMap<>();
		
		List<String> questionsString = bigSplit(questionLine);
		List<String> answersString = bigSplit(csvLine);
		
		for (int i = 0; i < answersString.size(); i++) {
			if (!answersString.get(i).isEmpty()) {
				responseMap.put(questionsString.get(i), answersString.get(i));
			}
		}
		response.setBatch(responseMap.get("What batch are you in?"));
		response.setSurveyUuid(surveyId);
		String weekString = responseMap.get("\"What was your most recently completed week of training? (Extended batches start with Week A, normal batches start with Week 1)\"");
		response.setWeek(getTrainingWeekFromString(weekString));
		response.setResponses(responseMap);
		String timeString = responseMap.get("Timestamp");
		response.setUuid(Uuids.startOf(timeLongFromString(timeString)));
		return response;
	}
	
	@Override
	public Flux<Response> addResponsesFromFile(Flux<FilePart> fileFlux, UUID surveyId){
		Flux<Response> responsesToAdd = fileFlux.flatMap(this::readStringFromFile)
				.map(string -> {
					List<Response> responses = new ArrayList<>();
					String[] lines = string.split("\\r?\\n");
					for(int i = 1; i < lines.length; i++) {
						try {
							Response response = buildResponseFromCsvLine(lines[i], lines[0], surveyId);
							responses.add(response);
						}catch(Exception e) {
//							logger.warn(e);
						}
					}
					return responses;
				}).flatMapIterable(Function.identity());
		return responseRepository.saveAll(responsesToAdd);
	}
	
	@Override
	public TrainingWeek getTrainingWeekFromString(String weekString) {
		switch (weekString) {
		case "Week A" : return TrainingWeek.A;
		case "Week B" : return TrainingWeek.B;
		case "Week 1" : return TrainingWeek.ONE;
		case "Week 2" : return TrainingWeek.TWO;
		case "Week 3" : return TrainingWeek.THREE;
		case "Week 4" : return TrainingWeek.FOUR;
		case "Week 5" : return TrainingWeek.FIVE;
		case "Week 6" : return TrainingWeek.SIX;
		case "Week 7" : return TrainingWeek.SEVEN;
		case "Week 8" : return TrainingWeek.EIGHT;
		case "Week 9" : return TrainingWeek.NINE;
		case "Week 10" : return TrainingWeek.TEN;
		default : return null;
		}
	}
		
	@Override
	public Flux<Response> getResponsesByBatch(String batchName) {		
		return responseRepository.findAllByBatch(batchName);
	}

}
