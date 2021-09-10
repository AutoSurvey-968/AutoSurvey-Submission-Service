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

	@Query("SELECT * FROM response WHERE date >= ?0 AND date <= ?1 ALLOW FILTERING")
	Flux<Response> findAllByWeek(Date startDate, Date endDate);
	
	@Query("SELECT * FROM response WHERE \"batch\" = ?0 AND date >= ?1 AND date <= ?2 ALLOW FILTERING")
	Flux<Response> findAllByBatchAndWeek(String batch, Date startDate, Date endDate);

	@AllowFiltering
	Mono<Response> deleteByUuid(UUID uuid);
	
	@Query("SELECT * FROM response WHERE surveyuuid= ?0 ALLOW FILTERING")
	Flux<Response> findAllBySurveyUuid(UUID uuid);
}
