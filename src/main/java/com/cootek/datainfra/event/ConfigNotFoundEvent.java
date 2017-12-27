package com.cootek.datainfra.event;

import com.cootek.datainfra.config.ConfigInfo;
import com.cootek.datainfra.listener.IConfigChangeListener;

public class ConfigNotFoundEvent extends AbstractListenerEvent {
    public ConfigNotFoundEvent(ConfigInfo configInfo) {
        super(configInfo);
    }

    @Override
    public void acceptListener(IConfigChangeListener listener) {
        listener.onNotFound(this.configInfo.getConfigName());
    }
}
