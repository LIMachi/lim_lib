package com.limachi.lim_lib.test;

import com.limachi.lim_lib.LimLib;
import com.limachi.lim_lib.Strings;
import com.limachi.lim_lib.menus.WidgetContainerMenu;
import com.limachi.lim_lib.menus.slots.BigSlotSA;
import com.limachi.lim_lib.menus.slots.TankSlot;
import com.limachi.lim_lib.network.NetworkManager;
import com.limachi.lim_lib.reflection.Classes;
import com.limachi.lim_lib.registries.Registries;
import com.limachi.lim_lib.registries.annotations.RegisterMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

public class TestMenu extends WidgetContainerMenu {

    @RegisterMenu(skip = "com.limachi.lim_lib.LimLib:useTests")
    public static RegistryObject<MenuType<TestMenu>> R_MENU;

    protected ItemStack testStack = new ItemStack(Items.REDSTONE, 69);

    protected TestMenu(int menuId, Inventory playerInventory, Container container) {
        super((MenuType<TestMenu>)Registries.getRegistryObject(LimLib.COMMON_ID, MenuType.class, Strings.camelToSnake(Classes.getSimpleClassName())).get(), menuId);
        NetworkManager.syncBigStackSize(this, playerInventory);
        playerSlots(this, playerInventory, 84);
        addSlot(new BigSlotSA(new SlotAccess() {
            @Override
            public ItemStack get() {
                return testStack;
            }

            @Override
            public boolean set(ItemStack p_147306_) {
                testStack = p_147306_;
                return true;
            }
        }, ()->16, 50, 50));
        FluidTank tank = new FluidTank(1000);
        tank.fill(new FluidStack(Fluids.WATER, 1000), IFluidHandler.FluidAction.EXECUTE);
        addSlot(new TankSlot(()->tank, 50, 70, s->true));
        build();
    }

    public void build() {}

    public TestMenu(int id, Inventory playerInventory, FriendlyByteBuf buff) { this(id, playerInventory, new SimpleContainer(0)); }
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
        if (!player.level().isClientSide()) {
            NetworkHooks
//                    .openGui( //VERSION 1.18.2
                    .openScreen( //VERSION 1.19.2
                            (ServerPlayer) player, new MenuProvider() {
                @Override
                public Component getDisplayName() {
                    return
//                            new TextComponent( //VERSION 1.18.2
                            Component.literal( //VERSION 1.19.2
                                    "test");
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
