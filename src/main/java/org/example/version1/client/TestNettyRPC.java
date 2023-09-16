package org.example.version1.client;


import org.example.api.HelloNetty;

public class TestNettyRPC {

    public static void main(String[] args) {

        NettyRPCClient.initClient();

        System.out.println("-----------1 TestNettyRPC main()-----------------");

        HelloNetty helloNetty = (HelloNetty) NettyRPCClient.create(HelloNetty.class);

        String xxx = helloNetty.hello("qqqqq",3);
        int inttt = helloNetty.inttt(666);

        System.out.println(xxx);
        System.out.println(inttt);


        //multiple threads invoke, will be messed up
        // cause netty is a asynchronous framework,
        // so we can't make sure each thread will get the right response
        // we solve it in version2
        for (int i = 0; i < 5; i++) {
            final int j = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String s = Thread.currentThread().getName();

                    System.out.println(helloNetty.hello(s,j));
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            },"thread-" + i).start();
        }

    }
}
