package org.example.version2.server;

import cn.hutool.json.JSONUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.example.common.Response;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

public class InvokeHandler2 extends ChannelInboundHandlerAdapter {

    private String getImplByMap(Map<String, Object> map) throws ClassNotFoundException {
        String interfacePath = "org.example.api";
        String calssName = (String) map.get("interfaceName");
        int lastDot = calssName.lastIndexOf(".");
        String interfaceName = calssName.substring(lastDot);
        Class superClass = Class.forName(interfacePath+interfaceName);


        Reflections reflections = new Reflections("org.example.version1.server");

        Set<Class> implClassSet = reflections.getSubTypesOf(superClass);

        if (implClassSet.size()==0){
            return null;
        }else if (implClassSet.size() > 1){
            return null;
        }else {
            Class[] classes = implClassSet.toArray(new Class[0]);
            return classes[0].getName(); //得到实现类的名字
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        Map<String,Object> map = (Map<String, Object>) msg;
        Object clazz2 = Class.forName(getImplByMap(map)).newInstance();
        Method method2 = clazz2.getClass().getMethod((String) map.get("methodName"),
                (Class<?>[]) map.get("argType"));
        Object result2 = method2.invoke(clazz2,(Object[])map.get("args"));

        Response response = new Response();
        response.setData(result2);
        response.setId((Long) map.get("reqId"));
        System.out.println("server response ---- "+ response.toString());
        String jsonStr = JSONUtil.toJsonStr(response);

        ctx.writeAndFlush(jsonStr);

    }
}
