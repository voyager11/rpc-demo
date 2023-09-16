package org.example.version1.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class NettyRPCServer {

    private int port;

    public NettyRPCServer(int port){
        this.port = port;
    }

    public void start(){

//        System.out.println("-------8 NettyRPCServer start()----");

        EventLoopGroup bossGroup = new NioEventLoopGroup(16);
        EventLoopGroup workerGroup = new NioEventLoopGroup(16);

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG,128)
                    .childOption(ChannelOption.SO_KEEPALIVE,true)
                    .localAddress(port)
                    .childHandler(
                            new ChannelInitializer<SocketChannel>() {
                                @Override
                                //使用netty自带的编码 解密器
                                //todo 实际开发中也可以使用json或者xml
                                protected void initChannel(SocketChannel ch) throws Exception {
                                    ChannelPipeline pipeline = ch.pipeline();
                                    pipeline.addLast("encoder",new ObjectEncoder())
                                            .addLast("decoder", new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)))
                                            .addLast(new InvokeHandler());
                                }
                            }
                    );

            ChannelFuture future = serverBootstrap.bind(port).sync();
            System.out.println("....server is ready....");

        }catch (Exception e){
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {

//        System.out.println("------7 NettyRPCServer main() -----");

        new NettyRPCServer(9999).start();
    }
}
