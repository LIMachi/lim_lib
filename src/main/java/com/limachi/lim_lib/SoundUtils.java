package com.limachi.lim_lib;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;

public class SoundUtils {
    public static void startRecord(Level in, BlockPos at, int id) {
        in.levelEvent(1010, at, id);
    }

    public static void startRecord(Level in, BlockPos at, ItemStack record) {
        int id = 0;
        if (record.getItem() instanceof RecordItem r)
            id = Item.getId(r);
        startRecord(in, at, id);
    }

    public static void stopRecord(Level in, BlockPos at) {
        startRecord(in, at, 0);
    }

    public static void playComparatorClick(Level in, BlockPos at, int pitchMultiplier) {
        in.playSound(null, at, SoundEvents.COMPARATOR_CLICK, SoundSource.BLOCKS, .3f, .5f + .05f * pitchMultiplier);
    }

    public static void playNote(Level in, BlockPos at, NoteBlockInstrument instrument, int id) {
        in.playSound((Entity)null, at, instrument.getSoundEvent().get(), SoundSource.RECORDS, 3.f, (float)Math.pow(2.f, (double)(id - 12) / 12.));
    }

    public static void playNoteWithEvent(Level in, BlockPos at, NoteBlockInstrument instrument, int id) {
        net.minecraftforge.event.level.NoteBlockEvent.Play e = new net.minecraftforge.event.level.NoteBlockEvent.Play(in, at, in.getBlockState(at), id, instrument);
        if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(e)) return;
        playNote((Level)e.getLevel(), e.getPos(), e.getInstrument(), e.getVanillaNoteId());
    }
}
