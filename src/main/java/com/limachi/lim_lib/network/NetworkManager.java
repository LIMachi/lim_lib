package com.limachi.lim_lib.network;

import com.limachi.lim_lib.Log;
import com.limachi.lim_lib.ModAnnotation;
import com.limachi.lim_lib.Sides;
import com.limachi.lim_lib.network.messages.ContainerStackSizeOverride;
import com.limachi.lim_lib.registries.Registries;
import net.minecraft.core.NonNullList;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetDataPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.ContainerSynchronizer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.server.ServerLifecycleHooks;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Predicate;

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
    protected static final HashMap<Class<?>, SimpleChannel> ANONYMOUS_HANDLER_ACCESS = new HashMap<>();

    protected static SimpleChannel getChannel(String modId) {
        if (!HANDLERS.containsKey(modId))
            HANDLERS.put(modId, NetworkRegistry.newSimpleChannel(new ResourceLocation(modId, "network"), ()->"1", "1"::equals, "1"::equals));
        return HANDLERS.get(modId);
    }

    protected static <T extends Record & IRecordMsg> SimpleChannel getChannel(T msg) {
        return ANONYMOUS_HANDLER_ACCESS.get(msg.getClass());
    }

    protected static <T extends Record & IRecordMsg> void registerMsg(String modId, Class<T> clazz, int id) {
        SimpleChannel channel = getChannel(modId);
        channel.registerMessage(id,
                clazz,
                RecordSerde.compileRecordToBuffer(clazz),
                RecordSerde.compileBufferToRecord(clazz),
                (msg, ictx) -> {
                    if (!clazz.isInstance(msg)) {
                        Log.error(msg, "Message received does not match registered consumer of type: " + clazz + "! Please check your message registrations.");
                        return;
                    }
                    NetworkEvent.Context ctx = ictx.get();
                    Target t = NetworkManager.target(ctx);
                    if (t == Target.CLIENT)
                        ctx.enqueueWork(()->msg.clientWork(Sides.getPlayer()));
                    if (t == Target.SERVER)
                        ctx.enqueueWork(()->msg.serverWork(ctx.getSender()));
                    ctx.setPacketHandled(true);
                });
        ANONYMOUS_HANDLER_ACCESS.put(clazz, channel);
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

    public static <T extends Record & IRecordMsg> void toServer(T msg) {
        if (msg != null) getChannel(msg).sendToServer(msg);
    }

    public static <T extends Record & IRecordMsg> void toClients(String modId, T msg) {
        if (msg != null) {
            SimpleChannel channel = getChannel(modId);
            for (ServerPlayer player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers())
                if (!(player instanceof FakePlayer))
                    channel.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
        }
    }

    public static <T extends Record & IRecordMsg> void toClients(T msg) {
        if (msg != null) {
            SimpleChannel channel = getChannel(msg);
            for (ServerPlayer player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers())
                if (!(player instanceof FakePlayer))
                    channel.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
        }
    }

    public static <T extends Record & IRecordMsg> void toClient(String modId, ServerPlayer player, T msg) {
        if (msg != null && !(player instanceof FakePlayer)) getChannel(modId).sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    public static <T extends Record & IRecordMsg> void toClient(ServerPlayer player, T msg) {
        if (msg != null && !(player instanceof FakePlayer)) getChannel(msg).sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    public static <T extends Record & IRecordMsg> void toClients(String modId, Predicate<ServerPlayer> pred, T msg) {
        if (msg != null) {
            SimpleChannel channel = getChannel(modId);
            for (ServerPlayer player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers())
                if (!(player instanceof FakePlayer) && pred.test(player))
                    channel.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
        }
    }

    public static <T extends Record & IRecordMsg> void toClients(Predicate<ServerPlayer> pred, T msg) {
        if (msg != null) {
            SimpleChannel channel = getChannel(msg);
            for (ServerPlayer player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers())
                if (!(player instanceof FakePlayer) && pred.test(player))
                    channel.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
        }
    }

    /**
     * helper function to call at the creation of a menu to auto sync stacks that may be bigger than the vanilla size
     */
    public static void syncBigStackSize(AbstractContainerMenu menu, Inventory playerInventory) {
        if (playerInventory.player instanceof ServerPlayer serverPlayer) {
            menu.setSynchronizer(new ContainerSynchronizer() {

                @Override
                public void sendInitialData(@Nonnull AbstractContainerMenu menu, @Nonnull NonNullList<ItemStack> stacks, @Nonnull ItemStack carried, @Nonnull int[] data) {
                    serverPlayer.connection.send(new ClientboundContainerSetContentPacket(menu.containerId, menu.incrementStateId(), stacks, carried));
                    for (int i = 0; i < stacks.size(); ++i) {
                        int count = stacks.get(i).getCount();
                        if (count > 127)
                            toClient(serverPlayer, new ContainerStackSizeOverride(menu.containerId, i, count));
                    }
                    for (int i = 0; i < data.length; ++i)
                        serverPlayer.connection.send(new ClientboundContainerSetDataPacket(menu.containerId, i, data[i]));
                }

                @Override
                public void sendSlotChange(@Nonnull AbstractContainerMenu menu, int slot, @Nonnull ItemStack stack) {
                    serverPlayer.connection.send(new ClientboundContainerSetSlotPacket(menu.containerId, menu.incrementStateId(), slot, stack));
                    if (stack.getCount() > 127)
                        toClient(serverPlayer, new ContainerStackSizeOverride(menu.containerId, slot, stack.getCount()));
                }

                @Override
                public void sendCarriedChange(@Nonnull AbstractContainerMenu menu, @Nonnull ItemStack stack) {
                    serverPlayer.connection.send(new ClientboundContainerSetSlotPacket(-1, menu.incrementStateId(), -1, stack));
                }

                @Override
                public void sendDataChange(@Nonnull AbstractContainerMenu menu, int slot, int data) {
                    serverPlayer.connection.send(new ClientboundContainerSetDataPacket(menu.containerId, slot, data));
                }
            });
        }
    }
}
