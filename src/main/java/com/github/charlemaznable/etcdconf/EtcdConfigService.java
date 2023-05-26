package com.github.charlemaznable.etcdconf;

import com.github.charlemaznable.etcdconf.client.EtcdClientWrapper;
import com.github.charlemaznable.etcdconf.impl.EtcdConfigImpl;
import io.etcd.jetcd.ByteSequence;

import java.util.List;
import java.util.Map;

import static com.github.charlemaznable.etcdconf.client.EtcdClientBuildService.clientBuilder;
import static com.github.charlemaznable.etcdconf.elf.ListElf.addItemToList;
import static com.google.common.collect.Maps.newConcurrentMap;

public final class EtcdConfigService {

    private static final EtcdConfigService instance = new EtcdConfigService();

    private volatile EtcdClientWrapper client;
    private final Map<EtcdConfigChangeListener, List<ByteSequence>> listeningKeys = newConcurrentMap();

    public static EtcdConfig getConfig(String namespace) {
        return new EtcdConfigImpl(namespace, instance);
    }

    public ByteSequence getValue(ByteSequence key) {
        return getClient().get(key).orElse(null);
    }

    public void addChangeListener(ByteSequence key, EtcdConfigChangeListener listener) {
        listeningKeys.compute(listener, (k, v) -> addItemToList(v, key));
        getClient().watch(key, listener);
    }

    public void removeChangeListener(EtcdConfigChangeListener listener) {
        listeningKeys.remove(listener);
        getClient().unwatch(listener);
    }

    private EtcdClientWrapper getClient() {
        if (client == null) {
            synchronized (this) {
                if (client == null) {
                    client = EtcdClientWrapper.build(clientBuilder());
                    listeningKeys.forEach((listener, keys) ->
                            keys.forEach(key -> client.watch(key, listener)));
                }
            }
        }
        return client;
    }

    // for mock only
    static void reset() {
        synchronized (instance) {
            instance.client = null;
            instance.getClient();
        }
    }
}
