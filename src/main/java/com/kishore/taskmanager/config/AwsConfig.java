package com.kishore.taskmanager.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.regions.Region;

import java.net.URI;

@Configuration
public class AwsConfig {

	/*
	 * @Bean public DynamoDbClient dynamoDbClient(DynamoProperties props) { return
	 * DynamoDbClient.builder() .endpointOverride(URI.create(props.getEndpoint()))
	 * .region(Region.of(props.getRegion())) .build(); }
	 */
    
    @Bean
    public DynamoDbClient dynamoDbClient(DynamoProperties props) {
        return DynamoDbClient.builder()
            .endpointOverride(URI.create(props.getEndpoint()))
            .region(Region.of(props.getRegion())) // ✅ FIXED
            .build();
    }
}

@Component
@ConfigurationProperties(prefix = "aws.dynamodb")
class DynamoProperties {
    private String endpoint;
    private String region;

    public String getEndpoint() {
        return endpoint;
    }
    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getRegion() {
        return region;
    }
    public void setRegion(String region) {
        this.region = region;
    }
}
