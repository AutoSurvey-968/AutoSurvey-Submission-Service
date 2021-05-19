package com.revature.autosurvey.submissions.data;

import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;

import com.revature.autosurvey.submissions.beans.Response;
import com.revature.autosurvey.submissions.beans.ResponsePrimaryKey;

@Repository
public interface ResponseRepository extends ReactiveCassandraRepository<Response, ResponsePrimaryKey>{
//We might have to change this primary key
}
