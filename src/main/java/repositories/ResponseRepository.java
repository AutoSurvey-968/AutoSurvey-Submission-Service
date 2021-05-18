package repositories;

import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;

import com.revature.autosurvey.submissionservice.beans.Response;

@Repository
public interface ResponseRepository extends ReactiveCassandraRepository<Response, Integer>{

}
