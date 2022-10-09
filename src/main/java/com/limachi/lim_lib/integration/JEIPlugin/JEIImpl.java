package com.limachi.lim_lib.integration.JEIPlugin;

import com.limachi.lim_lib.LimLib;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Map;
import java.util.function.Supplier;

@JeiPlugin
public class JEIImpl implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() { return new ResourceLocation(LimLib.COMMON_ID, "jei_plugin"); }

    @Override
    public void registerRecipes(IRecipeRegistration reg) {
        for (Map.Entry<Supplier<ItemStack>, String> info : JEIInfo.INFOS.entrySet())
            reg.addIngredientInfo(info.getKey().get(), VanillaTypes.ITEM_STACK, Component.translatable(info.getValue()));
    }
}
