package com.cootek.datainfra.config;

import com.cootek.datainfra.GuldanException;
import com.cootek.datainfra.GuldanUtils;
import com.cootek.datainfra.event.AbstractListenerEvent;
import com.cootek.datainfra.event.ConfigNotFoundEvent;

public class NotFoundConfig implements ConfigInfo {
    private final String configName;
    private final String guldanToken;

    public NotFoundConfig(String configUrl, String guldanToken) {
        this.configName = GuldanUtils.guldanFullUrlToConfigName(configUrl);
        this.guldanToken = guldanToken;
    }

    @Override
    public String getConfigName() {
        return this.configName;
    }

    @Override
    public String getConfig() {
        throw new GuldanException("item not found: " + this.configName);
    }

    @Override
    public String getGuldanToken() {
        return this.guldanToken;
    }

    @Override
    public String getGuldanVersion() {
        throw new GuldanException("item not found: " + this.configName);
    }

    @Override
    public AbstractListenerEvent generateListenerEvent(ConfigInfo oldConfig) {
        return new ConfigNotFoundEvent(this);
    }

    @Override
    public ConfigInfo chooseConfigInfoWith(ConfigInfo oldConfig) {
        return this;
    }
}
