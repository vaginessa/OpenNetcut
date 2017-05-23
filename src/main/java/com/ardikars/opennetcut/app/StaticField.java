package com.ardikars.opennetcut.app;

import com.ardikars.jxnet.*;
import com.ardikars.opennetcut.OpenNetcut;

import java.awt.*;

public class StaticField {

    public static LoggerHandler<LoggerStatus, String> LOGGER;

    public static final Image ICON_IMAGE = Toolkit.getDefaultToolkit().getImage(OpenNetcut.class.getResource("/com/ardikars/opennetcut/images/16x16/icon.png"));

    public static String SOURCE;
    public static Pcap PCAP;
    public static int SNAPLEN = 1500;
    public static int PROMISC = 1;
    public static int TIMEOUT = 300;
    public static StringBuilder ERRBUF = new StringBuilder();

    public static BpfProgram BPF_PROGRAM = new BpfProgram();
    public static int OPTIMIZE = 1;

    public static DataLinkType DATALINK_TYPE;

    public static MacAddress GATEWAY_MAC_ADDRESS;
    public static MacAddress CURRENT_MAC_ADDRESS;

    public static Inet4Address GATEWAY_INET4ADDRESS = Inet4Address.ZERO;
    public static Inet4Address CURRENT_INET4ADDRESS = Inet4Address.ZERO;
    public static Inet4Address CURRENT_NETWORK_ADDRESS = Inet4Address.ZERO;
    public static Inet4Address CURRENT_NETMASK_ADDRESS = Inet4Address.ZERO;

    public static String RANDOM_STRING;

}
