package com.cootek.datainfra.listener;

public interface IConfigChangeListener {
    void onChange(String configName, String config);

    void onForbidden(String configName);

    void onNotFound(String configName);

    void onServerError(String configName);
}
