package com.cootek.datainfra.config;

import com.cootek.datainfra.GuldanUtils;
import com.cootek.datainfra.event.AbstractListenerEvent;
import com.cootek.datainfra.event.ConfigChangeEvent;
import com.cootek.datainfra.event.ConfigEmptyEvent;

public class ValidConfig implements ConfigInfo {
    private final String configName;
    private final String config;
    private final String guldanToken;
    private final String guldanVersion;

    public ValidConfig(String guldanUrl, String config, String version, String guldanToken) {
        this.configName = GuldanUtils.guldanFullUrlToConfigName(guldanUrl);
        this.config = config;
        this.guldanToken = guldanToken;
        this.guldanVersion = version;
    }

    @Override
    public String getConfigName() {
        return this.configName;
    }

    @Override
    public String getConfig() {
        return this.config;
    }

    @Override
    public String getGuldanToken() {
        return this.guldanToken;
    }

    @Override
    public String getGuldanVersion() {
        return this.guldanVersion;
    }

    @Override
    public AbstractListenerEvent generateListenerEvent(ConfigInfo oldConfig) {
        if (this == oldConfig) {
            // this means that it is the first time we've got this config, we should validate it.
            return new ConfigChangeEvent(this);
        }

        if (oldConfig instanceof ValidConfig) {
            if (this.guldanVersion.equals(oldConfig.getGuldanVersion())) {
                return new ConfigEmptyEvent(this);
            }
        }

        return new ConfigChangeEvent(this);
    }

    @Override
    public ConfigInfo chooseConfigInfoWith(ConfigInfo oldConfig) {
        return this;
    }
}
