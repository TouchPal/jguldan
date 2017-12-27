package com.cootek.datainfra;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Constant {
    public static final String PID;
    public static final String HOSTNAME;
    public static final String CLIENT_TYPE = "guldan_java_client";
    public static final String CLIENT_VERSION = "0.0.1";

    static {
        PID = getPid();
        HOSTNAME = getHostName();
    }

    private static String getPid() {
        String pid = ManagementFactory.getRuntimeMXBean().getName();
        if (pid != null && pid.indexOf("@") != -1) {
            pid = pid.substring(0, pid.indexOf("@"));
        }
        return pid;
    }

    private static String getHostName() {
        String hostname;
        try {
            hostname = (InetAddress.getLocalHost()).getHostName();
        } catch (UnknownHostException uhe) {
            String host = uhe.getMessage(); // host = "hostname: hostname"
            if (host != null) {
                int colon = host.indexOf(':');
                if (colon > 0) {
                    hostname = host.substring(0, colon);
                } else {
                    hostname = "UnknownHost";
                }
            } else {
                hostname = "UnknownHost";
            }
        }

        return hostname;
    }
}
