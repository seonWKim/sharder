package com.sharder.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sharder.api.SimpleQueryRestController;

import com.linecorp.armeria.server.docs.DocService;
import com.linecorp.armeria.server.logging.AccessLogWriter;
import com.linecorp.armeria.server.logging.LoggingService;
import com.linecorp.armeria.spring.ArmeriaServerConfigurator;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class ArmeriaConfiguration {
    private final SimpleQueryRestController simpleQueryRestController;

    @Bean
    public ArmeriaServerConfigurator armeriaServerConfigurator() {
        return builder -> {
            builder.serviceUnder("/docs", new DocService());
            builder.decorator(LoggingService.newDecorator());
            builder.accessLogWriter(AccessLogWriter.combined(), false);

            builder.annotatedService(simpleQueryRestController);
            // builder.service(THttpService.of(...));
            // builder.service(GrpcService.builder()...build());
        };
    }
}
