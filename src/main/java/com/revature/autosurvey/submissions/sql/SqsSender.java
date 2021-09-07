package com.revature.autosurvey.submissions.sql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.util.json.Jackson;

import io.awspring.cloud.messaging.core.QueueMessagingTemplate;


import com.revature.autosurvey.submissions.beans.Response;



@Component
public class SqsSender {

	private final QueueMessagingTemplate queueMessagingTemplate;
	
	private String queueName = "https://sqs.us-east-1.amazonaws.com/855430746673/SubmissionQueue";

	@Autowired
	public SqsSender(AmazonSQSAsync sqs) {
		this.queueMessagingTemplate = new QueueMessagingTemplate(sqs);
	}
	
	
	
	
@Scheduled(fixedDelay = 5000)
	public void sendReponse() {
	System.out.println("\nSending a message");
		Response r = new Response();
		this.queueMessagingTemplate.send(queueName, MessageBuilder.withPayload(Jackson.toJsonString(r)).build());
		
		}

	public void sendResponse(Response message) {
		System.out.println("\nSending a message");
	 		this.queueMessagingTemplate.send(queueName, MessageBuilder.withPayload(Jackson.toJsonString(message)).build());
	 	}




}

