package com.limachi.lim_lib.test;

import com.limachi.lim_lib.LimLib;
import com.limachi.lim_lib.Reflection;
import com.limachi.lim_lib.Strings;
import com.limachi.lim_lib.registries.Registries;
import com.limachi.lim_lib.registries.annotations.RegisterMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

public class TestMenu extends AbstractContainerMenu {

    @RegisterMenu
    public static RegistryObject<MenuType<TestMenu>> R_MENU;

    protected TestMenu(int menuId, Inventory playerInventory, Container container) {
        super((MenuType<TestMenu>)Registries.getRegistryObject(LimLib.COMMON_ID, Strings.camelToSnake(Reflection.getSimpleClassName())).get(), menuId);
        playerSlots(this, playerInventory, 84);
        build();
    }

    public void build() {}

    public TestMenu(int id, Inventory playerInventory) { this(id, playerInventory, new SimpleContainer(0)); }

    @Override
    public Slot addSlot(Slot slot) { return super.addSlot(slot); }

    public static <T extends TestMenu> void playerSlots(T menu, Inventory playerInventory, int y_inv) { playerSlots(menu, playerInventory, 8, y_inv, y_inv + 58); }
    public static <T extends TestMenu>  void playerSlots(T menu, Inventory playerInventory, int x, int y_inv, int y_belt) {
        for (int row = 0; row < 3; ++row)
            for (int column = 0; column < 9; ++column)
                menu.addSlot(new Slot(playerInventory, 9 + row * 9 + column, x + column * 18, y_inv + row * 18));
        for (int column = 0; column < 9; ++column)
            menu.addSlot(new Slot(playerInventory, column, x + column * 18, y_belt));
    }

    @Override
    public boolean stillValid(Player player) { return true; }

    //disable shift-click
    @Override
    public ItemStack quickMoveStack(Player p_38941_, int p_38942_) { return ItemStack.EMPTY; }

    public static <T extends TestMenu> void open(Class<T> menu, Player player, Container container) {
        if (!player.level.isClientSide()) {
            NetworkHooks.openGui((ServerPlayer) player, new MenuProvider() {
                @Override
                public Component getDisplayName() {
                    return new TextComponent("test");
                }

                @Nullable
                @Override
                public AbstractContainerMenu createMenu(int id, Inventory inventory, Player p_39956_) {
                    return new TestMenu(id, inventory);
                }
            });
        }
    }
}
