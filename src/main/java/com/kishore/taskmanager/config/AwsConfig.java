package com.kishore.taskmanager.config;

import java.net.URI;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;

@Configuration
public class AwsConfig {
	@Bean
	public DynamoDbClient dynamoDbClient(DynamoProperties props) {

		System.out.println("🔧 DynamoDB Endpoint: " + props.getEndpoint());
		System.out.println("🔧 DynamoDB Region: " + props.getRegion());

		DynamoDbClientBuilder builder = DynamoDbClient.builder()
				.region(Region.of(props.getRegion()));

		if (props.getEndpoint() != null && !props.getEndpoint().isBlank()) {
			builder.endpointOverride(URI.create(props.getEndpoint()));
		}

		return builder.build();
	}
}
