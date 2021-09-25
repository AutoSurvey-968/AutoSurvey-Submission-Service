package com.revature.autosurvey.submissions.utils;

import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import com.revature.autosurvey.submissions.beans.Response;


public class SqsSenderTest {

	@Mock
	private QueueMessagingTemplate queueMessagingTemplate;
	@Mock
	private AmazonSQS sqsExtended;
	private String queueName = SQSNames.SUBMISSIONS_QUEUE;
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
		
		List<Response> response = new ArrayList<>();
		response.add(new Response());
		
		payload = response.toString();
		message = MessageBuilder.withPayload(payload)
				.setHeader("MessageId", requestHeader)
				.build();
		
		mockSender.sendResponse(Mockito.any(), Mockito.any());
		
		assertNotNull(queueName);
		assertNotNull(message.getPayload());
		
		verify(mockSender, times(1)).sendResponse(Mockito.any(), Mockito.any());
	}
	
	@Test
	void testSendResponseOversizedPayload() {
		// Get absolute file path to oversize payload file
		// (might have to include .txt extension if you're getting NoSuchFileException
		String filePathOm = "oversizeS3message_sqsTestFile_submissions.txt";
		Path pathToFile = Paths.get(filePathOm).toAbsolutePath();
		String oversizedPayload = "";
		 
        try
        {
        	oversizedPayload = new String (Files.readAllBytes(pathToFile));
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
        
        System.out.println(oversizedPayload);
 //       System.out.println(pathToFile.toAbsolutePath());
	}
}
