package com.github.charlemaznable.etcdconf;

import com.github.charlemaznable.etcdconf.test.EmbeddedEtcdCluster;
import io.etcd.jetcd.watch.WatchEvent;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class EtcdConfigListenerTest {

    @Test
    public void testEtcdConfigListener() {
        val testConfig = EtcdConfigService.getConfig("test");
        val counter = new AtomicInteger();

        EtcdConfigService.setUpTestMode();
        EmbeddedEtcdCluster.addOrModifyProperty("test", "aaa", "aaa");
        EtcdConfigChangeListener aaaListener = event -> {
            assertEquals("AAA", event.getValue());
            assertEquals(WatchEvent.EventType.PUT, event.getEventType());
            counter.incrementAndGet();
        };
        testConfig.addChangeListener("aaa", aaaListener);
        testConfig.addChangeListener("aaa", aaaListener);
        EmbeddedEtcdCluster.addOrModifyProperty("test", "aaa", "AAA");
        await().forever().until(() -> counter.get() == 2);
        EtcdConfigService.tearDownTestMode();

        EtcdConfigService.setUpTestMode();
        EmbeddedEtcdCluster.addOrModifyProperty("test", "bbb", "bbb");
        EtcdConfigChangeListener bbbListener = event -> {
            assertEquals("", event.getValue());
            assertEquals(WatchEvent.EventType.DELETE, event.getEventType());
            counter.incrementAndGet();
        };
        testConfig.addChangeListener("bbb", bbbListener);
        testConfig.addChangeListener("bbb", bbbListener);
        EmbeddedEtcdCluster.deleteProperty("test", "bbb");
        await().forever().until(() -> counter.get() == 4);
        EtcdConfigService.tearDownTestMode();

        EtcdConfigService.setUpTestMode();
        EtcdConfigChangeListener cccListener = event -> {
            assertEquals("ccc", event.getValue());
            assertEquals(WatchEvent.EventType.PUT, event.getEventType());
            counter.incrementAndGet();
        };
        testConfig.addChangeListener("ccc", cccListener);
        testConfig.addChangeListener("ccc", cccListener);
        EmbeddedEtcdCluster.addOrModifyProperty("test", "ccc", "ccc");
        await().forever().until(() -> counter.get() == 6);
        EtcdConfigService.tearDownTestMode();

        testConfig.removeChangeListener(aaaListener);
        testConfig.removeChangeListener(bbbListener);
        testConfig.removeChangeListener(cccListener);
    }
}
