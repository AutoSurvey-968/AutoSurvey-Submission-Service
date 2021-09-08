package com.revature.autosurvey.submissions.utils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;

import org.springframework.messaging.Message;

import com.amazonaws.util.json.Jackson;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.autosurvey.submissions.beans.Response;

/**
 * @author igastelum
 *
 */

public class SqsReceiver {
	
	private List<Response> messageData;
	ObjectMapper mapper = new ObjectMapper();
	
	SqsReceiver(){
		messageData = new ArrayList<>();
	}

    @SqsListener(value = SQSNames.SUBMISSIONS_QUEUE, deletionPolicy=SqsMessageDeletionPolicy.ON_SUCCESS)
    public void receiveMessage(Message<String> message) {
    	messageData.add(Jackson.fromJsonString(message.getPayload(), Response.class));
    }
}
