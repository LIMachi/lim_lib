package com.limachi.lim_lib.network;

import com.limachi.lim_lib.ModAnnotation;
import com.limachi.lim_lib.Sides;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.HashMap;

@SuppressWarnings("unused")
public class NetworkManager {
    @SuppressWarnings("unchecked")
    private static <T extends Record & IRecordMsg> void discoverMsgRegistry(String modId) {
        for (ModAnnotation a : ModAnnotation.iterModAnnotations(modId, RegisterMsg.class))
            registerMsg(a.getData("modId", modId), (Class<T>)a.getAnnotatedClass(), a.getData("value", -1));
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
                        //FIXME: add some kind of error there
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
