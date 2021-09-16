package com.revature.autosurvey.submissions.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.revature.autosurvey.submissions.beans.Response;

import lombok.Data;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;

/**
 * @author Jasmine
 *
 */

@Log4j2
@Data
@Component
public class SqsSender {

	private final QueueMessagingTemplate queueMessagingTemplate;
	private String queueName = SQSNames.SUBMISSIONS_QUEUE;
	private AmazonSQS sqsExtended;

	@Autowired
	@Qualifier("AmazonSQS")
	public void setAmazonSQS(AmazonSQS sqsExt) {
		sqsExtended = sqsExt;
	}
	
	@Autowired
	public SqsSender(AmazonSQSAsync sqs) {
		this.queueMessagingTemplate = new QueueMessagingTemplate(sqs);
	}

	public void sendResponse(Flux<Response> response, UUID id) {
		log.trace("Response received by Sender");
		//Response received by Sender
		List<Response> list = new ArrayList<>();
		response.map(r -> {
			list.add(r);
			return r;
		}).blockLast();
		log.trace("Message to be sent: " + list);

		// Build response from list and send to Analytics Service
		Message<String> message = MessageBuilder.withPayload(list.toString())
				.setHeader("MessageId", id.toString())
				.build();
		
		try {
			queueMessagingTemplate.send(this.queueName, message);
			log.trace("Message sent." + list);
		}	catch (Exception e) {
			log.error("Payload too large. Posting message to S3 instead: " + e);
		}
		
		// File size too large, send Message to S3 instead
	    // Create a message queue for this example.
	    String qUrlString = "";
	    String qName = "AnalyticsQueue";
	    
	    if(!("").equals(qUrlString))
	    	qUrlString = sqsExtended.getQueueUrl(qName).toString();
	    else {		
	    	// Queue doesn't exist, create new one
		    final CreateQueueRequest createQueueRequest =
		            new CreateQueueRequest(qName);
		    qUrlString = sqsExtended.createQueue(createQueueRequest).getQueueUrl();
		    log.trace("Queue created.");
	    }
	    
	    log.trace("QueueUrl retrieved: " + qUrlString);
	    
	    // Send message to S3
	    sqsExtended.sendMessage(qUrlString, list.toString());
	    log.trace("Sent message to AWS S3: " + list);
	}
}