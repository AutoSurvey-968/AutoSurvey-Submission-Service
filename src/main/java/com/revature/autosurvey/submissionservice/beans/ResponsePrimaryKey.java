package com.revature.autosurvey.submissionservice.beans;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

public class ResponsePrimaryKey {
	@PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED)
	private int id;
	@PrimaryKeyColumn(ordinal = 0)
	private String week;
	@PrimaryKeyColumn(ordinal = 1)
	private int batch;
}
