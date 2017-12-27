package com.cootek.datainfra.fetcher;

import com.cootek.datainfra.GuldanException;
import com.cootek.datainfra.GuldanUtils;
import com.cootek.datainfra.config.ConfigInfo;
import com.cootek.datainfra.config.EmptyConfig;
import com.cootek.datainfra.config.ForbiddenConfig;
import com.cootek.datainfra.config.NotFoundConfig;
import com.cootek.datainfra.config.ServerErrorConfig;
import com.cootek.datainfra.config.ValidConfig;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

class HttpFetcher {
    private static final Logger logger = LoggerFactory.getLogger(HttpFetcher.class);

    private static ConfigInfo getConfig(String guldanUrl, String guldanToken) {
        List<String> lines = new ArrayList<>(0);
        Header[] headers = new Header[0];
        int statusCode = 200;
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet getRequest = buildHttpGetRequest(guldanUrl, guldanToken);
            try (CloseableHttpResponse response = httpClient.execute(getRequest)) {
                statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    HttpEntity entity = response.getEntity();
                    headers = response.getHeaders("X-Guldan-Version");
                    lines = getLinesFromResponse(response);
                    EntityUtils.consume(entity);
                }
            }
        } catch (Throwable t) {
            logger.error("unable to retrieve config from: " + guldanUrl + "--" + guldanToken, t);
            return new EmptyConfig(guldanUrl, guldanToken);
        }

        if (statusCode == 403) {
            return new ForbiddenConfig(guldanUrl, guldanToken);
        }

        if (statusCode == 404) {
            return new NotFoundConfig(guldanUrl, guldanToken);
        }

        if (statusCode != 200) {
            return new ServerErrorConfig(guldanUrl, guldanToken);
        }

        if (headers.length == 0) {
            throw new GuldanException("invalid guldan puller response");
        }

        String configContent = GuldanUtils.joinStrings(lines, System.lineSeparator());
        return new ValidConfig(guldanUrl, configContent, headers[0].getValue(), guldanToken);
    }

    private static HttpGet buildHttpGetRequest(String url, String guldanToken) {
        HttpGet request = new HttpGet(url);
        request.setConfig(
                RequestConfig.copy(RequestConfig.DEFAULT)
                        .setConnectionRequestTimeout(3000)
                        .setConnectTimeout(3000)
                        .setSocketTimeout(3000)
                        .build()
        );
        if (!GuldanUtils.isBlankString(guldanToken)) {
            request.setHeader("X-Guldan-Token", guldanToken);
        }
        return request;
    }

    private static List<String> getLinesFromResponse(CloseableHttpResponse response) throws IOException {
        List<String> result = new LinkedList<>();
        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));
        String line = "";
        while ((line = rd.readLine()) != null) {
            if (line.isEmpty()) {
                continue;
            }
            result.add(line.trim());
        }
        return result;
    }

    public ConfigInfo fetch(String guldanUrl, String guldanToken) {
        return getConfig(guldanUrl, guldanToken);
    }
}
