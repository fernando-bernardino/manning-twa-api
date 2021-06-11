package com.twa.flights.api.clusters.service;

import com.twa.flights.api.clusters.configuration.zookeeper.ZooKeeperCuratorConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.barriers.DistributedBarrier;
import org.apache.zookeeper.*;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class ZooKeeperService {
  private final ZooKeeperCuratorConfiguration configuration;

  public boolean checkIfBarrierExists(String barrierName) {
    try {
      return configuration.getClient().checkExists().forPath(barrierName) != null;
    } catch (Exception e) {
      log.error("Failed to check for barrier", e);
      return false;
    }
  }

  public boolean createBarrier(String barrierName) {
    try {
      getBarrier(barrierName).setBarrier();
      log.info("created barrier {}", barrierName);
      return true;

    } catch (Exception e) {
      log.error("Failed to create barrier" + barrierName, e);
      return false;
    }
  }

  public void deleteBarrier(String barrierName) {
    try {
      if (checkIfBarrierExists(barrierName)) {
        getCuratorClient().delete().quietly().forPath(barrierName);
      }
    } catch (Exception e) {
      log.error("Failed to delete barrier" + barrierName, e);
    }
  }

  private CuratorFramework getCuratorClient() {
    return configuration.getClient();
  }

  public void waitOnBarrier(String barrierName) {
    try {
      log.info("waiting on barrier {}", barrierName);
      getBarrier(barrierName).waitOnBarrier(5000, TimeUnit.MILLISECONDS);
      log.info("finished waiting on barrier {}", barrierName);
    } catch (Exception e) {
      log.error("waitOnBarrier failed for barrier" + barrierName, e);
    }
  }

  private DistributedBarrier getBarrier(String barrierName) throws Exception {
    return new DistributedBarrier(getCuratorClient(), barrierName) {
      @Override
      public synchronized void setBarrier() throws Exception {
        try {
          getCuratorClient().create()
            .creatingParentContainersIfNeeded()
            .withMode(CreateMode.EPHEMERAL)
            .forPath(barrierName);
        } catch (KeeperException.NodeExistsException e) {
          log.error("Node already exists for path " + barrierName, e);
          throw e;
        }
      }
    };
  }
}
