package com.seonWKim.sharder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.seonWKim.sharder.config.SharderApplicationProperties;

@ConfigurationPropertiesScan
@EnableConfigurationProperties(SharderApplicationProperties.class)
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class SharderApplicationExample {
    public static void main(String[] args) {
        SpringApplication.run(SharderApplicationExample.class, args);
    }
}