package com.limachi.lim_lib.test.screenEditor;

import com.limachi.lim_lib.registries.annotations.RegisterItem;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

public class ScreenEditorItem extends Item {

    @RegisterItem(skip = "com.limachi.lim_lib.LimLib:useTests")
    public static RegistryObject<Item> R_ITEM;

    public ScreenEditorItem() {
        super(new Properties());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide()) {
                NetworkHooks.openScreen((ServerPlayer) player, new MenuProvider() {
                    @Override
                    public Component getDisplayName() { return Component.literal("test"); }

                    @Nullable
                    @Override
                    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player p_39956_) {
                        return new ScreenEditorMenu(id, inventory);
                    }
                });
        }
        return super.use(level, player, hand);
    }
}
