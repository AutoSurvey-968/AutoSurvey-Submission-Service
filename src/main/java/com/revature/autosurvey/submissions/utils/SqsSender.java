package com.revature.autosurvey.submissions.utils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import org.springframework.messaging.Message;
import com.amazonaws.util.json.Jackson;
import com.revature.autosurvey.submissions.beans.Response;

import lombok.Data;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;

/**
 * @author jasmine
 *
 */

@Log4j2
@Data
@Component
public class SqsSender {
	private final QueueMessagingTemplate queueMessagingTemplate;
	private static final String queueName = SQSNames.SUBMISSIONS_QUEUE;
	private List<String> headerIds;

	@Autowired
	public SqsSender(AmazonSQSAsync sqs) {
		this.queueMessagingTemplate = new QueueMessagingTemplate(sqs);
		this.headerIds = new ArrayList<>();
	}

	@Async
	public void sendResponse(Flux<Response> response) {
		List<Response> list = response.collectList().block();
		log.trace("Response received to Sender: " + list);
		Message<String> message = MessageBuilder.withPayload(Jackson.toJsonString(list)).build();
		try {
			headerIds.add(message.getHeaders().getId().toString());
		}catch(NullPointerException e) {
			log.debug("getId is null.");
		}
		this.queueMessagingTemplate.send(queueName, message);
		log.trace("Message sent.");
	}
}
