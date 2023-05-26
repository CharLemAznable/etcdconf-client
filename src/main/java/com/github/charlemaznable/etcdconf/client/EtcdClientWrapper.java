package com.github.charlemaznable.etcdconf.client;

import com.github.charlemaznable.etcdconf.EtcdConfigChangeListener;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.Watch;
import lombok.AllArgsConstructor;
import lombok.val;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static com.github.charlemaznable.etcdconf.elf.ListElf.addItemToList;
import static com.github.charlemaznable.etcdconf.elf.ListenerElf.listener;
import static com.google.common.collect.Maps.newConcurrentMap;
import static java.util.Objects.nonNull;
import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor(access = PRIVATE)
public final class EtcdClientWrapper {

    private final Client client;
    private final Map<EtcdConfigChangeListener, List<Watch.Watcher>> listeningWatchers = newConcurrentMap();

    public static EtcdClientWrapper build(EtcdClientBuilder clientBuilder) {
        try {
            return new EtcdClientWrapper(clientBuilder.build());
        } catch (Exception e) {
            return new EtcdClientWrapper(null);
        }
    }

    public Optional<ByteSequence> get(ByteSequence key) {
        return Optional.ofNullable(this.client).flatMap(client -> {
            try {
                val getResponse = client.getKVClient().get(key).get();
                if (getResponse.getCount() <= 0) return Optional.empty();
                return Optional.ofNullable(getResponse.getKvs().get(0).getValue());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return Optional.empty();
            } catch (ExecutionException e) {
                return Optional.empty();
            }
        });
    }

    @SuppressWarnings("UnusedReturnValue")
    public Optional<Watch.Watcher> watch(ByteSequence key, EtcdConfigChangeListener listener) {
        return Optional.ofNullable(this.client).flatMap(client -> {
            val watcher = client.getWatchClient().watch(key, listener(listener));
            listeningWatchers.compute(listener, (k, v) -> addItemToList(v, watcher));
            return Optional.of(watcher);
        });
    }

    public void unwatch(EtcdConfigChangeListener listener) {
        val watchers = listeningWatchers.remove(listener);
        if (nonNull(watchers)) watchers.forEach(Watch.Watcher::close);
    }
}
