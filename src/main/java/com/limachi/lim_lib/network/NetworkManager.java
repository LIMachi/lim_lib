package com.limachi.lim_lib.network;

import com.limachi.lim_lib.Log;
import com.limachi.lim_lib.ModAnnotation;
import com.limachi.lim_lib.Sides;
import com.limachi.lim_lib.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.*;

@SuppressWarnings("unused")
public class NetworkManager {
    @SuppressWarnings("unchecked")
    private static <T extends Record & IRecordMsg> void discoverMsgRegistry(String modId) {
        TreeMap<String, Class<T>> messages = new TreeMap<>(String::compareTo);
        for (ModAnnotation a : ModAnnotation.iterModAnnotations(modId, RegisterMsg.class))
            if (Record.class.isAssignableFrom(a.getAnnotatedClass())) {
                if (IRecordMsg.class.isAssignableFrom(a.getAnnotatedClass())) {
                    String name = Registries.name(a);
                    if (messages.put(name, (Class<T>) a.getAnnotatedClass()) != null)
                        Log.error("@RegisterMsg duplicated name: " + name);
//                    registerMsg(modId, (Class<T>)a.getAnnotatedClass(), a.getData("value", -1));
                } else {
                    Log.error(a.getAnnotatedClass(), "@RegisterMsg on a record not implementing IRecordMsg!");
                    System.exit(-1);
                    return;
                }
            }
            else {
                Log.error(a.getAnnotatedClass(), "@RegisterMsg on a non record class!");
                System.exit(-1);
                return;
            }
        int index = 0;
        for (Map.Entry<String, Class<T>> e : messages.entrySet())
            registerMsg(modId, e.getValue(), index++);
    }

    public static void register(String modId) {
        discoverMsgRegistry(modId);
    }

    protected static final HashMap<String, SimpleChannel> HANDLERS = new HashMap<>();

    protected static SimpleChannel getChannel(String modId) {
        if (!HANDLERS.containsKey(modId))
            HANDLERS.put(modId, NetworkRegistry.newSimpleChannel(new ResourceLocation(modId, "network"), ()->"1", "1"::equals, "1"::equals));
        return HANDLERS.get(modId);
    }

    protected static <T extends Record & IRecordMsg> void registerMsg(String modId, Class<T> clazz, int id) {
        getChannel(modId).registerMessage(id,
                clazz,
                Buffer::recordToBuffer,
                buff->Buffer.recordFromBuffer(clazz, buff),
                (msg, ictx) -> {
                    if (!clazz.isInstance(msg)) {
                        Log.error(msg, "Message received does not match registered consumer of type: " + clazz + "! Please check your message registrations.");
                        return;
                    }
                    NetworkEvent.Context ctx = ictx.get();
                    NetworkManager.Target t = NetworkManager.target(ctx);
                    if (t == NetworkManager.Target.CLIENT)
                        ctx.enqueueWork(()->(msg).clientWork(Sides.getPlayer()));
                    if (t == NetworkManager.Target.SERVER)
                        ctx.enqueueWork(()->(msg).serverWork(ctx.getSender()));
                    ctx.setPacketHandled(true);
                });
    }

    public enum Target {
        CLIENT,
        SERVER,
        I_M_NOT_SURE
    }

    public static Target target(NetworkEvent.Context ctx) {
        if (ctx.getDirection().getOriginationSide() == LogicalSide.SERVER)
            return ctx.getDirection().getReceptionSide() == LogicalSide.CLIENT ? Target.CLIENT : Target.I_M_NOT_SURE;
        if (ctx.getDirection().getOriginationSide() == LogicalSide.CLIENT)
            return ctx.getDirection().getReceptionSide() == LogicalSide.SERVER ? Target.SERVER : Target.I_M_NOT_SURE;
        return Target.I_M_NOT_SURE;
    }

    public static <T extends Record & IRecordMsg> void toServer(String modId, T msg) {
        if (msg != null) getChannel(modId).sendToServer(msg);
    }
    public static <T extends Record & IRecordMsg> void toClients(String modId, T msg) {
        if (msg != null) {
            SimpleChannel channel = getChannel(modId);
            for (ServerPlayer player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers())
                channel.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
        }
    }
    public static <T extends Record & IRecordMsg> void toClient(String modId, ServerPlayer player, T msg) {
        if (msg != null && !(player instanceof FakePlayer)) getChannel(modId).sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }
}
