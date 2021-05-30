package com.revature.autosurvey.submissions.data;

import java.util.Date;
import java.util.UUID;

import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;

import com.revature.autosurvey.submissions.beans.Response;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ResponseRepository extends ReactiveCassandraRepository<Response, UUID> {
	@AllowFiltering
	Mono<Response> findByUuid(UUID uuid);

	@AllowFiltering
	Flux<Response> findAllByBatch(String batch);

	Flux<Response> findAllByDate(Date date);

	@AllowFiltering
	Mono<Response> deleteByUuid(UUID uuid);

	@AllowFiltering
	Flux<Response> findAllByBatchAndDate(String batch, Date date);
}
