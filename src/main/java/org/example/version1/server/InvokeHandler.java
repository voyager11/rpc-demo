package org.example.version1.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.example.common.ClassInfo;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

public class InvokeHandler extends ChannelInboundHandlerAdapter {

    private String getImplByMap(Map<String, Object> map) throws ClassNotFoundException {
        //inference, implement package
        String interfacePath = "org.example.api";
        String calssName = (String) map.get("interfaceName");
        int lastDot = calssName.lastIndexOf(".");
        String interfaceName = calssName.substring(lastDot);
        Class superClass = Class.forName(interfacePath+interfaceName);

//        Reflections reflections = new Reflections(interfacePath);
        Reflections reflections = new Reflections("org.example.version1.server");

        //get all implements of a interface
        Set<Class> implClassSet = reflections.getSubTypesOf(superClass);

        if (implClassSet.size()==0){
            System.out.println("--can't found any implement--");
            return null;
        }else if (implClassSet.size() > 1){
            System.out.println("--found more than one implements--");
            return null;
        }else {
            //把集合转换为数组
            Class[] classes = implClassSet.toArray(new Class[0]);
            System.out.println("--implementation："+classes[0].getName());
            return classes[0].getName();
        }
    }



    //读取客户端发来的数据并通过反射调用实现类的方法
    //本地调用，并返回给 消费方
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        System.out.println("-----4 InvokeHandler channelRead----");


        Map<String,Object> map = (Map<String, Object>) msg;
        Object clazz2 = Class.forName(getImplByMap(map)).newInstance();
        Method method2 = clazz2.getClass().getMethod((String) map.get("methodName"),
                (Class<?>[]) map.get("argType"));
        Object result2 = method2.invoke(clazz2,(Object[])map.get("args"));
        ctx.writeAndFlush(result2);

    }
}
