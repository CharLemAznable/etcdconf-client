package com.github.charlemaznable.etcdconf;

import com.github.charlemaznable.etcdconf.test.EmbeddedEtcdCluster;
import lombok.val;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EtcdConfigGetterTest {

    @Test
    public void testEtcdConfigGetter() {
        EtcdConfigService.setUpTestMode();

        EmbeddedEtcdCluster.addOrModifyProperty("test", "some-str", "etcd使用gRPC");
        EmbeddedEtcdCluster.addOrModifyProperty("test", "some-int", Integer.toString(Integer.MAX_VALUE));
        EmbeddedEtcdCluster.addOrModifyProperty("test", "some-long", Long.toString(Long.MAX_VALUE));
        EmbeddedEtcdCluster.addOrModifyProperty("test", "some-short", Short.toString(Short.MAX_VALUE));
        EmbeddedEtcdCluster.addOrModifyProperty("test", "some-float", "123.45");
        EmbeddedEtcdCluster.addOrModifyProperty("test", "some-double", "6789.0");
        EmbeddedEtcdCluster.addOrModifyProperty("test", "some-byte", "97");
        EmbeddedEtcdCluster.addOrModifyProperty("test", "some-bool", "true");
        EmbeddedEtcdCluster.addOrModifyProperty("test", "some-duration", "3S300");

        val testConfig = EtcdConfigService.getConfig("test");
        assertEquals("etcd使用gRPC", testConfig.getString("some-str", "abc"));
        assertEquals(Integer.MAX_VALUE, testConfig.getInt("some-int", 123));
        assertEquals(Long.MAX_VALUE, testConfig.getLong("some-long", 456L));
        assertEquals(Short.MAX_VALUE, testConfig.getShort("some-short", (short) 12));
        assertEquals(123.45f, testConfig.getFloat("some-float", 3.4f));
        assertEquals(6789.0, testConfig.getDouble("some-double", 5.6));
        assertEquals('a', testConfig.getByte("some-byte", (byte) 'b'));
        assertTrue(testConfig.getBoolean("some-bool", false));
        assertEquals(3300, testConfig.getDuration("some-duration", 1000));

        EmbeddedEtcdCluster.deleteProperty("test", "some-str");
        EmbeddedEtcdCluster.deleteProperty("test", "some-int");
        EmbeddedEtcdCluster.deleteProperty("test", "some-long");
        EmbeddedEtcdCluster.deleteProperty("test", "some-short");
        EmbeddedEtcdCluster.deleteProperty("test", "some-float");
        EmbeddedEtcdCluster.deleteProperty("test", "some-double");
        EmbeddedEtcdCluster.deleteProperty("test", "some-byte");
        EmbeddedEtcdCluster.deleteProperty("test", "some-bool");
        EmbeddedEtcdCluster.deleteProperty("test", "some-duration");

        EtcdConfigService.tearDownTestMode();
    }
}
