package com.twa.flights.api.clusters.configuration.zookeeper;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "zookeeper")
@Configuration
public class ZookeeperConfiguration {
    private final String host;
    private final int port = 2181;
    private final int maxRetries;
    private final int timeBetweenRetries;
    private final int connectionTimeout;
}
