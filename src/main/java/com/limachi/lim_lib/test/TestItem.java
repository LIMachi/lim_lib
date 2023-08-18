package com.limachi.lim_lib.test;

import com.limachi.lim_lib.KeyMapController;
import com.limachi.lim_lib.registries.annotations.RegisterItem;
import com.limachi.lim_lib.scrollSystem.IScrollItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;

public class TestItem extends Item implements IScrollItem {
    @RegisterItem(skip = "com.limachi.lim_lib.LimLib:useTests")
    public static RegistryObject<Item> R_ITEM;

    public TestItem() { super(new Properties()); }

    /**
     * test if the scroll works as intended server side, also set the SaveData test to hold the cumulative scroll as an int
     */
    @Override
    public void scroll(Player player, int slot, int delta) {
//        TestData test = SaveDataManager.getInstance("test", player.level);
//        delta += test.getTest();
//        Log.debug("Validated scroll server side: " + delta);
//        test.setTest(delta);
    }

    @Override
    public void scrollFeedBack(Player player, int slot, int delta) {}

    /**
     * active the scroll system if the player holding the item is sneaking
     */
    @Override
    public boolean canScroll(Player player, int slot) { return KeyMapController.SNEAK.getState(player); }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide() && KeyMapController.SNEAK.getState(player))
            TestMenu.open(TestMenu.class, player, new SimpleContainer(0));
        return super.use(level, player, hand);
    }
}
