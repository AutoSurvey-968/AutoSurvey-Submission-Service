package com.revature.autosurvey.submissions.beans;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import lombok.Data;

@Data
@Table("response")
public class Response {
	@PrimaryKeyColumn(
			name="uuid",
			ordinal=2,
			type = PrimaryKeyType.CLUSTERED,
			ordering = Ordering.DESCENDING) 
	private UUID uuid;
	@PrimaryKeyColumn(
			name="batch",
			ordinal=0,
			type = PrimaryKeyType.CLUSTERED,
			ordering = Ordering.DESCENDING)
	private String batch;
	@PrimaryKeyColumn(
			name="date",
			ordinal=1,
			type = PrimaryKeyType.PARTITIONED,
			ordering = Ordering.DESCENDING)
	private Date date;
	@Column 
	private UUID surveyUuid;
	@Column
	private Map<String, String> responses;

}
