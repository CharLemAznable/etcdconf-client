package com.github.charlemaznable.etcdconf;

import com.github.charlemaznable.etcdconf.client.EtcdClientBuildService;
import com.github.charlemaznable.etcdconf.impl.EtcdConfigImpl;
import com.github.charlemaznable.etcdconf.test.EmbeddedEtcdCluster;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import lombok.val;

import java.util.concurrent.ExecutionException;

public final class EtcdConfigService {

    private static final EtcdConfigService instance = new EtcdConfigService();

    private volatile Client client;
    private volatile boolean testMode = false;

    public static EtcdConfig getConfig(String namespace) {
        return new EtcdConfigImpl(namespace, instance);
    }

    public static void setUpTestMode() {
        synchronized (instance) {
            instance.testMode = true;
            instance.client = null;
            EmbeddedEtcdCluster.setUp();
        }
    }

    public static void tearDownTestMode() {
        synchronized (instance) {
            instance.testMode = false;
            instance.client = null;
            EmbeddedEtcdCluster.tearDown();
        }
    }

    public ByteSequence getValue(ByteSequence namespace, ByteSequence key) {
        try {
            val getResponse = getClient().getKVClient()
                    .get(namespace.concat(key)).get();
            if (getResponse.getCount() <= 0) return null;
            return getResponse.getKvs().get(0).getValue();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        } catch (ExecutionException e) {
            return null;
        }
    }

    private Client getClient() {
        if (client == null) {
            synchronized (this) {
                if (client == null) {
                    client = testMode ? EmbeddedEtcdCluster.buildClient() :
                            EtcdClientBuildService.clientBuilder().build();
                }
            }
        }
        return client;
    }
}
