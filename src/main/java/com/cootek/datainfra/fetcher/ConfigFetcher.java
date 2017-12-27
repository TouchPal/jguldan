package com.cootek.datainfra.fetcher;

import com.cootek.datainfra.Constant;
import com.cootek.datainfra.GuldanUtils;
import com.cootek.datainfra.config.ConfigInfo;
import com.cootek.datainfra.config.InvalidConfig;
import com.cootek.datainfra.event.AbstractListenerEvent;
import com.cootek.datainfra.listener.IConfigChangeListener;
import com.cootek.datainfra.validator.IConfigValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.LinkedBlockingDeque;

public class ConfigFetcher {
    public static final String CONFIG_SEPARATOR = "/";
    private static final String MAP_KEY_JOINER = "@";
    private static final Logger logger = LoggerFactory.getLogger(ConfigFetcher.class);
    private static final String NORMAL_FETCHER_ARGS = constructNormalFetcherArgs();
    private static final String GREY_FETCHER_ARGS = constructGreyFetcherArgs();
    private static final HttpFetcher HTTP_FETCHER = new HttpFetcher();
    private static Map<String, ConfigInfo> NORMAL_CONFIG_MAP = new ConcurrentHashMap<>();
    private static Map<String, ConfigInfo> GREY_CONFIG_MAP = new ConcurrentHashMap<>();
    private final Map<String, Set<IConfigChangeListener>> configListenersMap = new ConcurrentHashMap<>();
    private final Map<String, Set<IConfigValidator>> configValidatorsMap = new ConcurrentHashMap<>();
    private Queue<AbstractListenerEvent> eventQueue = new LinkedBlockingDeque<>();
    private volatile int freshInterval = 5;
    private volatile String guldanUrl = "";

    public static String constructConfigName(String org, String project, String item) {
        return org + ConfigFetcher.CONFIG_SEPARATOR + project + ConfigFetcher.CONFIG_SEPARATOR + item;
    }

    private static String constructNormalFetcherArgs() {
        return "grey=false&ctype=" + Constant.CLIENT_TYPE + "&cver=" + Constant.CLIENT_VERSION + "&cid=" + Constant.PID;
    }

    private static String constructGreyFetcherArgs() {
        return "grey=true&ctype=" + Constant.CLIENT_TYPE + "&cver=" + Constant.CLIENT_VERSION + "&cid=" + Constant.PID;
    }

    public void start() {
        while (true) {
            boolean startSucceeded = true;
            try {
                new FetchWorker("guldan-fetcher-thread").start();
                new EventHandler("guldan-config-event-handler").start();
                startSucceeded = true;
            } catch (Throwable t) {
                logger.error("error when start fetcher thread", t);
                startSucceeded = false;
                GuldanUtils.sleep(1);
            }

            if (startSucceeded) {
                break;
            }
        }
    }

    public void setFreshInterval(int freshInterval) {
        this.freshInterval = freshInterval;
    }

    public void setGuldanUrl(String url) {
        this.guldanUrl = url;
    }

    public String getConfig(String configName, String guldanToken) {
        return this.getConfigInternal(configName, guldanToken, NORMAL_CONFIG_MAP, NORMAL_FETCHER_ARGS);
    }

    public String getGreyConfig(String configName, String guldanToken) {
        return this.getConfigInternal(configName, guldanToken, GREY_CONFIG_MAP, GREY_FETCHER_ARGS);
    }

    private String getCurrentVersion(ConfigInfo configInfo) {
        if (configInfo == null) {
            return "unknown";
        }

        try {
            return configInfo.getGuldanVersion();
        } catch (Throwable t) {
            return "unknown";
        }
    }

    private String buildConfigMapKey(String configName, String guldanToken) {
        return configName + ConfigFetcher.MAP_KEY_JOINER + guldanToken;
    }

    private String getConfigInternal(String configName, String guldanToken, Map<String, ConfigInfo> configMap, String fetcherArgs) {
        String configMapKey = buildConfigMapKey(configName, guldanToken);
        ConfigInfo oldConfigInfo = configMap.get(configMapKey);
        ConfigInfo newConfigInfo;
        if (oldConfigInfo == null) {
            newConfigInfo = getConfigInfo(configName, guldanToken, fetcherArgs, getCurrentVersion(null));
            configMap.put(configMapKey, newConfigInfo);
        } else {
            if (!guldanToken.equals(oldConfigInfo.getGuldanToken())) {
                newConfigInfo = getConfigInfo(configName, guldanToken, fetcherArgs, getCurrentVersion(oldConfigInfo));
                newConfigInfo = newConfigInfo.chooseConfigInfoWith(oldConfigInfo);
                configMap.put(configMapKey, newConfigInfo);
            } else {
                newConfigInfo = oldConfigInfo;
            }
        }

        return newConfigInfo.getConfig();
    }

    private ConfigInfo getConfigInfo(String configName, String guldanToken, String fetcherArgs, String currentVersion) {
        ConfigInfo configInfo;
        String urlArgs = fetcherArgs + "&lver=" + currentVersion;
        configInfo = this.fetchConfig(this.guldanUrl, configName, urlArgs, guldanToken);
        AbstractListenerEvent event = configInfo.generateListenerEvent(configInfo);
        configInfo = validateConfigInfo(event, configInfo);
        return configInfo;
    }

