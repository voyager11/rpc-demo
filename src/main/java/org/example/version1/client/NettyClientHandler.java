package org.example.version1.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Map;
import java.util.concurrent.Callable;

//@ChannelHandler.Sharable
public class NettyClientHandler extends ChannelInboundHandlerAdapter implements Callable {

    private ChannelHandlerContext context;
    private Object result;
    private Map<String,Object> classInfoMap;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        context = ctx;
    }

    @Override
    public synchronized void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        System.out.println(" channelRead 被调用  ");
//        String name = Thread.currentThread().getName();
//        System.out.println(name + " --notify");
        result = msg;

        notify();

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }


    @Override
    public synchronized Object call() throws Exception {
        String name = Thread.currentThread().getName();
//        System.out.println(name + " --wait");
        context.writeAndFlush(classInfoMap);
        wait();
        return  result; //服务方返回的结果

    }


    public void setClassInfoMap(Map<String, Object> classInfoMap) {
        this.classInfoMap = classInfoMap;
    }
}
