package com.github.charlemaznable.etcdconf;

import com.github.charlemaznable.etcdconf.client.EtcdClientBuildService;
import com.github.charlemaznable.etcdconf.impl.EtcdConfigImpl;
import com.github.charlemaznable.etcdconf.test.EmbeddedEtcdCluster;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.Watch;
import lombok.val;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.github.charlemaznable.etcdconf.elf.ListElf.addItemToList;
import static com.github.charlemaznable.etcdconf.elf.ListenerElf.listener;
import static com.google.common.collect.Maps.newConcurrentMap;
import static java.util.Objects.nonNull;

public final class EtcdConfigService {

    private static final EtcdConfigService instance = new EtcdConfigService();

    private volatile Client client;
    private volatile boolean testMode = false;

    private final Map<EtcdConfigChangeListener, List<ByteSequence>> listeningKeys = newConcurrentMap();
    private final Map<EtcdConfigChangeListener, List<Watch.Watcher>> listeningWatchers = newConcurrentMap();

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

    public void addChangeListener(ByteSequence namespace, ByteSequence key,
                                  EtcdConfigChangeListener listener) {
        val nsk = namespace.concat(key);
        val watcher = getClient().getWatchClient().watch(nsk, listener(listener));
        listeningKeys.compute(listener, (k, v) -> addItemToList(v, nsk));
        listeningWatchers.compute(listener, (k, v) -> addItemToList(v, watcher));
    }

    public void removeChangeListener(EtcdConfigChangeListener listener) {
        listeningKeys.remove(listener);
        val watchers = listeningWatchers.remove(listener);
        if (nonNull(watchers)) watchers.forEach(Watch.Watcher::close);
    }

    private Client getClient() {
        if (client == null) {
            synchronized (this) {
                if (client == null) {
                    client = testMode ? EmbeddedEtcdCluster.buildClient() :
                            EtcdClientBuildService.clientBuilder().build();
                    listeningWatchers.clear();
                    listeningKeys.forEach((listener, keys) -> keys.forEach(key -> {
                        val watcher = client.getWatchClient().watch(key, listener(listener));
                        listeningWatchers.compute(listener, (k, v) -> addItemToList(v, watcher));
                    }));
                }
            }
        }
        return client;
    }
}
