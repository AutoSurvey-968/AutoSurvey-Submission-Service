package com.revature.autosurvey.submissions.sql;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.amazonaws.services.sqs.AmazonSQSAsync;

import com.amazonaws.util.json.Jackson;

import org.springframework.messaging.Message;

import com.revature.autosurvey.submissions.beans.Response;

import lombok.Data;



/**
 * @author jasmine
 *
 */

@Data
@Component
public class SqsSender {

	private final QueueMessagingTemplate queueMessagingTemplate;
    private String queueName = "https://sqs.us-east-1.amazonaws.com/855430746673/SubmissionQueue";
	private List<String> headerIds;


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

	 public void sendResponse(Response response) {
		System.out.println("\nSending a message");
		Message<String> message = MessageBuilder.withPayload(Jackson.toJsonString(response)).build();
	    headerIds.add(message.getHeaders().getId().toString());
	    this.queueMessagingTemplate.send(queueName, message);
	 	}



}

