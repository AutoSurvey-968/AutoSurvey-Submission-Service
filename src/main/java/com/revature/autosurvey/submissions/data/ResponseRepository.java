package com.revature.autosurvey.submissions.data;

import java.util.UUID;

import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;

import com.revature.autosurvey.submissions.beans.Response;
import com.revature.autosurvey.submissions.beans.TrainingWeek;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ResponseRepository extends ReactiveCassandraRepository<Response, UUID> {
	@AllowFiltering
	Mono<Response> findByUuid(UUID uuid);

	Flux<Response> findAllByBatch(String batch);

	@AllowFiltering
	Flux<Response> findAllByWeek(TrainingWeek week);

	@AllowFiltering
	Mono<Response> deleteByUuid(UUID uuid);
	
	@AllowFiltering
	Flux<Response> findAllByBatchAndWeek(String batch, String week);
}
