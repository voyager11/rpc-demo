package org.example.version1.server;

import org.example.api.HelloNetty;

public class HelloNettyImpl implements HelloNetty {
    @Override
    public String hello(String str, Integer i) {

//        System.out.println("--------- 10 HelloNettyImpl hello -----");

        int j = i + 6;
        return str + ", "  + j;
    }

    @Override
    public int inttt(int a) {
        return a;
    }
}
