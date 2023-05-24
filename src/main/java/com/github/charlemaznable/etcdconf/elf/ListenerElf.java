package com.github.charlemaznable.etcdconf.elf;

import com.github.charlemaznable.etcdconf.EtcdConfigChangeListener;
import io.etcd.jetcd.Watch;
import io.etcd.jetcd.watch.WatchEvent;
import io.etcd.jetcd.watch.WatchResponse;
import lombok.NoArgsConstructor;

import static com.github.charlemaznable.etcdconf.elf.ByteSequenceElf.fromByteSequence;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class ListenerElf {

    public static Watch.Listener listener(EtcdConfigChangeListener etcdConfigChangeListener) {
        return new Watch.Listener() {

            @Override
            public void onNext(WatchResponse response) {
                response.getEvents().forEach(event ->
                        etcdConfigChangeListener.onChange(changeEvent(event)));
            }

            @Override
            public void onError(Throwable throwable) {
                // emtpy onError
            }

            @Override
            public void onCompleted() {
                // empty onCompleted
            }
        };
    }

    private static EtcdConfigChangeListener.ChangeEvent changeEvent(WatchEvent event) {
        return new EtcdConfigChangeListener.ChangeEvent(
                fromByteSequence(event.getKeyValue().getValue()),
                event.getEventType()
        );
    }
}
