package com.github.charlemaznable.etcdconf.client;

import com.google.common.base.Splitter;
import com.google.common.io.Resources;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import lombok.val;

import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.Properties;

import static java.lang.Thread.currentThread;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public final class DefaultEtcdClientBuilder implements EtcdClientBuilder {

    @Override
    public Client build() {
        val properties = loadEtcdConfProperties();
        val clientBuilder = Client.builder();

        val target = properties.getProperty("target");
        val endpoints = properties.getProperty("endpoints");
        if (isNotBlank(target)) {
            clientBuilder.target(target);
        } else if (isNotBlank(endpoints)) {
            clientBuilder.endpoints(Splitter.on(",").omitEmptyStrings()
                    .trimResults().splitToStream(endpoints).toArray(String[]::new));
        }

        val user = properties.getProperty("user");
        if (isNotBlank(user)) {
            clientBuilder.user(ByteSequence.from(user.getBytes(UTF_8)));
        }

        val password = properties.getProperty("password");
        if (isNotBlank(password)) {
            clientBuilder.password(ByteSequence.from(password.getBytes(UTF_8)));
        }

        return clientBuilder.build();
    }

    private Properties loadEtcdConfProperties() {
        val properties = new Properties();

        val confURL = currentThread().getContextClassLoader()
                .getResource("etcdconf.properties");
        if (isNull(confURL)) return properties;

        val confString = urlAsString(confURL);
        if (isNull(confString)) return properties;

        try {
            properties.load(new StringReader(confString));
        } catch (IOException e) {
            // ignore
        }
        return properties;
    }

    private static String urlAsString(URL url) {
        try {
            return Resources.toString(url, UTF_8);
        } catch (IOException e) {
            return null;
        }
    }
}
