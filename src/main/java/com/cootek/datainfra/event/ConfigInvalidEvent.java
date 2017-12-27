package com.cootek.datainfra.event;

import com.cootek.datainfra.config.ConfigInfo;
import com.cootek.datainfra.listener.IConfigChangeListener;

public class ConfigInvalidEvent extends AbstractListenerEvent {
    public ConfigInvalidEvent(ConfigInfo configInfo) {
        super(configInfo);
    }

    @Override
    public void acceptListener(IConfigChangeListener listener) {

    }
}
