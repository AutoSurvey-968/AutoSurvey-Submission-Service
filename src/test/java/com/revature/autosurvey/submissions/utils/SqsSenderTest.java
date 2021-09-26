package com.revature.autosurvey.submissions.utils;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.revature.autosurvey.submissions.beans.Response;


class SqsSenderTest {

	private QueueMessagingTemplate queueMessagingTemplate;
	@Mock
	private AmazonSQS sqsExtended;
	@Mock
	private AmazonSQSAsync sqs;
	private String queueName = SQSNames.ANALYTICS_QUEUE;
	private Message<String> message;
	private String payload;
	private String requestHeader = UUID.randomUUID().toString();
	
	@BeforeEach
	void beforeEach() {
		MockitoAnnotations.openMocks(this);
	}
	
	@Test
	void testSendResponseSmallPayload() {
		SqsSender mockSender = Mockito.mock(SqsSender.class);
		
		List<Response> responses = new ArrayList<>();
		responses.add(new Response());
		
		payload = responses.toString();
		message = MessageBuilder.withPayload(payload)
				.setHeader("MessageId", requestHeader)
				.build();
		
		mockSender.sendResponse(responses.toString(), UUID.fromString(requestHeader));
		
		assertNotNull(queueName);
		assertNotNull(requestHeader);
		assertNotNull(message.getPayload());
		
		verify(mockSender, times(1)).sendResponse(Mockito.any(), Mockito.any());
	}
	
	@Test
	void testSendResponseOversizedPayload() {
		// Get absolute file path to oversize payload file
		// (might have to include .txt extension if you're getting NoSuchFileException
		String filePath = "oversizeS3message_sqsTestFile_submissions";
		Path pathToFile = Paths.get(filePath).toAbsolutePath();
		String oversizedPayload = "";
		queueMessagingTemplate = new QueueMessagingTemplate(sqs);
		 
        try
        {
        	oversizedPayload = new String (Files.readAllBytes(pathToFile));
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }

        SqsSender messageHandler = Mockito.mock(SqsSender.class);
        messageHandler.setQueueMessagingTemplate(queueMessagingTemplate);
        
        Message<String> message = MessageBuilder.withPayload(oversizedPayload)
        		.setHeader("MessageId", requestHeader).build();
        
        doCallRealMethod().when(messageHandler).sendResponse(oversizedPayload, UUID.fromString(requestHeader));        
        Mockito.doNothing().when(messageHandler).sendResponseToS3(oversizedPayload);

        messageHandler.sendResponse(oversizedPayload, UUID.fromString(requestHeader));
        
		assertNotNull(queueName);
		assertNotNull(requestHeader);
		assertNotNull(message.getPayload());
		
		verify(messageHandler, times(1)).sendResponseToS3(oversizedPayload);		
	}
}
