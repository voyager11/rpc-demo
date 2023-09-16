package org.example.version2.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import org.example.common.MyDefaultFuture;
import org.example.common.Request;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class NettyRPCClient2 {

    private static NettyClientHandler2 clientHandler;

    private static final AtomicLong REQ_ID = new AtomicLong(0);

    private static volatile Integer a = 0;

    public static Object create(Class target){

        return Proxy.newProxyInstance(target.getClassLoader(), new Class[]{target},
                new InvocationHandler() {
            //java.lang.reflect.InvodationHandler.invoke
            @Override
            public Object invoke(Object proxy, Method method, Object[] args)
                    throws Throwable {

                String name = method.getName();

                //
                Long reqId = REQ_ID.incrementAndGet();
                Map<String,Object> map = new HashMap<>();
                map.put("reqId",reqId);
                map.put("interfaceName", target.getName());
                map.put("methodName",method.getName());
                map.put("args",args);
                map.put("argType",method.getParameterTypes());

                Request request = new Request();
                request.setId(reqId);
                request.setMap(map);

                MyDefaultFuture future =
                        MyDefaultFuture.newFuture(request,10000);

                clientHandler.sendReq(map);

                return future.get();
            }
        });
    }

    public static void initClient(){
        clientHandler = new NettyClientHandler2();
        NioEventLoopGroup group = new NioEventLoopGroup(16);
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(
                        new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel ch) throws Exception {
                                ChannelPipeline pipeline = ch.pipeline();
                                pipeline.addLast("encoder",new ObjectEncoder())
                                        .addLast("decoder", new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)))
                                        .addLast("handler",clientHandler);
                            }
                        }
                );

        try {
            bootstrap.connect("127.0.0.1", 9999).sync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
