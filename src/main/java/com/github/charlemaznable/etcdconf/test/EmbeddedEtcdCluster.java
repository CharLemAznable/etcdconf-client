package com.github.charlemaznable.etcdconf.test;

import io.etcd.jetcd.Client;
import io.etcd.jetcd.launcher.Etcd;
import io.etcd.jetcd.launcher.EtcdCluster;
import lombok.SneakyThrows;
import lombok.val;

import static com.github.charlemaznable.etcdconf.elf.ByteSequenceElf.toByteSequence;

public final class EmbeddedEtcdCluster {

    private static final EtcdCluster cluster
            = new Etcd.Builder().withNodes(3).build();

    public static void setUp() {
        cluster.start();
    }

    public static void tearDown() {
        cluster.close();
    }

    @SneakyThrows
    public static void addOrModifyProperty(String namespace, String someKey, String someValue) {
        val client = buildClient();
        val kvClient = client.getKVClient();
        val key = toByteSequence("/" + namespace + "/" + someKey);
        val value = toByteSequence(someValue);
        kvClient.put(key, value).get();
    }

    @SneakyThrows
    public static void deleteProperty(String namespace, String someKey) {
        val client = buildClient();
        val kvClient = client.getKVClient();
        val key = toByteSequence("/" + namespace + "/" + someKey);
        kvClient.delete(key).get();
    }

    public static Client buildClient() {
        return Client.builder().endpoints(
                cluster.clientEndpoints()).build();
    }
}
