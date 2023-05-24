package com.github.charlemaznable.etcdconf;

public interface EtcdConfig {

    String getString(String key, String defaultValue);

    int getInt(String key, int defaultValue);

    long getLong(String key, long defaultValue);

    short getShort(String key, short defaultValue);

    float getFloat(String key, float defaultValue);

    double getDouble(String key, double defaultValue);

    byte getByte(String key, byte defaultValue);

    boolean getBoolean(String key, boolean defaultValue);

    long getDuration(String key, long defaultValue);

    void addChangeListener(String key, EtcdConfigChangeListener listener);

    void removeChangeListener(EtcdConfigChangeListener listener);
}
