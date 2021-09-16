package com.revature.autosurvey.submissions.utils;

public class SQSNames {
	
	public static final String ANALYTICS_QUEUE = "https://sqs.us-east-1.amazonaws.com/855430746673/AnalyticsQueue";
	public static final String SURVEY_QUEUE = "https://sqs.us-east-1.amazonaws.com/855430746673/SubmissionQueue";
	public static final String SUBMISSIONS_QUEUE = "https://sqs.us-east-1.amazonaws.com/855430746673/SurveyQueue";
	public static final String TEST_QUEUE = "https://sqs.us-east-1.amazonaws.com/855430746673/TestQueue";

	private SQSNames() {
		
	}
}
