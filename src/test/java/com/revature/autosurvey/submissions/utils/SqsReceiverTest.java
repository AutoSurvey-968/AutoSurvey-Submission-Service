package com.revature.autosurvey.submissions.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.autosurvey.submissions.beans.Response;
import com.revature.autosurvey.submissions.data.ResponseRepository;

import reactor.core.publisher.Flux;

public class SqsReceiverTest {

	public static final String QUEUE_NAME = SQSNames.SUBMISSIONS_QUEUE;
	private Message<String> message;
	private final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-mm-dd");

	@Mock
	private ResponseRepository repository;
	@Mock
	private ObjectMapper mapper;
	@Mock
	private SqsSender sqsSender;
	
	private String requestHeader = UUID.randomUUID().toString();
	private String validId;
	private String validUuid;
	private String invalidId;
	private String validBatch;
	private String invalidBatch;
	private String payload;
	
	private List<Response> responses;
	
	private Date validDate;
	private Calendar cal = Calendar.getInstance();
	private Date invalidDate;
	
	
	@BeforeEach
	void beforeEach() {
		MockitoAnnotations.openMocks(this);
	}
	
//	@Test
//	void testReceiverValidResponseId() throws ParseException {
//		// Valid Response ID in payload
//		payload = "{\n"
//				+ "  \"uuid\": \"46af76a0-9927-11ea-8080-808080808080\",\n"
//				+ "  \"batch\": null,\n"
//				+ "  \"date\": null,\n"
//				+ "  \"surveyUuid\": null,\n"
//				+ "}";
//
//		String responseUUID = "";
//		message = MessageBuilder.withPayload(payload)
//				.setHeader("MessageId", requestHeader).build();
//		validUuid = "46af76a0-9927-11ea-8080-808080808080";
//		
//        StaticApplicationContext applicationContext = new StaticApplicationContext();
//        applicationContext.registerSingleton("incomingMessageHandler", SqsReceiver.class);
//        applicationContext.refresh();      
//
//        SqsReceiver messageHandler = applicationContext.getBean(SqsReceiver.class);
//		
//        messageHandler.setResponseRepo(repository); 
//        messageHandler.setSqsSender(sqsSender);
//        
//        Response r = new Response();
//		  
//		Mockito.when(messageHandler.getRepository().findByUuid(UUID.fromString(validUuid)))
//		.thenReturn(Mono.just(r));
//		
////		Flux<Response> res = Flux.just(r);
//		  
//		Mockito.doNothing().when(messageHandler.getSqsSender()).sendResponse(res, UUID.fromString(requestHeader));
//
//		messageHandler.receiveMessage(message);
//		
//		try {
//			JSONObject obj = new JSONObject(message.getPayload());
//			responseUUID = obj.getString("uuid");	
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//		
//		assertEquals(validUuid, responseUUID, "The Response UUID parsed from message payload"
//				+ " is the same one we entered");
//		Mockito.verify(messageHandler.getRepository()).findByUuid(UUID.fromString(responseUUID));
//		verify(messageHandler.getSqsSender(), times(0)).sendResponse(Mockito.any(), Mockito.any());
////		verify(messageHandler.getSqsSender(), times(1)).sendResponse(res, UUID.fromString(requestHeader));
//
//		applicationContext.close();
//	}
	
