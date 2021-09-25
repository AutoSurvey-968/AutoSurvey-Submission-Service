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
import reactor.core.publisher.Mono;

/**
 * @author igastelum
 *
 */

@Log4j2
@Component
public class SqsReceiver {
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

		if (reqHeader != null) {
			messageId = reqHeader.toString();
		}
		log.debug("Message ID Received: ", messageId);

//		String payload = message.getPayload().replace("\"", "");
		String payload = message.getPayload();
		log.debug("Payload received: ", payload);

		// Parse JSON payload and extract target DB query parameters from message
		UUID uuid = null;
		UUID surveyUuid = null;
		String batch = "";
		String date = "";
		List<Response> response = new ArrayList<>();
		Date startDate = null;
		String errResponse;

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
			System.out.println("Querying DB with UUID");
			if(repository.findByUuid(uuid) == null) {
				System.out.println("Method call producing null value");
				return;
			}
				
			Response r = repository.findByUuid(uuid).switchIfEmpty(Mono.just(new Response())).block();
			System.out.println("Response received from UUID query: " + r);
			
//			if(r.getUuid() != uuid) {
//				res = Flux.empty();
//			}
//			else {
//				res = Flux.just(r);
//			}
			
			sqsSender.sendResponse(r.toString(), UUID.fromString(messageId));
			return;
		}
		
		if (surveyUuid != null) {
			repository.findAllBySurveyUuid(surveyUuid).map(r -> {
				response.add(r);
				return response;
			}).blockLast();
			
			System.out.println("Response List: " + response);
			sqsSender.sendResponse(response.toString(), UUID.fromString(messageId));
			return;
		}

		if (batch != null && date != null) {
			
			try {
				startDate = dateTimeFormat.parse(date);
			} catch (ParseException e) {
				e.printStackTrace();
				
				System.out.println("Invalid date in message payload");
				errResponse = "Invalid date format, unable to parse date parameter";
				sqsSender.sendResponse(errResponse, UUID.fromString(messageId));

				return;
			}
			
			Calendar endCal = Calendar.getInstance();
			endCal.setTime(startDate);
			endCal.add(Calendar.DATE, 7);
			Date endDate = endCal.getTime();

			repository.findAllByBatchAndWeek(batch, startDate, endDate).map(r -> {
				response.add(r);
				return response;
			}).blockLast();
			sqsSender.sendResponse(response.toString(), UUID.fromString(messageId));;
			log.trace("Response retrieved by Batch and Week");
		}
		
		if (batch != null) {
			repository.findAllByBatch(batch).map(r -> {
				response.add(r);
				return response;
			}).blockLast();
			sqsSender.sendResponse(response.toString(), UUID.fromString(messageId));;
			log.trace("Response retrieved by Batch name.");
			return;
		}

		if (date != null) {
			try {
				startDate = dateTimeFormat.parse(date);
			} catch (ParseException e) {
				e.printStackTrace();
				
				System.out.println("Invalid date in message payload");
				errResponse = "Invalid date format, unable to parse date parameter";
				sqsSender.sendResponse(errResponse, UUID.fromString(messageId));

				return;
			}
			Calendar endCal = Calendar.getInstance();
			endCal.setTime(startDate);
			endCal.add(Calendar.DATE, 7);
			Date endDate = endCal.getTime();

			repository.findAllByWeek(startDate, endDate).map(r -> {
				response.add(r);
				return response;
			}).blockLast();
			sqsSender.sendResponse(response.toString(), UUID.fromString(messageId));;
			log.trace("Response retrieved by Week");
		}
	}
}
