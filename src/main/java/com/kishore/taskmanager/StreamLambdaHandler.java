package com.kishore.taskmanager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.amazonaws.serverless.exceptions.ContainerInitializationException;
import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.spring.SpringBootProxyHandlerBuilder;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;

public class StreamLambdaHandler implements RequestStreamHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(StreamLambdaHandler.class);

    private static SpringBootProxyHandlerBuilder<AwsProxyRequest> handlerBuilder;

    static {
    	
    	long startTime = System.currentTimeMillis();
    	
        handlerBuilder = new SpringBootProxyHandlerBuilder<AwsProxyRequest>()
		    .defaultProxy()
		    .asyncInit()
		    .springBootApplication(TaskManagerApplication.class);
        
        long endTime = System.currentTimeMillis();
        
        logger.info("Spring Boot Lambda container initialized in {} ms", (endTime - startTime));
        
    }

    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
        try {
			
        	handlerBuilder.build().proxyStream(input, output, context);
			
			logger.info("Handling request for function: {}", context.getFunctionName());
			
		} catch (IOException | ContainerInitializationException e) {
			 e.printStackTrace();
	         throw new RuntimeException("Could not initialize Spring Boot application", e);
		}
    }
}
