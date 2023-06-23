package net.flarepowered.nms.messages;

import net.flarepowered.nms.NMSUtils;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Titles {
    public void SendPacketTitle(Player player, String title, String subtitle) throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchFieldException, InvocationTargetException {
//        Class<?> packetClass = NMSUtils.getNMSClass("PacketPlayOutTitle");
//        Constructor<?> packetConstructor = packetClass.getConstructor(EnumTitleAction.class, IChatBaseComponent.class);
//        Object packet = packetConstructor.newInstance(enumTitleAction, iComponent);
//        Method sendPacket = NMSUtils.getNMSClass("PlayerConnection").getMethod("sendPacket", NMSUtils.getNMSClass("Packet"));
//        sendPacket.invoke(NMSUtils.getConnection(player), packet);
    }
}
