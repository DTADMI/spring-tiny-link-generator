package ca.dtadmi.tinylink.service;

import ca.dtadmi.tinylink.exception.ApiRuntimeException;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.recipes.shared.SharedCount;
import org.apache.curator.retry.RetryNTimes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CounterServiceTest {
    @InjectMocks
    private CounterService counterService;

    @Mock
    private CuratorFramework client;

    @Test
    @DisplayName("Should successfully connect to Zookeeper and retrieve count")
    void test_successfully_connects_to_zookeeper_and_retrieves_count() throws Exception {
        int expectedCount = 7;
        try(MockedStatic<CuratorFrameworkFactory> mocked = mockStatic(CuratorFrameworkFactory.class)) {

            mocked.when(() -> CuratorFrameworkFactory.newClient(anyString(), anyInt(), anyInt(), any(RetryNTimes.class))).thenReturn(client);
            doNothing().when(client).start();
            when(client.getState()).thenReturn(CuratorFrameworkState.STARTED);
            when(client.blockUntilConnected(anyInt(), any())).thenReturn(true);
            when(client.usingNamespace(anyString())).thenReturn(client);
            try (MockedConstruction<SharedCount> ignored = mockConstruction(SharedCount.class,
                    (mock, context) -> {
                        // further stubbings ...
                        doNothing().when(mock).start();
                        when(mock.getCount()).thenReturn(expectedCount);
                        when(mock.trySetCount(any(), anyInt())).thenReturn(true);
                    })) {

                try (MockedConstruction<InterProcessMutex> ignored1 = mockConstruction(InterProcessMutex.class,
                        (mock, context) -> {
                            // further stubbings ...
                            doNothing().when(mock).acquire();
                            doNothing().when(mock).release();
                        })) {


                    assertEquals(client, CuratorFrameworkFactory.newClient("zookeeper.url" + ":" + "1234", 10000, 10000, new RetryNTimes(3, 1000)));

                    Integer count = counterService.getCountFromZookeeper();

                    assertNotNull(count);
                    assertEquals(expectedCount, count);
                }

            }
        }
    }

    @Test
    @DisplayName("Should throw ApiRuntimeException when unable to connect to Zookeeper")
    void test_zookeeper_is_down_method_throws_ApiRuntimeException() {
        counterService.setZookeeperUrl("invalid_url");
        counterService.setZookeeperPort("1234");

        assertThrows(ApiRuntimeException.class, () -> {
            counterService.getCountFromZookeeper();
        });
    }

    @Test
    @DisplayName("Should throw ApiRuntimeException when Zookeeper url is null")
    void test_zookeeper_url_is_null_method_throws_ApiRuntimeException() {
        counterService.setZookeeperPort("2181");

        assertThrows(ApiRuntimeException.class, () -> {
            counterService.getCountFromZookeeper();
        });
    }

    @Test
    @DisplayName("Should throw ApiRuntimeException when Zookeeper port is null")
    void test_zookeeper_port_is_null_method_throws_ApiRuntimeException() {
        counterService.setZookeeperUrl("localhost");

        assertThrows(ApiRuntimeException.class, () -> {
            counterService.getCountFromZookeeper();
        });
    }

    @Test
    @DisplayName("Should throw ApiRuntimeException when Zookeeper url and port are invalid")
    void test_zookeeper_url_and_port_are_invalid_method_throws_ApiRuntimeException() {
        counterService.setZookeeperUrl("invalid_url");
        counterService.setZookeeperPort("invalid_port");

        assertThrows(ApiRuntimeException.class, () -> {
            counterService.getCountFromZookeeper();
        });
    }
}
