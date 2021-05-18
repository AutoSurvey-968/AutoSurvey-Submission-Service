package com.revature.autosurvey.submissionservice.beans;

import java.sql.Timestamp;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table
public class Response {
	
	@PrimaryKey private int id;
	@Column private Timestamp timestamp; //potentially don't save as actual timestamp
	@Column private String name;
	@Column private String email;
	@Column private Weeks week;
	@Column private int satisfaction;
	@Column private String overallFeedback;
	@Column private String trainingIssues;
	@Column private Location location;
	@Column private String batch; //perhaps should be a number
	@Column private int requirmentClarity;
	@Column private int preparedness;
	@Column private String projectFeedback;
	@Column private String userBackground;
	@Column private int programmingExp;
	@Column private int levelOfUnderstanding;
	@Column private Pacing pacing;
	@Column private Agreeance helpfulMaterial;
	@Column private Agreeance wellOrganized;
	@Column private Agreeance questionsEncouraged;
	@Column private Agreeance trainingExpectations;
	@Column private Boolean spark;
	@Column private Boolean lastWeekAssessment;
	
	

}
