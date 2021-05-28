package com.twa.flights.api.clusters.configuration.zookeeper;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

@Component
@RequiredArgsConstructor
public class ZooKeeperCuratorConfiguration {

  @Getter
  private final CuratorFramework client;


  @PreDestroy
  public void close() {
    client.close();
  }
}
