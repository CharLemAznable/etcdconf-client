package com.github.charlemaznable.etcdconf;

import io.etcd.jetcd.watch.WatchEvent;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class EtcdConfigListenerTest {

    @Test
    public void testEtcdConfigListener() {
        val counter = new AtomicInteger();
        val testConfig = EtcdConfigService.getConfig("test");

        EtcdConfigChangeListener addListener = event -> {
            assertEquals("aaa", event.getValue());
            assertEquals(WatchEvent.EventType.PUT, event.getEventType());
            counter.incrementAndGet();
        };
        testConfig.addChangeListener("aaa", addListener);
        testConfig.addChangeListener("aaa", addListener);

        MockEtcdServer.setUpMockServer();
        MockEtcdServer.addOrModifyProperty("test", "aaa", "aaa");
        await().forever().until(() -> counter.get() == 2);

        testConfig.removeChangeListener(addListener);

        EtcdConfigChangeListener modListener = event -> {
            assertEquals("AAA", event.getValue());
            assertEquals(WatchEvent.EventType.PUT, event.getEventType());
            counter.incrementAndGet();
        };
        testConfig.addChangeListener("aaa", modListener);
        testConfig.addChangeListener("aaa", modListener);

        MockEtcdServer.addOrModifyProperty("test", "aaa", "AAA");
        await().forever().until(() -> counter.get() == 4);

        testConfig.removeChangeListener(modListener);

        EtcdConfigChangeListener delListener = event -> {
            assertEquals("", event.getValue());
            assertEquals(WatchEvent.EventType.DELETE, event.getEventType());
            counter.incrementAndGet();
        };
        testConfig.addChangeListener("aaa", delListener);
        testConfig.addChangeListener("aaa", delListener);

        MockEtcdServer.deleteProperty("test", "aaa");
        await().forever().until(() -> counter.get() == 6);
        MockEtcdServer.tearDownMockServer();

        testConfig.removeChangeListener(delListener);
    }
}
