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
	public Mono<Response> findByUuid(UUID uuid);

	public Flux<Response> findAllByBatch(String batch);

	@AllowFiltering
	public Flux<Response> findAllByWeek(TrainingWeek week);

	@AllowFiltering
	Mono<Response> deleteByUuid(UUID uuid);
}
