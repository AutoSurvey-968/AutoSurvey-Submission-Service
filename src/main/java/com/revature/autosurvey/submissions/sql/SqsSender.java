package com.revature.autosurvey.submissions.sql;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.amazonaws.services.sqs.AmazonSQSAsync;

import com.amazonaws.util.json.Jackson;

import org.springframework.messaging.Message;

import com.revature.autosurvey.submissions.beans.Response;

import lombok.Data;
import reactor.core.publisher.Flux;



/**
 * @author Jasmine
 *
 */

@Data
@Component
public class SqsSender {

	private final QueueMessagingTemplate queueMessagingTemplate;
    private String queueName = "https://sqs.us-east-1.amazonaws.com/855430746673/SubmissionQueue";
	private List<UUID> headerIds;


	@Autowired
	public SqsSender(AmazonSQSAsync sqs) {
		this.queueMessagingTemplate = new QueueMessagingTemplate(sqs);
		this.headerIds = new ArrayList<>();
	}
	

		
    @Async
	 public void sendResponse(Flux<Response> response) {
    	List<Response> list = response.collectList().block();
		Message<String> message = MessageBuilder.withPayload(Jackson.toJsonString(list)).build();
	    headerIds.add(message.getHeaders().getId());
        this.queueMessagingTemplate.send(queueName, message);
	 	}

}

