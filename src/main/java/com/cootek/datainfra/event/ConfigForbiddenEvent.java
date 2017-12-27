package com.cootek.datainfra.event;

import com.cootek.datainfra.config.ConfigInfo;
import com.cootek.datainfra.listener.IConfigChangeListener;

public class ConfigForbiddenEvent extends AbstractListenerEvent {
    public ConfigForbiddenEvent(ConfigInfo configInfo) {
        super(configInfo);
    }

    @Override
    public void acceptListener(IConfigChangeListener listener) {
        listener.onForbidden(this.configInfo.getConfigName());
    }
}