	@Test
	void testReceiverValidSurveyId() throws ParseException {
		// Valid survey ID in payload
		payload = "{\n"
				+ "  \"uuid\": null,\n"
				+ "  \"batch\": null,\n"
				+ "  \"date\": null,\n"
				+ "  \"surveyUuid\": \"d50ca970-14ac-11ec-a00f-df792d7e6b27\"\n"
				+ "}";

		String messageSID = "";
		message = MessageBuilder.withPayload(payload)
				.setHeader("MessageId", requestHeader).build();
		validId = "d50ca970-14ac-11ec-a00f-df792d7e6b27";
		
        StaticApplicationContext applicationContext = new StaticApplicationContext();
        applicationContext.registerSingleton("incomingMessageHandler", SqsReceiver.class);
        applicationContext.refresh();      

        SqsReceiver messageHandler = applicationContext.getBean(SqsReceiver.class);
		
        messageHandler.setResponseRepo(repository); 
        messageHandler.setSqsSender(sqsSender);
        
        Response r = new Response();
		  
		Mockito.when(messageHandler.getRepository().findAllBySurveyUuid(UUID.fromString(validId)))
		.thenReturn(Flux.just(r));
		
		responses = new ArrayList<>();
		responses.add(r);
		  
		Mockito.doNothing().when(messageHandler.getSqsSender())
		.sendResponse(responses.toString(), UUID.fromString(requestHeader));

		messageHandler.receiveMessage(message);
		
		try {
			JSONObject obj = new JSONObject(message.getPayload());
			messageSID = obj.getString("surveyUuid");	
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		assertEquals(validId, messageSID, "The surveyId parsed from message payload"
				+ " is the same one we entered");
		Mockito.verify(messageHandler.getRepository()).findAllBySurveyUuid(UUID.fromString(messageSID));
		verify(messageHandler.getSqsSender(), times(1))
		.sendResponse(responses.toString(), UUID.fromString(requestHeader));
//		verify(messageHandler.getSqsSender(), times(1)).sendResponse(res, UUID.fromString(requestHeader));

		applicationContext.close();
	}
	
	@Test
	void testReceiverInvalidSurveyId() throws ParseException {
		// Invalid survey ID in payload
		payload = "{\n"
				+ "  \"uuid\": null,\n"
				+ "  \"batch\": null,\n"
				+ "  \"date\": null,\n"
				+ "  \"surveyUuid\": \"d50ca970-14ac-11ec-a00f\"\n"
				+ "}";

		String messageSID = "";
		String errMessage = "Invalid format for UUID, unable to parse message";
		message = MessageBuilder.withPayload(payload)
				.setHeader("MessageId", requestHeader).build();
		invalidId = "d50ca970-14ac-11ec-a00f";
		
        StaticApplicationContext applicationContext = new StaticApplicationContext();
        applicationContext.registerSingleton("incomingMessageHandler", SqsReceiver.class);
        applicationContext.refresh();      

        SqsReceiver messageHandler = applicationContext.getBean(SqsReceiver.class);
		
        messageHandler.setResponseRepo(repository); 
        messageHandler.setSqsSender(sqsSender);
        
		messageHandler.receiveMessage(message);
		
		try {
			JSONObject obj = new JSONObject(message.getPayload());
			messageSID = obj.getString("surveyUuid");	
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		assertEquals(invalidId, messageSID, "The surveyId parsed from message payload"
				+ " is the same one we entered");

		Mockito.verifyNoInteractions(messageHandler.getRepository());
		verify(messageHandler.getSqsSender(), times(1))
		.sendResponse(errMessage, UUID.fromString(requestHeader));

		applicationContext.close();
	}

	@Test
	void testReceiverValidBatch() throws ParseException {
		// Valid batch in payload
		payload = "{\n"
				+ "  \"uuid\": null,\n"
				+ "  \"batch\": \"Mock Batch 42\",\n"
				+ "  \"date\": null,\n"
				+ "  \"surveyUuid\": null\n"
				+ "}";
		
		validBatch = "Mock Batch 42";
		String messageBatch = "";
		
		message = MessageBuilder.withPayload(payload)
				.setHeader("MessageId", requestHeader).build();
		
        StaticApplicationContext applicationContext = new StaticApplicationContext();
        applicationContext.registerSingleton("incomingMessageHandler", SqsReceiver.class);
        applicationContext.refresh();
        

        SqsReceiver messageHandler = applicationContext.getBean(SqsReceiver.class);
		
        messageHandler.setResponseRepo(repository); 
        messageHandler.setSqsSender(sqsSender);
        
        Response r = new Response();
        responses = new ArrayList<>();
        responses.add(r);
        
		Mockito.when(messageHandler.getRepository().findAllByBatch(validBatch))
		.thenReturn(Flux.just(r));
				  
		Mockito.doNothing().when(messageHandler.getSqsSender())
		.sendResponse(responses.toString(), UUID.fromString(requestHeader));

		messageHandler.receiveMessage(message);
		
		try {
			JSONObject obj = new JSONObject(message.getPayload());
			messageBatch = obj.getString("batch");	
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		assertEquals(validBatch, messageBatch, "The batch parsed from message payload"
				+ " is the same one we entered");
		Mockito.verify(messageHandler.getRepository()).findAllByBatch(validBatch);
		verify(messageHandler.getSqsSender(), times(1)).
		sendResponse(responses.toString(), UUID.fromString(requestHeader));

		applicationContext.close();
	}

	@Test
	void testReceiverInvalidBatch() throws ParseException {
		// Invalid batch in payload
		payload = "{\n"
				+ "  \"uuid\": null,\n"
				+ "  \"batch\": \"Invalid Batch\",\n"
				+ "  \"date\": null,\n"
				+ "  \"surveyUuid\": null\n"
				+ "}";
		
		invalidBatch = "Invalid Batch";
		String messageBatch = "";
		
		message = MessageBuilder.withPayload(payload)
				.setHeader("MessageId", requestHeader).build();
		
        StaticApplicationContext applicationContext = new StaticApplicationContext();
        applicationContext.registerSingleton("incomingMessageHandler", SqsReceiver.class);
        applicationContext.refresh();
        

        SqsReceiver messageHandler = applicationContext.getBean(SqsReceiver.class);
		
        messageHandler.setResponseRepo(repository); 
        messageHandler.setSqsSender(sqsSender);
        		  
		Mockito.when(messageHandler.getRepository().findAllByBatch(invalidBatch))
		.thenReturn(Flux.empty());
		
		responses = new ArrayList<>();
		Mockito.doNothing().when(messageHandler.getSqsSender())
		.sendResponse(responses.toString(), UUID.fromString(requestHeader));

		messageHandler.receiveMessage(message);
		
		try {
			JSONObject obj = new JSONObject(message.getPayload());
			messageBatch = obj.getString("batch");	
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		assertEquals(invalidBatch, messageBatch, "The batch parsed from message payload"
				+ " is the same one we entered");
		Mockito.verify(messageHandler.getRepository()).findAllByBatch(invalidBatch);
		verify(messageHandler.getSqsSender(), times(1))
		.sendResponse(responses.toString(), UUID.fromString(requestHeader));

		applicationContext.close();
	}
	
	@Test
	void testReceiverValidDate() throws ParseException {
		// Valid date in payload
		payload = "{\n"
				+ "  \"uuid\": null,\n"
				+ "  \"batch\": null,\n"
				+ "  \"date\": \"2020-04-27\",\n"
				+ "  \"surveyUuid\": null\n"
				+ "}";
				
		message = MessageBuilder.withPayload(payload)
				.setHeader("MessageId", requestHeader).build();
		
		validDate = dateTimeFormat.parse("2020-04-27");
		Date messageDate = null;
		Date endDate = null;
		try {
			JSONObject obj = new JSONObject(message.getPayload());
			messageDate = dateTimeFormat.parse(obj.getString("date"));	
			cal.setTime(messageDate);
			cal.add(Calendar.DATE, 7);
			endDate = cal.getTime();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
        StaticApplicationContext applicationContext = new StaticApplicationContext();
        applicationContext.registerSingleton("incomingMessageHandler", SqsReceiver.class);
        applicationContext.refresh();
        

        SqsReceiver messageHandler = applicationContext.getBean(SqsReceiver.class);
		
        messageHandler.setResponseRepo(repository); 
        messageHandler.setSqsSender(sqsSender);
        
        Response r = new Response();
        responses = new ArrayList<>();
        responses.add(r);
        
		Mockito.when(messageHandler.getRepository().findAllByWeek(messageDate, endDate))
		.thenReturn(Flux.just(r));
				  
		Mockito.doNothing().when(messageHandler.getSqsSender())
		.sendResponse(responses.toString(), UUID.fromString(requestHeader));

		messageHandler.receiveMessage(message);
		
		assertEquals(validDate, messageDate, "The date parsed from message payload"
				+ " is the same one we entered");
		Mockito.verify(messageHandler.getRepository()).findAllByWeek(messageDate, endDate);
		verify(messageHandler.getSqsSender(), times(1))
		.sendResponse(responses.toString(), UUID.fromString(requestHeader));

		applicationContext.close();
	}
	
	@Test
	void testReceiverInvalidDate() throws ParseException {
		// Invalid date in payload
		payload = "{\n"
				+ "  \"uuid\": null,\n"
				+ "  \"batch\": null,\n"
				+ "  \"date\": \"2020-04\",\n"
				+ "  \"surveyUuid\": null,\n"
				+ "  \"responses\": null\n"
				+ "}";
				
		String errMessage = "Invalid date format, unable to parse date parameter";
		message = MessageBuilder.withPayload(payload)
				.setHeader("MessageId", requestHeader).build();
	
		Date messageDate = null;
		try {
			invalidDate = dateTimeFormat.parse("2020-04");
			JSONObject obj = new JSONObject(message.getPayload());
			messageDate = dateTimeFormat.parse(obj.getString("date"));	
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Invalid date found in Test Receiver");
		}
		
        StaticApplicationContext applicationContext = new StaticApplicationContext();
        applicationContext.registerSingleton("incomingMessageHandler", SqsReceiver.class);
        applicationContext.refresh();
        

        SqsReceiver messageHandler = applicationContext.getBean(SqsReceiver.class);
        messageHandler.setResponseRepo(repository); 
        messageHandler.setSqsSender(sqsSender);
        
		messageHandler.receiveMessage(message);
		
		assertEquals(invalidDate, messageDate, "The date parsed from message payload"
				+ " is the same one we entered");
		Mockito.verifyNoInteractions(messageHandler.getRepository());
		verify(messageHandler.getSqsSender(), times(1))
		.sendResponse(errMessage, UUID.fromString(requestHeader));

		applicationContext.close();
	}
}
