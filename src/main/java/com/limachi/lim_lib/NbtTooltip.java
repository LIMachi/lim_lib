package com.limachi.lim_lib;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
//import net.minecraft.network.chat.TranslatableComponent; //VERSION 1.18.2
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class NbtTooltip {

    @Configs.Config(cmt = "Add a tooltip to view NBT on items when in extra info mode (F3 + H)")
    public static boolean ADD_NBT_TOOLTIP = true;

    @SubscribeEvent
    public static void addExtendedTooltip(ItemTooltipEvent event) {
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
                tooltip.addAll(TextUtils.prettyTag(event.getItemStack().getTag()));

            } else
                event.getToolTip().add(
//                        new TranslatableComponent( //VERSION 1.18.2
                        Component.translatable( //VERSION 1.19.2
                                "extended_tooltip.nbt_tooltip.use_alt"));
        }
    }
}
