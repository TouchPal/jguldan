package com.cootek.datainfra.config;


import com.cootek.datainfra.GuldanException;
import com.cootek.datainfra.event.AbstractListenerEvent;
import com.cootek.datainfra.event.ConfigInvalidEvent;

public class InvalidConfig implements ConfigInfo {
    private final String configName;
    private final String guldanToken;
    private final String config;

    public InvalidConfig(String configName, String config, String guldanToken) {
        this.configName = configName;
        this.config = config;
        this.guldanToken = guldanToken;
    }

    @Override
    public String getConfigName() {
        return this.configName;
    }

    @Override
    public String getConfig() {
        throw new GuldanException("invalid config format: " + this.config);
    }

    @Override
    public String getGuldanToken() {
        return this.guldanToken;
    }

    @Override
    public String getGuldanVersion() {
        throw new GuldanException("invalid config format: " + this.config);
    }

    @Override
    public AbstractListenerEvent generateListenerEvent(ConfigInfo oldConfig) {
        return new ConfigInvalidEvent(this);
    }

    @Override
    public ConfigInfo chooseConfigInfoWith(ConfigInfo oldConfig) {
        return oldConfig;
    }
}
