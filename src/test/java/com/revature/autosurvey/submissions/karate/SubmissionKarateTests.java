package com.revature.autosurvey.submissions.karate;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import com.intuit.karate.Results;
import com.intuit.karate.Runner;

class SubmissionKarateTests {
	String test = "classpath:/com/revature/autosurvey/surveys/karate/survey-tests.feature";
	@Test
	void testParallel() {
		System.setProperty("karate.env", "dev");
		Results results = Runner.path(test).parallel(5);
		assertEquals(0, results.getFailCount(), results.getErrorMessages());
	}
}
