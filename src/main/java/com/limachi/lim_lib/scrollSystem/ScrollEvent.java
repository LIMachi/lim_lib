package com.limachi.lim_lib.scrollSystem;

import com.limachi.lim_lib.ModBase;
import com.limachi.lim_lib.network.NetworkManager;
import com.limachi.lim_lib.network.messages.ScrolledBlockMsg;
import com.limachi.lim_lib.network.messages.ScrolledItemMsg;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class ScrollEvent {

    public static int COUNTDOWN_LENGTH_BLOCK = 20;
    public static int COUNTDOWN_LENGTH_ITEM = 5;

    private static int DELTA = 0;
    private static int COUNTDOWN = -1;
    private static BlockPos POS = null;
    private static int SLOT = -1;

    @SubscribeEvent
    public static void scrollBlockEvent(InputEvent.MouseScrollEvent event) { //VERSION 1.18.2
//    public static void scrollBlockEvent(InputEvent.MouseScrollingEvent event) { //VERSION 1.19.2
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            if (player.getMainHandItem().getItem() instanceof IScrollItem item && item.canScroll(player, player.getInventory().selected)) {
                DELTA += event.getScrollDelta();
                COUNTDOWN = COUNTDOWN_LENGTH_ITEM;
                SLOT = player.getInventory().selected;
                item.scrollFeedBack(player, SLOT, DELTA);
                event.setCanceled(true);
            } else if (player.getOffhandItem().getItem() instanceof IScrollItem item && item.canScroll(player, Inventory.SLOT_OFFHAND)) {
                DELTA += event.getScrollDelta();
                COUNTDOWN = COUNTDOWN_LENGTH_ITEM;
                SLOT = Inventory.SLOT_OFFHAND;
                item.scrollFeedBack(player, SLOT, DELTA);
                event.setCanceled(true);
            } else {
                HitResult target = Minecraft.getInstance().hitResult;
                if (target != null && target.getType() == HitResult.Type.BLOCK) {
                    BlockPos pos = ((BlockHitResult) target).getBlockPos();
                    BlockEntity be = player.level.getBlockEntity(pos);
                    Block block = player.level.getBlockState(pos).getBlock();
                    if ((block instanceof IScrollBlock sb && sb.canScroll(player, pos)) || (be instanceof IScrollBlock sbe && sbe.canScroll(player, pos))) {
                        POS = pos;
                        DELTA += event.getScrollDelta();
                        COUNTDOWN = COUNTDOWN_LENGTH_BLOCK;
                        if (block instanceof IScrollBlock sb)
                            sb.scrollFeedBack(player.level, pos, DELTA, player);
                        if (be instanceof IScrollBlock sbe)
                            sbe.scrollFeedBack(player.level, pos, DELTA, player);
                        event.setCanceled(true);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void flushOnFinishedCountDown(TickEvent.ClientTickEvent event) {
        if (COUNTDOWN < 0) return;
        if (COUNTDOWN-- == 0) {
            if (DELTA != 0) {
                if (POS != null)
                    NetworkManager.toServer(ModBase.COMMON_ID, new ScrolledBlockMsg(POS, DELTA));
                if (SLOT != -1)
                    NetworkManager.toServer(ModBase.COMMON_ID, new ScrolledItemMsg(SLOT, DELTA));
            }
            DELTA = 0;
            POS = null;
            SLOT = -1;
        }
    }
}
