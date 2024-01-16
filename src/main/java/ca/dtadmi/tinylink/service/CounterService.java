package ca.dtadmi.tinylink.service;

import ca.dtadmi.tinylink.exception.ApiRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.recipes.shared.SharedCount;
import org.apache.curator.retry.RetryNTimes;

@Service
public class CounterService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final AtomicInteger count = new AtomicInteger(1);

    @Value("${zookeeper.server.url}")
    private String zookeeperUrl;
    @Value("${zookeeper.server.port}")
    private String zookeeperPort;

    public static Integer getCount() {
        return count.incrementAndGet();
    }
    public Integer getCountFromZookeeper() {
        try (CuratorFramework client = CuratorFrameworkFactory.newClient(zookeeperUrl + ":" + zookeeperPort, 10000, 10000, new RetryNTimes(3, 1000))) {
            //client = CuratorFrameworkFactory.builder().connectString(zookeeperUrl + ":" + zookeeperPort).retryPolicy(new RetryNTimes(3, 1000)).build();
            /*CuratorFramework client = CuratorFrameworkFactory.newClient(
                    zookeeperUrl+":"+zookeeperPort,
                    new RetryNTimes(2, 1000))*/
            client.start();
            client.blockUntilConnected();
            return counter(client);
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            throw new ApiRuntimeException("Zookeeper client error : " + e.getMessage(), new Date(), e);
        }
    }

    private int counter(CuratorFramework client) throws Exception {
        logger.debug("********going to implement counter*******");
        client = client.usingNamespace("counter");
        SharedCount sharedCount = new SharedCount(client, "/counter", 1);
        sharedCount.start();
        InterProcessMutex lock = new InterProcessMutex(client, "/counter-lock");

        try {
            lock.acquire();
            int currentCount = sharedCount.getCount();
            logger.debug("Current count value: {}", currentCount);
            sharedCount.trySetCount(sharedCount.getVersionedValue(), currentCount + 1);
            logger.debug("New count value: {}", sharedCount.getCount());
            return currentCount;
        } finally {
            lock.release();
        }
    }
}
