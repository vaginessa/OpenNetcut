package com.ardikars.opennetcut.app;

import com.ardikars.jxnet.*;

import java.util.HashMap;
import java.util.Map;

public class StaticField {

    public static LoggerHandler<LoggerStatus, String> LOGGER;

    public static String SOURCE;
    public static Pcap PCAP;
    public static Pcap Pcap_IDS;
    public static Pcap PCAP_ICMP_TRAP;
    public static int SNAPLEN = 1500;
    public static int PROMISC = 1;
    public static int TIMEOUT = 300;
    public static StringBuilder ERRBUF = new StringBuilder();

    public static BpfProgram BPF_PROGRAM = new BpfProgram();
    public static BpfProgram BPF_PROGRAM_IDS = new BpfProgram();
    public static BpfProgram BPF_PROGRAM_ICMP_TRAP = new BpfProgram();
    public static int OPTIMIZE = 1;

    public static DataLinkType DATALINK_TYPE;

    public static MacAddress GATEWAY_MAC_ADDRESS;
    public static MacAddress CURRENT_MAC_ADDRESS;

    public static Inet4Address GATEWAY_INET4ADDRESS = new Inet4Address();
    public static Inet4Address CURRENT_INET4ADDRESS = new Inet4Address();
    public static Inet4Address CURRENT_NETWORK_ADDRESS = new Inet4Address();
    public static Inet4Address CURRENT_NETMASK_ADDRESS = new Inet4Address();

    public static String RANDOM_STRING;

    public static Map<String, String> ARP_CACHE = new HashMap<String, String>();

    public static Map<String, Long> EPOCH_TIME = new HashMap<String, Long>();

}
