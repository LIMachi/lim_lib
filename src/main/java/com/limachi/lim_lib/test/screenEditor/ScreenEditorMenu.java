package com.limachi.lim_lib.test.screenEditor;

import com.limachi.lim_lib.menus.WidgetContainerMenu;
import com.limachi.lim_lib.registries.annotations.RegisterMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.RegistryObject;

public class ScreenEditorMenu extends WidgetContainerMenu {

    @RegisterMenu(skip = "com.limachi.lim_lib.LimLib:useTests")
    public static RegistryObject<MenuType<ScreenEditorMenu>> R_TYPE;

    public ScreenEditorMenu(int id, Inventory playerInventory, FriendlyByteBuf buff) { this(id, playerInventory); }
    public ScreenEditorMenu(int id, Inventory playerInventory) { super(R_TYPE.get(), id); }
}
