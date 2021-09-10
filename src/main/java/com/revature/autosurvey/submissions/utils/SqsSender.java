package com.revature.autosurvey.submissions.utils;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import org.springframework.messaging.Message;
import com.amazonaws.util.json.Jackson;
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

	@Autowired
	public SqsSender(AmazonSQSAsync sqs) {
		this.queueMessagingTemplate = new QueueMessagingTemplate(sqs);
	}

	public void sendResponse(Flux<Response> response, UUID id) {
		log.trace("Response received by Sender");
		List<Response> list = response.collectList().block();
		Message<String> message = MessageBuilder.withPayload(Jackson.toJsonString(list)).build();
		message.getHeaders().put("MessageId", id);
		this.queueMessagingTemplate.send(queueName, message);
		log.trace("Message sent.");
	}
}
