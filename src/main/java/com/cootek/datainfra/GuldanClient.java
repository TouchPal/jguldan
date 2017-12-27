package com.cootek.datainfra;

import com.cootek.datainfra.fetcher.ConfigFetcher;
import com.cootek.datainfra.listener.IConfigChangeListener;
import com.cootek.datainfra.validator.IConfigValidator;

import java.util.List;

public class GuldanClient {
    private static final int NUMBER_OF_CONFIG_PARTS = 3;
    private static ConfigFetcher configFetcher;

    static {
        configFetcher = new ConfigFetcher();
        configFetcher.start();
    }

    public GuldanClient(String guldanUrl) {
        configFetcher.setGuldanUrl(guldanUrl);
    }

    public GuldanClient(String guldanUrl, int refreshInterval) {
        this(guldanUrl);
        configFetcher.setFreshInterval(refreshInterval);
    }

    public String getPublicConfig(String org, String project, String item) {
        return this.getConfig(org, project, item, "");
    }

    public String getConfig(String org, String project, String item, String guldanToken) {
        String configName = ConfigFetcher.constructConfigName(org, project, item);
        return this.getConfig(configName, guldanToken);
    }

    public String getPublicConfig(List<String> configParts) {
        return this.getConfig(configParts, "");
    }

    public String getConfig(List<String> configParts, String guldanToken) {
        if (configParts.size() != NUMBER_OF_CONFIG_PARTS) {
            throw new GuldanException("config list must have 3 parts");
        }

        String fullConfig = GuldanUtils.joinStrings(configParts, ConfigFetcher.CONFIG_SEPARATOR);
        return this.getConfig(fullConfig, guldanToken);
    }

    public String getPublicGreyConfig(String org, String project, String item) {
        return this.getGreyConfig(org, project, item, "");
    }

    public String getGreyConfig(String org, String project, String item, String guldanToken) {
        String configName = ConfigFetcher.constructConfigName(org, project, item);
        return this.getGreyConfig(configName, guldanToken);
    }

    public String getPublicGreyConfig(List<String> configParts) {
        return getGreyConfig(configParts, "");
    }

    public String getGreyConfig(List<String> configParts, String guldanToken) {
        if (configParts.size() != NUMBER_OF_CONFIG_PARTS) {
            throw new GuldanException("config list must have 3 parts");
        }

        String fullConfig = GuldanUtils.joinStrings(configParts, ConfigFetcher.CONFIG_SEPARATOR);
        return this.getGreyConfig(fullConfig, guldanToken);
    }

    public void subscribeChanges(String org, String project, String item, IConfigChangeListener listener) {
        String fullName = ConfigFetcher.constructConfigName(org, project, item);
        configFetcher.subscribeChanges(fullName, listener);
    }

    public void unsubscribeChanges(String org, String project, String item, IConfigChangeListener listener) {
        String fullName = ConfigFetcher.constructConfigName(org, project, item);
        configFetcher.unsubscribeChanges(fullName, listener);
    }

    public void addValidator(String org, String project, String item, IConfigValidator validator) {
        String fullName = ConfigFetcher.constructConfigName(org, project, item);
        configFetcher.addValidator(fullName, validator);
    }

    public void removeValidator(String org, String project, String item, IConfigValidator validator) {
        String fullName = ConfigFetcher.constructConfigName(org, project, item);
        configFetcher.removeValidator(fullName, validator);
    }

    private String getConfig(String config, String guldanToken) {
        if (guldanToken == null) {
            throw new GuldanException("please provide your guldan token");
        }
        return configFetcher.getConfig(config, guldanToken);
    }

    private String getGreyConfig(String config, String guldanToken) {
        if (guldanToken == null) {
            throw new GuldanException("please provide your guldan token");
        }
        return configFetcher.getGreyConfig(config, guldanToken);
    }
}
