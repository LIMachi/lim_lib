package com.limachi.lim_lib.integration.theOneProbePlugin;

import com.limachi.lim_lib.LimLib;
import mcjty.theoneprobe.api.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Function;
/*
public class TOPIMC  implements IProbeInfoProvider, Function<ITheOneProbe, Void> {
    @Override
    public Void apply(ITheOneProbe iTheOneProbe) {
        iTheOneProbe.registerProvider(this);
        return null;
    }

    @Override
    public ResourceLocation getID() { return new ResourceLocation(LimLib.COMMON_ID, "top_plugin"); }

    @Override
    public void addProbeInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, Player player, Level level, BlockState blockState, IProbeHitData iProbeHitData) {
        if (blockState.getBlock() instanceof IProbeInfoGiver g)
            g.addProbeInfo(probeMode, iProbeInfo, player, level, blockState, iProbeHitData);
    }
}*/