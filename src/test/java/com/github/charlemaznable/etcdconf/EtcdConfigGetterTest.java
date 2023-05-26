package com.github.charlemaznable.etcdconf;

import lombok.val;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EtcdConfigGetterTest {

    @Test
    public void testEtcdConfigGetter() {
        val testConfig = EtcdConfigService.getConfig("test");
        assertEquals("abc", testConfig.getString("some-str", "abc"));
        assertEquals(123, testConfig.getInt("some-int", 123));
        assertEquals(456L, testConfig.getLong("some-long", 456L));
        assertEquals((short) 12, testConfig.getShort("some-short", (short) 12));
        assertEquals(3.4f, testConfig.getFloat("some-float", 3.4f));
        assertEquals(5.6, testConfig.getDouble("some-double", 5.6));
        assertEquals((byte) 'b', testConfig.getByte("some-byte", (byte) 'b'));
        assertTrue(testConfig.getBoolean("some-bool", true));
        assertEquals(1000, testConfig.getDuration("some-duration", 1000));

        MockEtcdServer.setUpMockServer();

        MockEtcdServer.addOrModifyProperty("test", "some-str", "etcd使用gRPC");
        MockEtcdServer.addOrModifyProperty("test", "some-int", Integer.toString(Integer.MAX_VALUE));
        MockEtcdServer.addOrModifyProperty("test", "some-long", Long.toString(Long.MAX_VALUE));
        MockEtcdServer.addOrModifyProperty("test", "some-short", Short.toString(Short.MAX_VALUE));
        MockEtcdServer.addOrModifyProperty("test", "some-float", "123.45");
        MockEtcdServer.addOrModifyProperty("test", "some-double", "6789.0");
        MockEtcdServer.addOrModifyProperty("test", "some-byte", "97");
        MockEtcdServer.addOrModifyProperty("test", "some-bool", "true");
        MockEtcdServer.addOrModifyProperty("test", "some-duration", "3S300");

        assertEquals("etcd使用gRPC", testConfig.getString("some-str", "abc"));
        assertEquals("abc", testConfig.getString("some-str2", "abc"));
        assertEquals(Integer.MAX_VALUE, testConfig.getInt("some-int", 123));
        assertEquals(123, testConfig.getInt("some-str", 123));
        assertEquals(Long.MAX_VALUE, testConfig.getLong("some-long", 456L));
        assertEquals(456L, testConfig.getLong("some-str", 456L));
        assertEquals(Short.MAX_VALUE, testConfig.getShort("some-short", (short) 12));
        assertEquals((short) 12, testConfig.getShort("some-str", (short) 12));
        assertEquals(123.45f, testConfig.getFloat("some-float", 3.4f));
        assertEquals(6789.0, testConfig.getDouble("some-double", 5.6));
        assertEquals('a', testConfig.getByte("some-byte", (byte) 'b'));
        assertTrue(testConfig.getBoolean("some-bool", false));
        assertEquals(3300, testConfig.getDuration("some-duration", 1000));

        MockEtcdServer.deleteProperty("test", "some-str");
        MockEtcdServer.deleteProperty("test", "some-int");
        MockEtcdServer.deleteProperty("test", "some-long");
        MockEtcdServer.deleteProperty("test", "some-short");
        MockEtcdServer.deleteProperty("test", "some-float");
        MockEtcdServer.deleteProperty("test", "some-double");
        MockEtcdServer.deleteProperty("test", "some-byte");
        MockEtcdServer.deleteProperty("test", "some-bool");
        MockEtcdServer.deleteProperty("test", "some-duration");

        MockEtcdServer.tearDownMockServer();
    }
}
