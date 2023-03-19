package com.limachi.lim_lib;

import com.google.common.collect.ArrayListMultimap;
import com.limachi.lim_lib.blocks.IGetUseSneakWithItemEvent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber
public class Events {
    public static int tick = 0;
    private static final ArrayListMultimap<Integer, Runnable> pendingTasks = ArrayListMultimap.create();
    /**
     * queue a delayed task to be run in X ticks (minimum 1 tick)
     */
    public static void delayedTask(int ticksToWait, Runnable run) { if (ticksToWait <= 0) ticksToWait = 1; pendingTasks.put(ticksToWait + tick, run); }
    /**
     * handles delayed tasks on the TickEvent server side, if you need something delayed client side, please use the same function but in the client package
     */
    @SubscribeEvent
    public static void onTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            List<Runnable> tasks = pendingTasks.get(tick);
            for (Runnable task : tasks)
                task.run();
        } else if (event.phase == TickEvent.Phase.END) {
            pendingTasks.removeAll(tick);
            ++tick;
        }
    }

    @SubscribeEvent
    public static void acceptSneakUseOfBlockWithItem(PlayerInteractEvent.RightClickBlock event) {
        Block block = event
                .getWorld() // VERSION 1.18.2
//                .getLevel() // VERSION 1.19.2
                .getBlockState(event.getHitVec().getBlockPos()).getBlock();
        Item item = event
                .getPlayer() // VERSION 1.18.2
//                .getEntity() // VERSION 1.19.2
                .getItemInHand(event.getHand()).getItem();
        if (block instanceof IGetUseSneakWithItemEvent && !(item instanceof BlockItem)) {
            event.setUseBlock(Event.Result.ALLOW);
            event.setUseItem(Event.Result.DENY);
        }
    }
}
