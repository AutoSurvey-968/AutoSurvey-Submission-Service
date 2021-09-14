package com.revature.autosurvey.submissions.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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

/**
 * @author igastelum
 *
 */

@Log4j2
@Component
public class SqsReceiver {
	public static final String QUEUE_NAME = SQSNames.SUBMISSIONS_QUEUE;
	private Message<String> lastReceived;
	private final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyy-MM-dd");

	private ResponseRepository repository;
	private ObjectMapper mapper;
	private SqsSender sqsSender;

	public SqsReceiver() {
		super();
	}

	public SqsSender getSqsSender() {
		return sqsSender;
	}

	@Autowired
	public void setSqsSender(SqsSender sqsSender) {
		this.sqsSender = sqsSender;
	}

	@Autowired
	public void setResponseRepo(ResponseRepository repository) {
		this.repository = repository;
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
	public void receiveMessage(Message<String> message) {
		
		log.debug("Survey Queue listener invoked");

		log.debug("Headers received: {}", message.getHeaders());
		
		String reqHeader = message.getHeaders().get("MessageId").toString();
		log.debug("Message ID Received: {}", reqHeader);

		String payload = message.getPayload();
		log.debug("Payload received: ", payload);

		// Parse JSON payload and extract target survey ID from message
		String batch = "";
		String date = "";
		String surveyUuid = "";
		Flux<Response> res;
		Date startDate = null;

		try {
			String batchCons = "batch";
			JSONObject obj = new JSONObject(payload);
			log.trace("Response batch received: " + obj.getString(batchCons));
			batch = obj.getString(batchCons).equals("null") ? null : obj.getString(batchCons);
			String dateCons = "date";
			log.trace("Response date received: " + obj.getString(dateCons));
			date = obj.getString(dateCons).equals("null") ? null : obj.getString(dateCons);
			String surveyUuidCons = "surveyUuid";
			log.trace("Response surveyUuid received: " + obj.getString(surveyUuidCons));
			surveyUuid = obj.getString(surveyUuidCons).equals("null") ? null : obj.getString(surveyUuidCons);
		} catch (JSONException e1) {
			log.error(e1);
		}
		
		if(surveyUuid!=null) {
			res = repository.findAllBySurveyUuid(UUID.fromString(surveyUuid));
			sqsSender.sendResponse(res, UUID.fromString(reqHeader));
			return;
		}
				
		if((!("").equals(batch) && !("").equals(date)) || (batch!=null && date!=null)) {
			try {
				startDate = dateTimeFormat.parse(date);
			} catch (ParseException e) {
				log.error(e);
			}
    		Calendar endCal = Calendar.getInstance();
    		endCal.setTime(startDate);
    		endCal.add(Calendar.DATE, 7);
    		Date endDate = endCal.getTime();
    		
    		res = repository.findAllByBatchAndWeek(batch, startDate, endDate);
    		log.trace("Response retrieved by Batch and Week");
    		sqsSender.sendResponse(res, UUID.fromString(reqHeader));
    	}
    	
    	if(!("").equals(batch)) {
    		res = repository.findAllByBatch(batch);
    		log.trace("Response retrieved by Batch name.");
    		sqsSender.sendResponse(res, UUID.fromString(reqHeader));
    		return;
    	}
    	
    	if(!("").equals(date) || date!=null) {
			try {
				startDate = dateTimeFormat.parse(date);
			} catch (ParseException e) {
				log.error(e);
			}
    		Calendar endCal = Calendar.getInstance();
    		endCal.setTime(startDate);
    		endCal.add(Calendar.DATE, 7);
    		Date endDate = endCal.getTime();
    		
    		res = repository.findAllByWeek(startDate, endDate);
    		log.trace("Response retrieved by Week");
    		sqsSender.sendResponse(res, UUID.fromString(reqHeader));
    	}
	}
}
