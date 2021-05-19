package com.revature.autosurvey.submissions.data;

import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;

import com.revature.autosurvey.submissions.beans.Response;

@Repository
public interface ResponseRepository extends ReactiveCassandraRepository<Response, Integer>{
//We might have to change this primary key
}
