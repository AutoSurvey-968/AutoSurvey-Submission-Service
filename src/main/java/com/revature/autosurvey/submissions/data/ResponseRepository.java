package com.revature.autosurvey.submissions.data;

import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;

import com.revature.autosurvey.submissions.beans.Response;
import com.revature.autosurvey.submissions.beans.ResponsePrimaryKey;

import reactor.core.publisher.Flux;

@Repository
public interface ResponseRepository extends ReactiveCassandraRepository<Response, ResponsePrimaryKey>{
	@AllowFiltering
	public Flux<Response> findAllByKeyBatch(int batch);
	@AllowFiltering
	public Flux<Response> findAllByKeyWeek(String week);
}
