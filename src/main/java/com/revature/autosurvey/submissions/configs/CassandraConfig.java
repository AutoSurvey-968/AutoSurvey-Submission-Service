package com.revature.autosurvey.submissions.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.cassandra.SessionFactory;
import org.springframework.data.cassandra.config.CqlSessionFactoryBean;
import org.springframework.data.cassandra.config.SchemaAction;
import org.springframework.data.cassandra.config.SessionFactoryFactoryBean;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.core.convert.CassandraConverter;
import org.springframework.data.cassandra.core.convert.MappingCassandraConverter;
import org.springframework.data.cassandra.core.mapping.CassandraMappingContext;
import org.springframework.data.cassandra.core.mapping.SimpleUserTypeResolver;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.config.DriverConfigLoader;

@Configuration
@EnableCassandraRepositories(basePackages = { "com.revature.autosurvey.submissions.data" })
public class CassandraConfig {
	
	@Value("${cloud.aws.credentials.access-key}")
    private String awsAccessKey;
 
   
    @Value("${cloud.aws.credentials.secret-key}")
    private String awsSecretKey;
    
	@Bean
	public CqlSessionFactoryBean session() {
		CqlSessionFactoryBean factory = new CqlSessionFactoryBean();
		DriverConfigLoader loader = DriverConfigLoader.fromClasspath("application.conf");
		factory.setSessionBuilderConfigurer(builder -> builder.withConfigLoader(loader).withKeyspace("\"AutoSurvey\""));
		factory.setKeyspaceName("\"AutoSurvey\"");
		return factory;
	}

	@Bean
	public SessionFactoryFactoryBean sessionFactory(CqlSession session, CassandraConverter converter) {
		SessionFactoryFactoryBean sessionFactory = new SessionFactoryFactoryBean();
		((MappingCassandraConverter) converter).setUserTypeResolver(new SimpleUserTypeResolver(session));
		sessionFactory.setSession(session);
		sessionFactory.setConverter(converter);
		sessionFactory.setSchemaAction(SchemaAction.CREATE_IF_NOT_EXISTS);

		return sessionFactory;
	}

	@Bean
	public CassandraMappingContext mappingContext(CqlSession cqlSession) {

		return new CassandraMappingContext();
	}

	@Bean
	public CassandraConverter converter(CassandraMappingContext mappingContext) {
		return new MappingCassandraConverter(mappingContext);
	}

	@Bean
	public CassandraOperations cassandraTemplate(SessionFactory sessionFactory, CassandraConverter converter) {
		return new CassandraTemplate(sessionFactory, converter);
	}
	
	@Bean
    @Primary
    public AmazonSQSAsync amazonSQSAsync() {
        return AmazonSQSAsyncClientBuilder
                .standard()
                .withRegion(Regions.US_EAST_1)
                .withCredentials(new AWSStaticCredentialsProvider(
                        new BasicAWSCredentials(awsAccessKey, awsSecretKey)))
                .build();
    }

	
	
}
