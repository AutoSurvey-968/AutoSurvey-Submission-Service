package com.revature.autosurvey.submissions.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazon.sqs.javamessaging.AmazonSQSExtendedClient;
import com.amazon.sqs.javamessaging.ExtendedClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.BucketLifecycleConfiguration;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Configuration
public class AwsS3Config {

	@Value("${cloud.aws.credentials.s3-bucket}")
	private String s3BucketName;
	
	@Value("${cloud.aws.credentials.s3-access-key}")
    private String awsAccessKey;
   
    @Value("${cloud.aws.credentials.s3-secret-key}")
    private String awsSecretKey;
    
	private AWSCredentials awsCreds;  

	@Bean("AmazonS3")
    public AmazonS3 s3Client() {
    	awsCreds = new BasicAWSCredentials(
      		  awsAccessKey,
      		  awsSecretKey);
        /*set a lifecycle rule on the
         * bucket to permanently delete objects 1 day after each object's
         * creation date.
         */
     	
        final BucketLifecycleConfiguration.Rule expirationRule =
                new BucketLifecycleConfiguration.Rule();
        expirationRule.withExpirationInDays(1).withStatus("Enabled");
       
        final BucketLifecycleConfiguration lifecycleConfig =
                new BucketLifecycleConfiguration().withRules(expirationRule);    		            
      
        AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .withRegion(Regions.US_EAST_2)
                .build();
        
        // Create S3 bucket if it does not exist
        if(!s3.doesBucketExistV2(s3BucketName)) {
            log.debug("S3 Bucket with give name not found.\n"
              + "Creating one..");
            s3.createBucket(s3BucketName);
        }
        
        s3.setBucketLifecycleConfiguration(s3BucketName, lifecycleConfig);
        
        return s3;

    }
    
	@Bean("AmazonSQS")
    public AmazonSQS amazonSQS(AmazonS3 s3) {
    	awsCreds = new BasicAWSCredentials(
      		  awsAccessKey,
      		  awsSecretKey);
    	
       // Build AmazonSQS client with extended configuration
       final ExtendedClientConfiguration extendedClientConfig =
    	            new ExtendedClientConfiguration()
    	            .withPayloadSupportEnabled(s3, s3BucketName);

       AmazonSQS sqs = AmazonSQSClientBuilder.standard()
               .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
               .withRegion(Regions.US_EAST_2)
               .build();
       
    	    return new AmazonSQSExtendedClient(sqs, extendedClientConfig);  
    }

}
