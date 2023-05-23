package com.github.charlemaznable.etcdconf.client;

import io.etcd.jetcd.Client;

public interface EtcdClientBuilder {

    Client build();
}
