package com.limachi.lim_lib;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.limachi.lim_lib.registries.Registries;
import com.limachi.lim_lib.registries.ClientRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.RegistryObject;

import java.io.File;
import java.util.ArrayList;

@Mod(ModBase.COMMON_ID)
public class LimLib extends ModBase {
    public static final boolean USE_TESTS;
    public static final ArrayList<String> BLOCK_KEYS;

    static {
        File config_file = new File(FMLPaths.CONFIGDIR.get().toFile(), COMMON_ID + "_tests.toml");
        CommentedFileConfig test = CommentedFileConfig.of(config_file);
        test.load();
        test.add("use_tests", false);
        USE_TESTS = test.get("use_tests");
        test.save();

        CommentedFileConfig blocks = CommentedFileConfig.of(new File(FMLPaths.CONFIGDIR.get().toFile(), COMMON_ID + "_blocks.toml"));
        blocks.load();
        blocks.add("blocks", new ArrayList<String>());
        BLOCK_KEYS = blocks.get("blocks");
        blocks.save();

        for (String regKey : BLOCK_KEYS) {
            RegistryObject<Block> rBlock = Registries.block(COMMON_ID, regKey, ()->new Block(BlockBehaviour.Properties.copy(Blocks.AMETHYST_BLOCK)));
            Registries.item(COMMON_ID, regKey, ()->new BlockItem(rBlock.get(), new Item.Properties()), null, null);
        }
    }

    public static boolean useTests(Class<?> annotation, String key) {
        return !USE_TESTS;
    }

    public LimLib() {
        super(ModBase.COMMON_ID, "LimLib", true, null);
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.register(Registries.class);
        DistExecutor.unsafeCallWhenOn(Dist.CLIENT, ()->()->{ bus.register(ClientRegistries.class); return true; });
    }
}
