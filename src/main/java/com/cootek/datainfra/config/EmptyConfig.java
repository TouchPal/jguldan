package com.cootek.datainfra.config;

import com.cootek.datainfra.GuldanUtils;
import com.cootek.datainfra.event.AbstractListenerEvent;
import com.cootek.datainfra.event.ConfigEmptyEvent;

public class EmptyConfig implements ConfigInfo {
    private final String guldanToken;
    private final String configName;

    public EmptyConfig(String guldanUrl, String guldanToken) {
        this.configName = GuldanUtils.guldanFullUrlToConfigName(guldanUrl);
        this.guldanToken = guldanToken;
    }

    @Override
    public String getConfigName() {
        return this.configName;
    }

    @Override
    public String getConfig() {
        return "";
    }

    @Override
    public String getGuldanToken() {
        return this.guldanToken;
    }

    @Override
    public String getGuldanVersion() {
        return "";
    }

    @Override
    public AbstractListenerEvent generateListenerEvent(ConfigInfo oldConfig) {
        return new ConfigEmptyEvent(this);
    }

    @Override
    public ConfigInfo chooseConfigInfoWith(ConfigInfo oldConfig) {
        return oldConfig;
    }
}
