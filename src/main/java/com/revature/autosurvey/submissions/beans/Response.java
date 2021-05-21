package com.revature.autosurvey.submissions.beans;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import lombok.Data;

@Table("response")
@Data
public class Response {
	@PrimaryKeyColumn(
			name="responseId",
			ordinal=0,
			type = PrimaryKeyType.PARTITIONED,
			ordering = Ordering.DESCENDING) 
	private UUID responseId;
	@PrimaryKeyColumn(
			name="batchName",
			ordinal=1,
			type = PrimaryKeyType.CLUSTERED,
			ordering = Ordering.DESCENDING)
	private String batchName;
	@PrimaryKeyColumn(
			name="week",
			ordinal=2,
			type = PrimaryKeyType.CLUSTERED,
			ordering = Ordering.DESCENDING) 
	private trainingWeek week;
	@Column 
	private UUID surveyId;
	@Column
	private Map<String, String> surveyResponses;
	
	public enum trainingWeek implements Serializable{
		A, B, ONE, TWO, THREE, FOUR, FIVE, SIX,
		SEVEN, EIGHT, NINE, TEN, ELEVEN, TWELVE;
	}

}
