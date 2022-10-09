package com.limachi.lim_lib;

import com.limachi.lim_lib.registries.ClientRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.registries.RegistryObject;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.POWER;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.POWERED;

public class RedstoneUtils {
    public static void hasRedstoneTint(RegistryObject<Block> block) {
        DistExecutor.unsafeCallWhenOn(Dist.CLIENT, ()->()->{ ClientRegistries.setColor(block, RedstoneUtils::getRedstoneTint); return null; });
    }

    @OnlyIn(Dist.CLIENT)
    public static int getRedstoneTint(BlockState state, BlockAndTintGetter getter, BlockPos pos, int index) {
        return RedStoneWireBlock.getColorForPower(state.getOptionalValue(POWER).orElse(state.getOptionalValue(POWERED).orElse(false) ? 15 : 0));
    }
}
