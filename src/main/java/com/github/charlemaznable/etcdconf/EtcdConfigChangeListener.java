package com.github.charlemaznable.etcdconf;

import io.etcd.jetcd.watch.WatchEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

public interface EtcdConfigChangeListener {

    void onChange(ChangeEvent event);

    @AllArgsConstructor
    @Getter
    final class ChangeEvent {

        private final String newValue;
        private final String prevValue;
        private final WatchEvent.EventType eventType;
    }
}
