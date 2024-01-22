package ca.dtadmi.tinylink.service;

import ca.dtadmi.tinylink.exception.ApiRuntimeException;
import lombok.Setter;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.recipes.shared.SharedCount;
import org.apache.curator.retry.RetryNTimes;

@Service
@Setter
public class CounterService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final AtomicInteger count = new AtomicInteger(1);

    @Value("${zookeeper.server.url}")
    private String zookeeperUrl;
    @Value("${zookeeper.server.port}")
    private String zookeeperPort;

    public static synchronized Integer getCount() {
        return count.incrementAndGet();
    }
    public synchronized Integer getCountFromZookeeper() {
        try (CuratorFramework client = CuratorFrameworkFactory.newClient(zookeeperUrl + ":" + zookeeperPort, 3000, 3000, new RetryNTimes(3, 1000))) {
            if(client==null) {
                throw new ApiRuntimeException("Zookeeper client could not be created at " + zookeeperUrl + ":" + zookeeperPort, new Date());
            }
            client.start();
            if(!client.getState().equals(CuratorFrameworkState.STARTED)) {
                throw new ApiRuntimeException("Zookeeper client not responding", new Date());
            }
            boolean connected = client.blockUntilConnected(10, TimeUnit.SECONDS);
            if(!connected) {
                throw new ApiRuntimeException("Zookeeper client not responding", new Date());
            }
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
