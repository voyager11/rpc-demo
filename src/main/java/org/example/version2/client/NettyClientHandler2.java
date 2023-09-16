package org.example.version2.client;

import cn.hutool.json.JSONUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.example.common.MyDefaultFuture;
import org.example.common.Response;

import java.util.Map;

public class NettyClientHandler2 extends ChannelInboundHandlerAdapter {

    private ChannelHandlerContext context;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        context = ctx;
    }


    @Override
    public synchronized void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String s = (String) msg;
        Response response = JSONUtil.toBean(s,Response.class);
        MyDefaultFuture.received(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println(cause);
        ctx.close();
    }


    public void sendReq(Map<String,Object> map){

        context.writeAndFlush(map);
    }
}
