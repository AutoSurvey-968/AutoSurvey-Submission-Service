package com.revature.autosurvey.submissionservice.beans;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

@PrimaryKeyClass
public class ResponsePrimaryKey {
	
	@PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED)
	private int id;
	
	@PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED, ordinal = 0)
	private String week;
	
	@PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED, ordinal = 1)
	private String batch;

}
