package com.github.charlemaznable.etcdconf.client;

import lombok.val;

import java.util.ServiceLoader;

public final class EtcdClientBuildService {

    private static final EtcdClientBuilder clientBuilder;

    static {
        clientBuilder = findClientBuilder();
    }

    public static EtcdClientBuilder clientBuilder() {
        return clientBuilder;
    }

    private static EtcdClientBuilder findClientBuilder() {
        val clientBuilders = ServiceLoader.load(EtcdClientBuilder.class).iterator();
        if (!clientBuilders.hasNext()) return new DefaultEtcdClientBuilder();

        val result = clientBuilders.next();
        if (clientBuilders.hasNext())
            throw new IllegalStateException("Multiple EtcdClientBuilder Defined");
        return result;
    }
}