    public void subscribeChanges(String configName, IConfigChangeListener listener) {
        synchronized (configListenersMap) {
            Set<IConfigChangeListener> listeners = configListenersMap.get(configName);
            if (listeners == null) {
                listeners = new CopyOnWriteArraySet<>();
                configListenersMap.put(configName, listeners);
            }
            listeners.add(listener);
        }
    }

    public void unsubscribeChanges(String configName, IConfigChangeListener listener) {
        synchronized (configListenersMap) {
            final Set<IConfigChangeListener> listeners = configListenersMap.get(configName);
            if (listeners != null) {
                listeners.remove(listener);
            }
        }
    }

    private ConfigInfo fetchConfig(String guldanUrl, String configName, String args, String guldanToken) {
        String fullUrl = GuldanUtils.constructFullUrl(guldanUrl, configName, args);
        return HTTP_FETCHER.fetch(fullUrl, guldanToken);
    }

    public void addValidator(String configName, IConfigValidator validator) {
        synchronized (configValidatorsMap) {
            Set<IConfigValidator> validators = configValidatorsMap.get(configName);
            if (validators == null) {
                validators = new CopyOnWriteArraySet<>();
                configValidatorsMap.put(configName, validators);
            }
            validators.add(validator);
        }
    }

    public void removeValidator(String configName, IConfigValidator validator) {
        synchronized (configValidatorsMap) {
            final Set<IConfigValidator> validators = configValidatorsMap.get(configName);
            if (validators != null) {
                validators.remove(validator);
            }
        }
    }

    private ConfigInfo validateConfigInfo(AbstractListenerEvent event, ConfigInfo configInfo) {
        String configName = configInfo.getConfigName();
        synchronized (this.configValidatorsMap) {
            Set<IConfigValidator> validators = this.configValidatorsMap.get(configName);
            if (validators != null) {
                for (IConfigValidator validator : validators) {
                    if (!event.acceptValidator(validator)) {
                        return new InvalidConfig(configName, configInfo.getConfig(), configInfo.getGuldanToken());
                    }
                }
            }
        }

        return configInfo;
    }

    class FetchWorker extends Thread {
        FetchWorker(String name) {
            super(name);
            this.setDaemon(true);
        }

        @Override
        public void run() {
            while (true) {
                try {
                    NORMAL_CONFIG_MAP = fresh(NORMAL_CONFIG_MAP, NORMAL_FETCHER_ARGS);
                    GREY_CONFIG_MAP = fresh(GREY_CONFIG_MAP, GREY_FETCHER_ARGS);
                } catch (Throwable t) {
                    logger.error("exc when background fetch", t);
                } finally {
                    GuldanUtils.sleep(freshInterval);
                }
            }
        }

        private Map<String, ConfigInfo> fresh(Map<String, ConfigInfo> configMap, String args) {
            Map<String, ConfigInfo> newMap = new ConcurrentHashMap<>();
            for (Map.Entry<String, ConfigInfo> entry : configMap.entrySet()) {
                String[] configNameAndGuldanToken = entry.getKey().split(ConfigFetcher.MAP_KEY_JOINER);
                String configName = configNameAndGuldanToken[0];
                String guldanToken = configNameAndGuldanToken[1];
                String url = GuldanUtils.constructFullUrl(guldanUrl, configName, args);
                ConfigInfo newConfigInfo = HTTP_FETCHER.fetch(url, guldanToken);
                ConfigInfo oldConfigInfo = entry.getValue();
                AbstractListenerEvent event = newConfigInfo.generateListenerEvent(oldConfigInfo);
                newConfigInfo = ConfigFetcher.this.validateConfigInfo(event, newConfigInfo);
                ConfigInfo finalConfigInfo = newConfigInfo.chooseConfigInfoWith(oldConfigInfo);
                newMap.put(entry.getKey(), finalConfigInfo);
                ConfigFetcher.this.eventQueue.offer(event);
            }
            return newMap;
        }
    }

    class EventHandler extends Thread {
        EventHandler(String name) {
            super(name);
            this.setDaemon(true);
        }

        @Override
        public void run() {
            while (true) {
                try {
                    AbstractListenerEvent event = ConfigFetcher.this.eventQueue.peek();
                    if (event != null) {
                        handleEvent(event);
                        ConfigFetcher.this.eventQueue.poll();
                    } else {
                        GuldanUtils.sleep(1);
                    }
                } catch (Throwable t) {
                    logger.error("error when handle event", t);
                }
            }
        }

        private void handleEvent(AbstractListenerEvent event) {
            ConfigInfo configInfo = event.getConfigInfo();
            String configName = configInfo.getConfigName();
            synchronized (ConfigFetcher.this.configListenersMap) {
                Set<IConfigChangeListener> listeners = ConfigFetcher.this.configListenersMap.get(configName);
                if (listeners != null) {
                    for (IConfigChangeListener listener : listeners) {
                        event.acceptListener(listener);
                    }
                }
            }
        }
    }
}
