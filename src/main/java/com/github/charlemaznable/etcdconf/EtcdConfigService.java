package com.github.charlemaznable.etcdconf;

import com.github.charlemaznable.etcdconf.client.EtcdClientBuildService;
import com.github.charlemaznable.etcdconf.impl.EtcdConfigImpl;
import com.github.charlemaznable.etcdconf.test.EmbeddedEtcdCluster;
import io.etcd.jetcd.Client;

public final class EtcdConfigService {

    private static final EtcdConfigService instance = new EtcdConfigService();

    private volatile Client client;
    private volatile boolean testMode = false;

    public static EtcdConfig getConfig(String namespace) {
        return new EtcdConfigImpl(namespace, instance.getClient());
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
