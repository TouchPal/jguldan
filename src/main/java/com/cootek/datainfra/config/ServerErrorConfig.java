package com.cootek.datainfra.config;

import com.cootek.datainfra.GuldanUtils;
import com.cootek.datainfra.event.AbstractListenerEvent;
import com.cootek.datainfra.event.ServerErrorEvent;

public class ServerErrorConfig implements ConfigInfo {
    private final String configName;
    private final String guldanToken;

    public ServerErrorConfig(String configUrl, String guldanToken) {
        this.configName = GuldanUtils.guldanFullUrlToConfigName(configUrl);
        this.guldanToken = guldanToken;
    }

    @Override
    public String getConfigName() {
        return this.configName;
    }

    @Override
    public String getConfig() {
        return null;
    }

    @Override
    public String getGuldanToken() {
        return this.guldanToken;
    }

    @Override
    public String getGuldanVersion() {
        return null;
    }

    @Override
    public AbstractListenerEvent generateListenerEvent(ConfigInfo oldConfig) {
        return new ServerErrorEvent(oldConfig);
    }

    @Override
    public ConfigInfo chooseConfigInfoWith(ConfigInfo oldConfig) {
        return oldConfig;
    }
}
