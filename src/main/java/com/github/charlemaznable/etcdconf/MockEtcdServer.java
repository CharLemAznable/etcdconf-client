package com.github.charlemaznable.etcdconf;

import com.github.charlemaznable.etcdconf.client.EtcdClientBuilder;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.launcher.Etcd;
import io.etcd.jetcd.launcher.EtcdCluster;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;

import static com.github.charlemaznable.etcdconf.elf.ByteSequenceElf.toByteSequence;

@AllArgsConstructor
public final class MockEtcdServer implements EtcdClientBuilder {

    private static final EtcdCluster cluster
            = new Etcd.Builder().withNodes(3).build();
    private static volatile boolean testMode = false;

    private final EtcdClientBuilder delegate;

    public static void setUpMockServer() {
        cluster.start();
        testMode = true;
        EtcdConfigService.reset();
    }

    public static void tearDownMockServer() {
        testMode = false;
        EtcdConfigService.reset();
        cluster.close();
    }

    @SneakyThrows
    public static void addOrModifyProperty(String namespace, String someKey, String someValue) {
        if (!testMode) return;
        val client = buildClient();
        val kvClient = client.getKVClient();
        val key = toByteSequence("/" + namespace + "/" + someKey);
        val value = toByteSequence(someValue);
        kvClient.put(key, value).get();
    }

    @SneakyThrows
    public static void deleteProperty(String namespace, String someKey) {
        if (!testMode) return;
        val client = buildClient();
        val kvClient = client.getKVClient();
        val key = toByteSequence("/" + namespace + "/" + someKey);
        kvClient.delete(key).get();
    }

    private static Client buildClient() {
        return Client.builder().endpoints(
                cluster.clientEndpoints()).build();
    }

    @Override
    public Client build() {
        return testMode ? buildClient() : delegate.build();
    }
}
