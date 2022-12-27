package com.limachi.lim_lib.integration.JEIPlugin;

import com.limachi.lim_lib.LimLib;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.function.Supplier;

@SuppressWarnings("unused")
@JeiPlugin
public class JEIImpl implements IModPlugin {
    @Override
    @Nonnull
    public ResourceLocation getPluginUid() { return new ResourceLocation(LimLib.COMMON_ID, "jei_plugin"); }

    @Override
    public void registerRecipes(@Nonnull IRecipeRegistration registration) {
        for (Map.Entry<Supplier<ItemStack>, String> info : JEIInfo.INFOS.entrySet())
            registration.addIngredientInfo(info.getKey().get(), VanillaTypes.ITEM_STACK, Component.translatable(info.getValue()));
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGlobalGuiHandler(new JEIShouldNotRenderOverWidgets());
    }
}
