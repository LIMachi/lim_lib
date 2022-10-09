package com.limachi.lim_lib;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayer;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiPredicate;

@SuppressWarnings("unused")
public class EntityUtils {
    public static BlockPos oldBlockPos(Entity entity) {
        Vec3 d = entity.getDeltaMovement();
        Vec3 p = entity.position();
        return new BlockPos(p.x - d.x, p.y - d.y, p.z - d.z);
    }

    public static boolean entityChangedBlock(Entity entity) {
        return !entity.blockPosition().equals(oldBlockPos(entity));
    }

    public static boolean isMoving(Entity entity) {
        Vec3 t = entity.getDeltaMovement();
        return t.x != 0 || t.y != 0 || t.z != 0;
    }

    public static void updateSurroundingBlocks(Entity entity, int radius, @Nullable BiPredicate<BlockPos, BlockState> pred) {
        for (int x = -radius; x <= radius; ++x)
            for (int y = -radius; y <= radius; ++y)
                for (int z = -radius; z <= radius; ++z) {
                    BlockPos pos = entity.blockPosition().offset(x, y, z);
                    BlockState state = entity.level.getBlockState(pos);
                    if (pred == null || pred.test(pos, state))
                        entity.level.markAndNotifyBlock(pos, entity.level.getChunkAt(pos), state, state, 3, 512);
                }
    }

    public static Set<BlockPos> getAllIntersectingPos(Entity entity) {
        EntityDimensions hb = entity.getDimensions(entity.getPose());
        int mx = (int)Math.ceil(hb.width / 2. + entity.position().x);
        int my = (int)Math.ceil(hb.height + entity.position().y);
        int mz = (int)Math.ceil(hb.width / 2. + entity.position().z);
        HashSet<BlockPos> out = new HashSet<>();
        for (int x = (int)(entity.position().x - (hb.width / 2.)); x <= mx; ++x)
            for (int y = (int)entity.position().y; y <= my; ++y)
                for (int z = (int)(entity.position().z - (hb.width / 2.)); z <= mz; ++z)
                    out.add(new BlockPos(x, y, z));
        return out;
    }

    public static boolean truePlayer(Entity player) {
        return player instanceof Player && !(player instanceof FakePlayer);
    }
}
