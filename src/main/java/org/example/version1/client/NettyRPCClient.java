package org.example.version1.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NettyRPCClient {

    private static ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private static NettyClientHandler clientHandler;

    public static Object create(Class target){

        return Proxy.newProxyInstance(target.getClassLoader(), new Class[]{target},
                new InvocationHandler() {
            //java.lang.reflect.InvodationHandler.invoke
            @Override
            public Object invoke(Object proxy, Method method, Object[] args)
                    throws Throwable {

//                String name = method.getName();
//                System.out.println("---method name " +name+"----");
                Map<String,Object> map = new HashMap<>();
                map.put("interfaceName", target.getName());
                map.put("methodName",method.getName());
                map.put("args",args);
                map.put("argType",method.getParameterTypes());

                clientHandler.setClassInfoMap(map);

                return executor.submit(clientHandler).get();
            }
        });
    }

    public static void initClient(){
        clientHandler = new NettyClientHandler();

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
