package com.revature.autosurvey.submissions.utils;


import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;

import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.util.json.Jackson;
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
	
	private Response messageData;
	ObjectMapper mapper = new ObjectMapper();
	private ResponseRepository responseRepo;
	private SqsSender sqsSender;
	
	@Autowired
	SqsReceiver(ResponseRepository responseRepo, AmazonSQSAsync sqs){
		this.responseRepo = responseRepo;
		this.sqsSender = new SqsSender(sqs);
	}

    @SqsListener(value = SQSNames.SUBMISSIONS_QUEUE, deletionPolicy=SqsMessageDeletionPolicy.ON_SUCCESS)
    public void receiveMessage(Message<String> message) {
    	
    	messageData = Jackson.fromJsonString(message.getPayload(), Response.class);
    	Flux<Response> res;
    	log.trace("Receiving message: " + messageData);
    	
    	if(messageData.getBatch()!=null && messageData.getDate()!=null) {
    		Date startDate = messageData.getDate();
    		Calendar endCal = Calendar.getInstance();
    		endCal.setTime(startDate);
    		endCal.add(Calendar.DATE, 7);
    		Date endDate = endCal.getTime();
    		
    		res = responseRepo.findAllByBatchAndWeek(messageData.getBatch(), startDate, endDate);
    		log.trace("Response retrieved by Batch and Week");
    		sqsSender.sendResponse(res);
    	}
    	
    	if(messageData.getBatch()!=null) {
    		res = responseRepo.findAllByBatch(messageData.getBatch());
    		log.trace("Response retrieved by Batch name.");
    		sqsSender.sendResponse(res);
    	}
    	
    	if(messageData.getDate()!=null) {
    		Date startDate = messageData.getDate();
    		Calendar endCal = Calendar.getInstance();
    		endCal.setTime(startDate);
    		endCal.add(Calendar.DATE, 7);
    		Date endDate = endCal.getTime();
    		
    		res = responseRepo.findAllByWeek(startDate, endDate);
    		log.trace("Response retrieved by Week");
    		sqsSender.sendResponse(res);
    	}
    }

}
