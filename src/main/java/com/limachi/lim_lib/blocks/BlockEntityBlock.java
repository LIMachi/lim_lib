package com.limachi.lim_lib.blocks;

import com.limachi.lim_lib.blockEntities.IOnRemoveBlockListener;
import com.limachi.lim_lib.blockEntities.IOnUseBlockListener;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

public class BlockEntityBlock extends BaseEntityBlock {

    RegistryObject<BlockEntityType<BlockEntity>> betr;

    protected BlockEntityBlock(Properties props, RegistryObject<BlockEntityType<BlockEntity>> betr) {
        super(props);
        this.betr = betr;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return betr.get().create(pos, state);
    }

    @Override
    public void onRemove(BlockState block, Level level, BlockPos pos, BlockState state, boolean bool) {
        if (!block.is(state.getBlock())) {
            if (level.getBlockEntity(pos) instanceof IOnRemoveBlockListener be)
                be.onRemove(block, level, pos, state, bool);
            super.onRemove(block, level, pos, state, bool);
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.getBlockEntity(pos) instanceof IOnUseBlockListener be)
            be.use(state, level, pos, player, hand, hit);
        return super.use(state, level, pos, player, hand, hit);
    }
}
