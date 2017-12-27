package com.cootek.datainfra.event;

import com.cootek.datainfra.config.ConfigInfo;
import com.cootek.datainfra.listener.IConfigChangeListener;
import com.cootek.datainfra.validator.IConfigValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigChangeEvent extends AbstractListenerEvent {
    private static final Logger logger = LoggerFactory.getLogger(ConfigChangeEvent.class);

    public ConfigChangeEvent(ConfigInfo configInfo) {
        super(configInfo);
    }

    @Override
    public void acceptListener(IConfigChangeListener listener) {
        listener.onChange(this.configInfo.getConfigName(), this.configInfo.getConfig());
    }

    @Override
    public boolean acceptValidator(IConfigValidator validator) {
        try {
            validator.validate(configInfo.getConfig());
        } catch (Throwable t) {
            logger.error("invalid config", t);
            return false;
        }

        return true;
    }
}
