package com.revature.autosurvey.submissions.data;

import java.util.UUID;

import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;

import com.revature.autosurvey.submissions.beans.Response;

import reactor.core.publisher.Flux;

@Repository
public interface ResponseRepository extends ReactiveCassandraRepository<Response, UUID>{
	@AllowFiltering
	public Flux<Response> findAllByBatch(String batch);
}
