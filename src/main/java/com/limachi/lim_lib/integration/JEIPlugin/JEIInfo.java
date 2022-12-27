package com.limachi.lim_lib.integration.JEIPlugin;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class JEIInfo {
    static final HashMap<Supplier<ItemStack>, String> INFOS = new HashMap<>();

    public static void registerInfo(ItemStack stack, String translationKey) { INFOS.put(()->stack, translationKey); }
    public static void registerInfo(RegistryObject<? extends Item> rItem, String translationKey) { INFOS.put(()->new ItemStack(rItem.get()), translationKey); }
}
