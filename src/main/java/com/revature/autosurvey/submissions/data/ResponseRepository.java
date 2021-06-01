package com.revature.autosurvey.submissions.data;

import java.util.Date;
import java.util.UUID;

import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;

import com.revature.autosurvey.submissions.beans.Response;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ResponseRepository extends ReactiveCassandraRepository<Response, Date> {
	@AllowFiltering
	Mono<Response> findByUuid(UUID uuid);

	@AllowFiltering
	Flux<Response> findAllByBatch(String batch);

	@Query("SELECT * FROM response r WHERE r.date >= %?1 AND r.date <= %?2")
	Flux<Response> findAllByWeek(Date startDate, Date endDate);
	
	@Query("SELECT * FROM response r WHERE r.date >= %?2 AND r.date <= %?2 AND r.batch = %?1")
	Flux<Response> findAllByBatchAndWeek(String batch, Date startDate, Date endDate);

	@AllowFiltering
	Mono<Response> deleteByUuid(UUID uuid);
}
