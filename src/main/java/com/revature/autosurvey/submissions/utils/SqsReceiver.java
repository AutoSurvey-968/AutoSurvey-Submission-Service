package com.revature.autosurvey.submissions.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.autosurvey.submissions.beans.Response;
import com.revature.autosurvey.submissions.data.ResponseRepository;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author igastelum
 *
 */

@Log4j2
@Component
class SqsReceiver {
	public static final String QUEUE_NAME = SQSNames.SUBMISSIONS_QUEUE;
	private Message<String> lastReceived;
	private final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-mm-dd");

	private ResponseRepository repository;
	private ObjectMapper mapper;
	private SqsSender sqsSender;

	public SqsReceiver() {
		super();
	}

	public SqsSender getSqsSender() {
		return this.sqsSender;
	}

	@Autowired
	public void setSqsSender(SqsSender sqsSender) {
		this.sqsSender = sqsSender;
	}

	@Autowired
	public void setResponseRepo(ResponseRepository repository) {
		this.repository = repository;
	}

	public ResponseRepository getRepository() {
		return repository;
	}

	@Autowired
	public void setObjectMapper(ObjectMapper mapper) {
		this.mapper = mapper;
	}

	public Message<String> getLastReceived() {
		return lastReceived;
	}

	public void setLastReceived(Message<String> lastReceived) {
		this.lastReceived = lastReceived;
	}

	public ObjectMapper getMapper() {
		return mapper;
	}
	
