package com.cootek.datainfra;

import com.cootek.datainfra.fetcher.ConfigFetcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class GuldanUtils {
    private static final Logger logger = LoggerFactory.getLogger(GuldanUtils.class);

    public static String joinStrings(List<String> configList, String separator) {
        if (configList.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(configList.get(0));
        for (int i = 1; i < configList.size(); ++i) {
            sb.append(separator);
            sb.append(configList.get(i));
        }

        return sb.toString();
    }

    public static void sleep(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (Throwable t) {
            logger.error("error when sleep", t);
        }
    }

    public static String constructFullUrl(String guldanUrl, String configFullName, String args) {
        return guldanUrl + "/api/puller/" + configFullName + "?" + args;
    }

    /**
     * <p>Checks if a String is whitespace, empty ("") or null.</p>
     * <p>
     * <pre>
     * StringUtils.isBlankString(null)      = true
     * StringUtils.isBlankString("")        = true
     * StringUtils.isBlankString(" ")       = true
     * StringUtils.isBlankString("bob")     = false
     * StringUtils.isBlankString("  bob  ") = false
     * </pre>
     *
     * @param str the String to check, may be null
     * @return <code>true</code> if the String is null, empty or whitespace
     * @since 2.0
     */
    public static boolean isBlankString(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ((Character.isWhitespace(str.charAt(i)) == false)) {
                return false;
            }
        }
        return true;
    }

    public static String guldanFullUrlToConfigName(String guldanUrl) {
        URL url;
        try {
            url = new URL(guldanUrl);
        } catch (MalformedURLException e) {
            throw new GuldanException("invalid url string", e);
        }

        String[] urlParts = url.getPath().split("/");
        return urlParts[3] + ConfigFetcher.CONFIG_SEPARATOR + urlParts[4] + ConfigFetcher.CONFIG_SEPARATOR + urlParts[5];
    }
}
