package com.limachi.lim_lib.test;

import com.limachi.lim_lib.blockEntities.ListContainerBlockEntity;
import com.limachi.lim_lib.blocks.BlockEntityBlock;
import com.limachi.lim_lib.registries.annotations.RegisterBlock;
import com.limachi.lim_lib.registries.annotations.RegisterBlockEntity;
import com.limachi.lim_lib.registries.annotations.RegisterBlockItem;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
//import net.minecraft.network.chat.TextComponent; //VERSION 1.18.2
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.registries.RegistryObject;

public class TestChest {
    public static class TestChestBlock extends BlockEntityBlock {
        @RegisterBlock(skip = "com.limachi.lim_lib.LimLib:useTests")
        public static RegistryObject<Block> R_BLOCK;

        @RegisterBlockItem(skip = "com.limachi.lim_lib.LimLib:useTests")
        public static RegistryObject<Item> R_ITEM;

        public TestChestBlock() { super(Properties.of(Material.WOOD, MaterialColor.DIRT).strength(2.0F, 6.0F), TestChestBlockEntity.R_TYPE); }
    }

    public static class TestChestBlockEntity extends ListContainerBlockEntity {
        @RegisterBlockEntity(skip = "com.limachi.lim_lib.LimLib:useTests")
        public static RegistryObject<BlockEntityType<BlockEntity>> R_TYPE;

        public TestChestBlockEntity(BlockPos pos, BlockState state) { super(R_TYPE.get(), pos, state); }

        @Override
        protected int initialContainerSize() { return 60; }

        @Override
        protected Component getDefaultName() { return
//                new TextComponent( //VERSION 1.18.2
                Component.literal( //VERSION 1.19.2
                        "Test Chest"); }
    }
}
