package com.revature.autosurvey.submissions.utils;

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

import lombok.Data;
import lombok.extern.log4j.Log4j2;

/**
 * @author Jasmine
 *
 */

@Log4j2
@Data
@Component
class SqsSender {

	private QueueMessagingTemplate queueMessagingTemplate;
	private String queueName = SQSNames.SUBMISSIONS_QUEUE;
	private AmazonSQS sqsExtended;

	@Autowired
	public SqsSender(AmazonSQSAsync sqs) {
		this.queueMessagingTemplate = new QueueMessagingTemplate(sqs);
	}
	
	@Autowired
	@Qualifier("AmazonSQS")
	public void setAmazonSQS(AmazonSQS sqsExt) {
		sqsExtended = sqsExt;
	}
	
	public void setQueueMessagingTemplate(QueueMessagingTemplate qmt) {
		this.queueMessagingTemplate = qmt;
	}

	public void sendResponse(String response, UUID id) {
		log.trace("Response received by Sender");

		// Build response from list and send to Analytics Service
		Message<String> message = MessageBuilder.withPayload(response)
				.setHeader("MessageId", id.toString())
				.build();
		System.out.println(response);
		
		try {
			queueMessagingTemplate.send(SQSNames.ANALYTICS_QUEUE, message);
			log.trace("Message sent." + response);
			System.out.println("Message sent." + response);
		}	catch (Exception e) {
			log.error("Payload too large. Posting message to S3 instead: " + e);
			sendResponseToS3(response.toString());
		}
		
	}
	
	public void sendResponseToS3(String payload) {
		// File size too large, send Message to S3 instead
	    // Create a message queue in S3
		
	    String qName = "AnalyticsQueue";  
	    String qUrlString = sqsExtended.getQueueUrl(qName).toString();

	    final CreateQueueRequest createQueueRequest = new CreateQueueRequest(qName);
	    qUrlString = sqsExtended.createQueue(createQueueRequest).getQueueUrl();
	    log.trace("Queue created.");	    
	    log.trace("QueueUrl retrieved: " + qUrlString);
	    
	    // Send message to S3
	    sqsExtended.sendMessage(qUrlString, payload);
	    log.trace("Sent message to AWS S3: " + payload);
	    System.out.println("Sent message to S3: " + payload);
	}
}