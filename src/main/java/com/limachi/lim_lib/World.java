package com.limachi.lim_lib;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

@SuppressWarnings("unused")
public class World {

    public static Iterable<? extends Level> getAllLevels() {
        if (Sides.isLogicalClient())
            return Collections.singletonList(Sides.getPlayer().level());
        return Sides.getServer().getAllLevels();
    }

    public static Level getLevel(ResourceKey<Level> reg) {
        if (Sides.isLogicalClient()) {
            Level t = Sides.getPlayer().level();
            return t.dimension().equals(reg) ? t : null;
        }
        return Sides.getServer().getLevel(reg);
    }

    public static Level getLevel(String reg) {
        return getLevel(ResourceKey.create(Registries.DIMENSION, new ResourceLocation(reg)));
    }

    public static String asString(Level level) {
        if (level == null) return "";
        return level.dimension().location().toString();
    }

    public static Level overworld() { return getLevel(Level.OVERWORLD); }
    public static Level nether() { return getLevel(Level.NETHER); }
    public static Level end() { return getLevel(Level.END); }

    public static long tick() { return overworld().getGameTime(); }

    public static BlockPos getWorldSpawn(ResourceKey<Level> reg) {
        Level level = getLevel(reg);
        if (level == null) return new BlockPos(0, 64, 0);
        LevelData data = level.getLevelData();
        return new BlockPos(data.getXSpawn(), data.getYSpawn(), data.getZSpawn());
    }

