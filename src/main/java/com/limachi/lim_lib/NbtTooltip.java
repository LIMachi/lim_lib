package com.limachi.lim_lib;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent; //VERSION 1.18.2
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class NbtTooltip {

    @Configs.Config(cmt = "Add a tooltip to view NBT on items when in extra info mode (F3 + H)")
    public static boolean ADD_NBT_TOOLTIP = true;

    @Configs.Config(cmt = "Maximum number of lines of NBT before we need to scroll.", min = "8")
    public static int SCROLL_IF_MORE_LINES = 16;

    @Configs.Config(cmt = "Windows style scroll (scroll down to go down) or Mac style scroll (scroll up to go down)")
    public static boolean WIDOWS_SCROLL = true;

    private static boolean can_scroll = false;
    private static int scroll = 0;

    private static final Component SCROLL_SUGGESTION =
            new TranslatableComponent( //VERSION 1.18.2
//            Component.translatable( //VERSION 1.19.2
                    "extended_tooltip.nbt_tooltip.scroll_suggestion").withStyle(ChatFormatting.LIGHT_PURPLE);
    private static final Component SHOW_NBT_SUGGESTION =
            new TranslatableComponent( //VERSION 1.18.2
//            Component.translatable( //VERSION 1.19.2
                    "extended_tooltip.nbt_tooltip.use_alt").withStyle(ChatFormatting.LIGHT_PURPLE);

    @SubscribeEvent
    public static void addNbtTooltip(ItemTooltipEvent event) {
        if (ADD_NBT_TOOLTIP && event.getItemStack().getTag() != null && event.getFlags().isAdvanced()) {
            if (Screen.hasAltDown()) {
                List<Component> tooltip = event.getToolTip();
                Component remove = null;
                for (Component t : tooltip)
                    if (t.getString().matches("NBT: [0-9]+ tag\\(s\\)")) {
                        remove = t;
                        break;
                    }
                if (remove != null)
                    tooltip.remove(remove);
                List<Component> nbt = TextUtils.prettyTag(event.getItemStack().getTag());
                if (nbt.size() > SCROLL_IF_MORE_LINES) {
                    can_scroll = true;
                    scroll = Mth.clamp(scroll, 0, nbt.size() - SCROLL_IF_MORE_LINES + 1);
                    nbt = nbt.subList(scroll, scroll + SCROLL_IF_MORE_LINES - 1);
                    nbt.add(0, SCROLL_SUGGESTION);
                } else
                    can_scroll = false;
                tooltip.addAll(nbt);
            } else {
                event.getToolTip().add(SHOW_NBT_SUGGESTION);
                can_scroll = false;
            }
        } else
            can_scroll = false;
        if (!can_scroll)
            scroll = 0;
    }

    @SubscribeEvent
    public static void scrollNbtTooltip(
//            ScreenEvent.MouseScrolled.Pre //VERSION 1.19.2
            ScreenEvent.MouseScrollEvent.Pre //VERSION 1.18.2
                    event) {
        if (can_scroll) {
            scroll += event.getScrollDelta() * (WIDOWS_SCROLL ? -1 : 1);
            event.setCanceled(true);
        }
    }
}
