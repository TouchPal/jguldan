package com.cootek.datainfra.listener;

public abstract class AbstractConfigChangeListener implements IConfigChangeListener {
    @Override
    public void onChange(String configName, String config) {
    }

    @Override
    public void onForbidden(String configName) {
    }

    @Override
    public void onNotFound(String configName) {
    }

    @Override
    public void onServerError(String configName) {
    }
}
