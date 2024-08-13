package com.wentry.wraft.spring.config;

import com.wentry.wraft.transport.packet.Packet;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 管理者，依赖Spring容器
 */
@Component
public class Packets implements ApplicationContextAware, SmartInitializingSingleton {

    private ApplicationContext ctx;

    static final Map<Integer, Class<? extends Packet>> packetCls = new ConcurrentHashMap<>();

    @Override
    public void afterSingletonsInstantiated() {
        Map<String, Packet> allPacket = ctx.getBeansOfType(Packet.class);
        for (Map.Entry<String, Packet> ety : allPacket.entrySet()) {
            Packet value = ety.getValue();
            packetCls.put(ety.getValue().type(), value.getClass());
        }
    }

    public static Class<? extends Packet> getClz(int type) {
        return packetCls.get(type);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ctx = applicationContext;
    }
}
