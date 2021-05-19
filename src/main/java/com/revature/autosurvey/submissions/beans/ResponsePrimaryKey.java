package com.revature.autosurvey.submissions.beans;

import java.util.UUID;

import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

@PrimaryKeyClass
public class ResponsePrimaryKey {
	@PrimaryKeyColumn(
			type = PrimaryKeyType.PARTITIONED,
			ordinal = 2,
			ordering = Ordering.DESCENDING)
	private UUID id;
	@PrimaryKeyColumn(
			type = PrimaryKeyType.CLUSTERED,
			ordinal = 1,
			ordering = Ordering.DESCENDING)
	private String week;
	@PrimaryKeyColumn(
			type = PrimaryKeyType.CLUSTERED,
			ordinal = 0,
			ordering = Ordering.DESCENDING)
	private int batch;
}