	@SqsListener(value = QUEUE_NAME, deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
	public void receiveMessage(Message<String> message) throws ParseException {

		log.debug("Survey Queue listener invoked");
		log.debug("Headers received: ", message.getHeaders());
		Object reqHeader = message.getHeaders().get("MessageId");
		String messageId = null;
		if(reqHeader != null) {
			messageId = reqHeader.toString();
		}

		log.debug("Message ID Received: ", messageId);

		String payload = message.getPayload();
		log.debug("Payload received: ", payload);

		// Parse JSON payload and extract target DB query parameters from message
		UUID uuid = null;
		UUID surveyUuid = null;
		String batch = "";
		String date = "";
		Date startDate = null;
		
		List<Response> responses = new ArrayList<>();
		String errResponse = "";

		try {
			JSONObject obj = new JSONObject(payload);
			
			String uuidCons = "uuid";
			log.trace("Response UUID received: " + obj.getString(uuidCons));
			uuid = obj.getString(uuidCons).equals("null") ? null : 
				UUID.fromString(obj.getString(uuidCons).replace("\"", ""));

			String surveyUuidCons = "surveyUuid";
			log.trace("Survey UUID received: " + obj.getString(surveyUuidCons));
			surveyUuid = obj.getString(surveyUuidCons).equals("null") ? null : 
				UUID.fromString(obj.getString(surveyUuidCons).replace("\"", ""));
			
			String batchCons = "batch";
			log.trace("Batch received: " + obj.getString(batchCons));
			batch = obj.getString(batchCons).equals("null") ? null : 
				obj.getString(batchCons).replace("\"", "");
			
			String dateCons = "date";
			log.trace("Date received: " + obj.getString(dateCons));
			date = obj.getString(dateCons).equals("null") ? null : 
				obj.getString(dateCons).replace("\"", "");
			
		} catch (JSONException e) {
			log.error(e);
			
			errResponse = "Invalid parameters, unable to parse JSON message";
			sqsSender.sendResponse(errResponse, UUID.fromString(messageId));
			System.out.println("Error response sent to Analytics: " + errResponse);			

			return;
		} catch (IllegalArgumentException e) {
			log.error(e);
			
			errResponse = "Invalid format for UUID, unable to parse message";
			sqsSender.sendResponse(errResponse, UUID.fromString(messageId));
			System.out.println("Error response sent to Analytics: " + errResponse);

			return;	
		}
		
		System.out.println("Response ID received: " + uuid);
		System.out.println("Survey ID received: " + surveyUuid);
		System.out.println("Batch received: " + batch);
		System.out.println("Date received: " + date);
			
		if (uuid != null) {				
			getResponseByUuid(uuid, UUID.fromString(messageId), responses);
			return;
		}
		
		if (surveyUuid != null) {
			getResponseBySurveyId(surveyUuid, UUID.fromString(messageId), responses);
			return;
		}

		if (date != null) {
			
			try {
				startDate = dateTimeFormat.parse(date);
			} catch (ParseException e) {
				log.error(e);
				
				System.out.println("Invalid date in message payload");
				errResponse = "Invalid date format, unable to parse date parameter";
				sqsSender.sendResponse(errResponse, UUID.fromString(messageId));
				return;
			}
			
			getResponseByDateAndBatch(startDate, batch, UUID.fromString(messageId), responses);
			return;
		}
		
		if (batch != null) {
			getResponseByBatch(batch, UUID.fromString(messageId), responses);
		}
	}
	
	private void getResponseByUuid(UUID uuid, UUID messageId, List<Response> responses) {
		Response response = repository.findByUuid(uuid)
				.switchIfEmpty(Mono.just(new Response())).block();
		System.out.println("Response received from UUID query: " + response);
		
		if(response.getUuid() == null || !response.getUuid().equals(uuid)) {
			String reply = "No Response found with UUID :" + uuid;
			System.out.println(reply + "\nSent reply to Analytics Queue");
			sqsSender.sendResponse(reply, messageId);
			return;
		}
		
		responses.add(response);
		sqsSender.sendResponse(responses.toString(), messageId);
	}
	
	private void getResponseBySurveyId(UUID surveyId, UUID messageId, List<Response> responses) {
		repository.findAllBySurveyUuid(surveyId)
		.switchIfEmpty(Flux.just(new Response()))
		.map(r -> {
			responses.add(r);
			return responses;
		}).blockLast();
		
		if(responses.size() == 1 && responses.get(0).getSurveyUuid() == null) {
			String reply = "No Responses found for Query by SurveyID :" + surveyId;
			sqsSender.sendResponse(responses.toString(), messageId);
			System.out.println("Response sent to Analytics:\n" + reply);
			return;
		}
		
		System.out.println("Response List: " + responses);
		sqsSender.sendResponse(responses.toString(), messageId);
	}
	
	private void getResponseByDateAndBatch(Date startDate, String batch, UUID messageId,
			List<Response> responses) {
		
		Calendar endCal = Calendar.getInstance();
		endCal.setTime(startDate);
		endCal.add(Calendar.DATE, 7);
		Date endDate = endCal.getTime();
		
		// If batch and date are provided as parameters
		if(batch != null) {
			System.out.println("Batch: " + batch);
			System.out.println("startDate: " + startDate);
			System.out.println("endDate: " + endDate);
			
			repository.findAllByBatchAndWeek(batch, startDate, endDate)
			.switchIfEmpty(Flux.just(new Response()))
			.map(r -> {
				responses.add(r);
				return responses;
			})
			.blockLast();
			
			// If empty Response, reply with no results found
			if(responses.size() == 1 && responses.get(0).getBatch() == null) {
				String reply = "No Responses found for Batch and Date Query :" +
						"\nBatch: " + batch + "\nDate: " + startDate;
				sqsSender.sendResponse(responses.toString(), messageId);
				System.out.println("Response sent to Analytics:\n" + reply);
				return;
			}
			
			sqsSender.sendResponse(responses.toString(), messageId);
			log.trace("Responses retrieved by Batch and Week");
			return;
		}
		
		// Else only date was given
		repository.findAllByWeek(startDate, endDate)
		.switchIfEmpty(Flux.just(new Response()))
		.map(r -> {
			responses.add(r);
			return responses;
		})
		.blockLast();
		
		// If empty Response, reply with no results found
		if(responses.size() == 1 && responses.get(0).getDate() == null) {
			String reply = "No Responses found for Query by Date: " + startDate;
			sqsSender.sendResponse(responses.toString(), messageId);
			System.out.println("Response sent to Analytics:\n" + reply);
			return;
		}
		
		sqsSender.sendResponse(responses.toString(), messageId);
		log.trace("Response retrieved by Week");		
	}
	
	private void getResponseByBatch(String batch, UUID messageId, List<Response> responses) {
		repository.findAllByBatch(batch)
		.switchIfEmpty(Flux.just(new Response()))
		.map(r -> {
			responses.add(r);
			return responses;
		})
		.blockLast();
		
		// If empty Response, reply with no results found
		if(responses.size() == 1 && responses.get(0).getBatch() == null) {
			String reply = "No Response found for Query by Batch: " + batch;
			sqsSender.sendResponse(responses.toString(), messageId);
			System.out.println("Response sent to Analytics:\n" + reply);
			return;
		}
		
		sqsSender.sendResponse(responses.toString(), messageId);
		log.trace("Response retrieved by Batch name.");
	}
}
