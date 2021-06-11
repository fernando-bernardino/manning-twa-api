package com.twa.flights.api.clusters.configuration.zookeeper;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ZookeeperCuratorBeanConfiguration {

  @Bean
  public ZooKeeperCuratorConfiguration zooKeeperCuratorConfiguration(ZookeeperConfiguration configuration) {
    return new ZooKeeperCuratorConfiguration(curatorClient(configuration));
  }

  private CuratorFramework curatorClient(ZookeeperConfiguration zookeeperSettings) {
    RetryPolicy retryPolicy = new RetryNTimes(zookeeperSettings.getMaxRetries(),
      zookeeperSettings.getTimeBetweenRetries());

    CuratorFramework client = CuratorFrameworkFactory.builder()
      .connectString(zookeeperSettings.getHost() + ":" + zookeeperSettings.getPort())
      .connectionTimeoutMs(zookeeperSettings.getConnectionTimeout()).retryPolicy(retryPolicy).build();

    client.start();

    return client;
  }
}
