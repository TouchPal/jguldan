package com.cootek.datainfra.event;

import com.cootek.datainfra.config.ConfigInfo;
import com.cootek.datainfra.listener.IConfigChangeListener;

public class ServerErrorEvent extends AbstractListenerEvent {
    public ServerErrorEvent(ConfigInfo configInfo) {
        super(configInfo);
    }

    @Override
    public void acceptListener(IConfigChangeListener listener) {
        listener.onServerError(this.configInfo.getConfigName());
    }
}
