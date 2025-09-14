package com.kishore.taskmanager.integration;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.DYNAMODB;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
public class LocalStackTestConfig {

    public static final LocalStackContainer LOCALSTACK = new LocalStackContainer(DockerImageName.parse("localstack/localstack"))
            .withServices(DYNAMODB);

    static {
        LOCALSTACK.start(); // Ensures container is running before Spring binds properties
    }

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("aws.dynamodb.endpoint", () -> LOCALSTACK.getEndpointOverride(DYNAMODB).toString());
        registry.add("aws.dynamodb.region", () -> LOCALSTACK.getRegion().toString());
    }
    
    public static LocalStackContainer getContainer() {
        return LOCALSTACK;
    }
}
