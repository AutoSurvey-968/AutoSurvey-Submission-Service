package com.revature.autosurvey.submissions.beans;

import java.sql.Timestamp;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table
public class Response {
	@PrimaryKey private ResponsePrimaryKey key;
	@Column private Timestamp timestamp;
	@Column private String name;
	@Column private String email;
	@Column private int satisfaction;
	@Column private String overallFeedback;
	@Column private String trainingIssues;
	@Column private String location;
	@Column private String requirementClarity;
	@Column private int projectPreparedness;
	@Column private String projectFeedback;
	@Column private String background;
	@Column private int programmingExperience;
	@Column private int understandingLastWeek;
	@Column private String batchName;
	@Column private String pacing; //toofast, good, tooslow
	@Column private String materialHelpful; //agreenace
	@Column private String trainingOrganization; //agreeance
	@Column private String questionsEncouraged; //agreeance
	@Column private Boolean trainingMetExpectations;
	@Column private Boolean assessmentLastWeek;

}
