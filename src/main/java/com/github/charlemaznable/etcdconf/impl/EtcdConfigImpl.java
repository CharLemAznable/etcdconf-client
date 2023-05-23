package com.github.charlemaznable.etcdconf.impl;

import com.github.charlemaznable.etcdconf.EtcdConfig;
import com.github.charlemaznable.etcdconf.elf.Functions;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import lombok.val;

import java.util.concurrent.ExecutionException;
import java.util.function.Function;

import static com.github.charlemaznable.etcdconf.elf.ByteSequenceElf.fromByteSequence;
import static com.github.charlemaznable.etcdconf.elf.ByteSequenceElf.toByteSequence;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

public final class EtcdConfigImpl implements EtcdConfig {

    private final ByteSequence namespace;
    private final KV kvClient;

    public EtcdConfigImpl(String namespace, Client client) {
        val ns = trimToEmpty(namespace);
        this.namespace = toByteSequence(isBlank(ns)
                ? "/" : ("/" + namespace + "/"));
        this.kvClient = client.getKVClient();
    }

    @Override
    public String getString(String key, String defaultValue) {
        return getValue(key, defaultValue, Functions.TO_STR_FUNCTION);
    }

    @Override
    public int getInt(String key, int defaultValue) {
        return getValue(key, defaultValue, Functions.TO_INT_FUNCTION);
    }

    @Override
    public long getLong(String key, long defaultValue) {
        return getValue(key, defaultValue, Functions.TO_LONG_FUNCTION);
    }

    @Override
    public short getShort(String key, short defaultValue) {
        return getValue(key, defaultValue, Functions.TO_SHORT_FUNCTION);
    }

    @Override
    public float getFloat(String key, float defaultValue) {
        return getValue(key, defaultValue, Functions.TO_FLOAT_FUNCTION);
    }

    @Override
    public double getDouble(String key, double defaultValue) {
        return getValue(key, defaultValue, Functions.TO_DOUBLE_FUNCTION);
    }

    @Override
    public byte getByte(String key, byte defaultValue) {
        return getValue(key, defaultValue, Functions.TO_BYTE_FUNCTION);
    }

    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        return getValue(key, defaultValue, Functions.TO_BOOLEAN_FUNCTION);
    }

    @Override
    public long getDuration(String key, long defaultValue) {
        return getValue(key, defaultValue, Functions.TO_DURATION_FUNCTION);
    }

    private String getValue(String key) {
        try {
            val getResponse = kvClient.get(
                    namespace.concat(toByteSequence(key))).get();
            if (getResponse.getCount() <= 0) return null;
            return fromByteSequence(getResponse.getKvs().get(0).getValue());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        } catch (ExecutionException e) {
            return null;
        }
    }

    private <T> T getValue(String key, T defaultValue, Function<String, T> parser) {
        val value = getValue(key);
        if (isBlank(value)) return defaultValue;
        try {
            return parser.apply(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
