package com.revature.autosurvey.submissions.sql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.util.json.Jackson;

import io.awspring.cloud.messaging.core.QueueMessagingTemplate;
import lombok.Data;
import reactor.core.publisher.Flux;

import com.revature.autosurvey.submissions.beans.Response;


@Data
@Component
public class SqsSender {

	private final QueueMessagingTemplate queueMessagingTemplate;
	
	private String queueName = "https://sqs.us-east-1.amazonaws.com/855430746673/SubmissionQueue";
//	@Value("${aws.queuename}")
//	private String queueName;

	@Autowired
	public SqsSender(AmazonSQSAsync sqs) {
		this.queueMessagingTemplate = new QueueMessagingTemplate(sqs);
	}
	
	
	
//@Scheduled(fixedDelay = 5000)
//	public void sendReponse() {
//	System.out.println("\nSending a message");
//		Response r = new Response();
//		this.queueMessagingTemplate.send(queueName, MessageBuilder.withPayload(Jackson.toJsonString(r)).build());
//		
//		}
	public void sendResponse(String queue, String ResponseId) {
	System.out.println("\nSending a message");
 		this.queueMessagingTemplate.send(queueName, MessageBuilder.withPayload(Jackson.toJsonString(ResponseId)).build());
 	}
	public void sendResponse(Response message) {
		System.out.println("\nSending a message");
	 		this.queueMessagingTemplate.send(queueName, MessageBuilder.withPayload(Jackson.toJsonString(message)).build());
	 	}



	public void sendReponse(Flux<Response> responses) {
		System.out.println("\nSending a message");
 		this.queueMessagingTemplate.send(queueName, MessageBuilder.withPayload(Jackson.toJsonString(responses)).build());
 	}




}

