package com.cootek.datainfra.event;

import com.cootek.datainfra.config.ConfigInfo;
import com.cootek.datainfra.listener.IConfigChangeListener;
import com.cootek.datainfra.validator.IConfigValidator;

public abstract class AbstractListenerEvent {
    protected ConfigInfo configInfo;

    public AbstractListenerEvent(ConfigInfo configInfo) {
        this.configInfo = configInfo;
    }

    public ConfigInfo getConfigInfo() {
        return configInfo;
    }

    public abstract void acceptListener(IConfigChangeListener listener);

    public boolean acceptValidator(IConfigValidator validator) {
        return true;
    }
}