    public static boolean replaceBlockAndGiveBack(@Nullable Level level, BlockPos pos, Block block, Player player) {
        ItemStack prev = player.getMainHandItem();
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(block, 64));
        boolean ok = replaceBlockAndGiveBack(level == null ? player.level() : level, pos, player, InteractionHand.MAIN_HAND, !player.isCreative(), p->true);
        player.setItemInHand(InteractionHand.MAIN_HAND, prev);
        return ok;
    }

    public static boolean replaceBlockAndGiveBack(Level level, BlockPos pos, Player player, InteractionHand hand, boolean giveBack, Predicate<BlockState> isNewStateValid) {
        if (level == null || level.isClientSide()) return false;
        ItemStack block = player.getItemInHand(hand);
        if (!(block.getItem() instanceof BlockItem)) return false;
        Block replace = ((BlockItem)block.getItem()).getBlock();
        Direction dir = Direction.orderedByNearest(player)[0];
        Vec3 hit = new Vec3((double)pos.getX() + 0.5D + (double)dir.getStepX() * 0.5D, (double)pos.getY() + 0.5D + (double)dir.getStepY() * 0.5D, (double)pos.getZ() + 0.5D + (double)dir.getStepZ() * 0.5D);
        BlockState prev = level.getBlockState(pos);
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        BlockPlaceContext use = new BlockPlaceContext(player, hand, block, new BlockHitResult(hit, dir, pos, true));
        BlockState next = replace.getStateForPlacement(use);
        if (!isNewStateValid.test(next)) {
            level.setBlock(pos, prev, 3);
            return false;
        }
        InteractionResult ok = ((BlockItem)block.getItem()).place(use);
        if (ok.consumesAction()) {
            if (giveBack) PlayerUtils.giveOrDrop(player, new ItemStack(prev.getBlock()));
            return true;
        } else {
            level.setBlock(pos, prev, 3);
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends BlockEntity> List<T> getTileEntitiesWithinAABB(Level level, Class<T> beClass, AABB aabb, @Nullable Predicate<T> pred) {
        ArrayList<T> out = new ArrayList<>();
        for (int x = (int)aabb.minX; x <= aabb.maxX; ++x)
            for (int y = (int)aabb.minY; y <= aabb.maxY; ++y)
                for (int z = (int)aabb.minZ; z <= aabb.maxZ; ++z) {
                    BlockEntity be = level.getBlockEntity(new BlockPos(x, y, z));
                    if (beClass.isInstance(be) && (pred == null || pred.test((T)be)))
                        out.add((T)be);
                }
        return out;
    }

    /*
    private static Entity teleport(Entity entityIn, ServerLevel worldIn, double x, double y, double z, float yaw, float pitch) { //modified version of TeleportCommand.java: 123: TeleportCommand#teleport(CommandSource source, Entity entityIn, ServerWorld worldIn, double x, double y, double z, Set<SPlayerPositionLookPacket.Flags> relativeList, float yaw, float pitch, @Nullable TeleportCommand.Facing facing) throws CommandSyntaxException
        if (entityIn.isRemoved()) return entityIn;
        SyncUtils.XPSnapShot xp = entityIn instanceof ServerPlayer ? new SyncUtils.XPSnapShot((Player)entityIn) : SyncUtils.XPSnapShot.ZERO;
        Set<ClientboundPlayerPositionPacket.RelativeArgument> set = EnumSet.noneOf(ClientboundPlayerPositionPacket.RelativeArgument.class);
        set.add(ClientboundPlayerPositionPacket.RelativeArgument.X_ROT);
        set.add(ClientboundPlayerPositionPacket.RelativeArgument.Y_ROT);
        if (entityIn instanceof ServerPlayer) {
            ChunkPos chunkpos = new ChunkPos(new BlockPos(x, y, z));
            worldIn.getChunkSource().addRegionTicket(TicketType.POST_TELEPORT, chunkpos, 1, entityIn.getId());
            entityIn.stopRiding();
            if (((ServerPlayer)entityIn).isSleeping())
                ((ServerPlayer)entityIn).stopSleepInBed(true, true);
            if (worldIn == entityIn.level)
                ((ServerPlayer)entityIn).connection.teleport(x, y, z, yaw, pitch, set);
            else
                ((ServerPlayer)entityIn).teleportTo(worldIn, x, y, z, yaw, pitch);
            entityIn.setYHeadRot(yaw);
        } else {
            float f1 = Mth.wrapDegrees(yaw);
            float f = Mth.wrapDegrees(pitch);
            f = Mth.clamp(f, -90.0F, 90.0F);
            if (worldIn == entityIn.level) {
                entityIn.moveTo(x, y, z, f1, f);
                entityIn.setYHeadRot(f1);
            } else {
                entityIn.unRide();
                Entity entity = entityIn;
                entityIn = entityIn.getType().create(worldIn);
                if (entityIn == null)
                    return null;
                entityIn.restoreFrom(entity);
                entityIn.moveTo(x, y, z, f1, f);
                entityIn.setYHeadRot(f1);
                worldIn.addDuringTeleport(entityIn);
                entity.setRemoved(Entity.RemovalReason.CHANGED_DIMENSION);
            }
        }
        if (!(entityIn instanceof LivingEntity) || !((LivingEntity)entityIn).isFallFlying()) {
            entityIn.setDeltaMovement(entityIn.getDeltaMovement().multiply(1.0D, 0.0D, 1.0D));
            entityIn.setOnGround(true);
        }
        if (entityIn instanceof PathfinderMob) {
            ((PathfinderMob)entityIn).getNavigation().stop();
        }
        if (entityIn instanceof ServerPlayer) {
            SyncUtils.resyncXP((ServerPlayer) entityIn, xp);
        }
        return entityIn;
    }
*/
    public static Entity teleportEntity(Entity entity, ResourceKey<Level> destType, double x, double y, double z, float xRot, float yRot) {
        if (entity == null || entity.level().isClientSide()) return null;
        ServerLevel world;
        if (destType != null && entity.getServer() != null)
            world = entity.getServer().getLevel(destType);
        else
            world = (ServerLevel)entity.level();
        return world != null && entity.teleportTo(world, x, y, z, Set.of(), yRot, xRot) ? entity : null;
    }

    public static Entity teleportEntity(Entity entity, ResourceKey<Level> destType, BlockPos destPos, float xRot, float yRot) {
        return teleportEntity(entity, destType, destPos.getX() + 0.5, destPos.getY(), destPos.getZ() + 0.5, xRot, yRot);
    }

    public static Entity teleportEntity(Entity entity, ResourceKey<Level> destType, BlockPos destPos) {
        if (entity == null) return null;
        return teleportEntity(entity, destType, destPos.getX() + 0.5, destPos.getY(), destPos.getZ() + 0.5, entity.getXRot(), entity.getYRot());
    }

    public static Entity teleportEntity(Entity entity, ResourceKey<Level> destType, Vec3 vec, float xRot, float yRot) {
        return teleportEntity(entity, destType, vec.x, vec.y, vec.z, xRot, yRot);
    }

    public static Entity teleportEntity(Entity entity, ResourceKey<Level> destType, Vec3 vec) {
        return teleportEntity(entity, destType, vec.x, vec.y, vec.z, entity.getXRot(), entity.getYRot());
    }

    public static void temporaryChunkLoad(Level level, BlockPos pos, int area) {
        if (level instanceof ServerLevel sl)
            sl.getChunkSource().addRegionTicket(TicketType.PORTAL, new ChunkPos(pos), area, pos, true);
    }

    public static void temporaryChunkLoad(Level level, BlockPos pos) {
        temporaryChunkLoad(level, pos, 3);
    }
}
