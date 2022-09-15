package com.limachi.lim_lib.menus;

import com.limachi.lim_lib.registries.annotations.RegisterMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.RegistryObject;

public class AutoScaleMenu extends CommonContainerMenu {

    public static void open(Player player, Container container, Component title) {
        if (!player.level.isClientSide())
            NetworkHooks
//                    .openGui( // VERSION 1.18.2
                    .openScreen( // VERSION 1.19.2
                            (ServerPlayer)player, new SimpleMenuProvider((id, playerInventory, _player) -> new AutoScaleMenu(id, playerInventory, container), title), buff->buff.writeInt(container.getContainerSize()));
    }

    @RegisterMenu
    public static RegistryObject<MenuType<?>> R_TYPE;

    public AutoScaleMenu(int id, Inventory playerInventory, Container container) {
        super(R_TYPE.get(), id, playerInventory, container);
        playerSlots(30 + getRows() * 18);
        if (container != null) {
            newSlotSection();
            int index = 0;
            for (int row = 0; row < getRows() && index < container.getContainerSize(); ++row)
                for (int column = 0; column < getColumns() && index < container.getContainerSize(); ++column)
                    addSlot(new Slot(container, index++, 8 + offsetX() + column * 18, 17 + row * 18));
            if (container.getContainerSize() > 54) {
                for (;index < container.getContainerSize(); ++index)
                    addSlot(new Slot(container, index, Integer.MIN_VALUE, Integer.MIN_VALUE));
            }
        }
    }

    public int validSlots() {
        return container.getContainerSize();
    }

    public int pages() {
        int s = container.getContainerSize();
        return s <= 54 ? 1 : (s - 48) / 8 + ((s - 48) % 8 == 0 ? 0 : 1);
    }

    public int offsetX() { return container.getContainerSize() <= 9 ? 3 * 18 : 0; }

    public int getRows() {
        int s = container.getContainerSize();
        if (s <= 9) return 3; //Dispenser layout
        if (s <= 54) return s / 9 + (s % 9 != 0 ? 1 : 0); //Chest layout
        return 6; //Double chest with scroll bar layout
    }

    public int getColumns() {
        int s = container.getContainerSize();
        if (s <= 9) return 3; //Dispenser layout
        if (s <= 54) return 9; //Chest layout
        return 8; //Double chest with scroll bar layout
    }

    public AutoScaleMenu(int id, Inventory playerInventory, FriendlyByteBuf buff) {
        this(id, playerInventory, new SimpleContainer(buff.readInt()));
    }

    @Override
    public boolean stillValid(Player player) { return container.stillValid(player); }
}
