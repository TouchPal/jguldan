package com.cootek.datainfra.config;

import com.cootek.datainfra.event.AbstractListenerEvent;

public interface ConfigInfo {
    String getConfigName();

    String getConfig();

    String getGuldanToken();

    String getGuldanVersion();

    AbstractListenerEvent generateListenerEvent(ConfigInfo oldConfig);

    ConfigInfo chooseConfigInfoWith(ConfigInfo oldConfig);
}
