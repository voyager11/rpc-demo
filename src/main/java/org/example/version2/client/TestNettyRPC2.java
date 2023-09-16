package org.example.version2.client;

import org.example.api.HelloNetty;


public class TestNettyRPC2 {

    public static void main(String[] args) {

        System.out.println("-----------1 TestNettyRPC main()-----------------");

        NettyRPCClient2.initClient();

        HelloNetty helloNetty = (HelloNetty) NettyRPCClient2.create(HelloNetty.class);

        String xxx = helloNetty.hello("1122444",3);
        int inttt = helloNetty.inttt(666);
        System.out.println(xxx);
        System.out.println(inttt);
        System.out.println("xx");



        // multiple threads invoke, working well
        for (int i = 0; i < 5; i++) {
            final  int j = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String s = Thread.currentThread().getName();
                    System.out.println(helloNetty.hello(s,j));
                }
            },"thread-" + i).start();
        }

    }
}
