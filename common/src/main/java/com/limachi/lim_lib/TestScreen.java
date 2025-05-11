package com.limachi.lim_lib;

import com.limachi.lim_lib.client.screens.SimpleScreen;

import com.limachi.lim_lib.client.widgets.WidgetEditor;
import com.limachi.lim_lib.common.annotations.CmdArg;
import com.limachi.lim_lib.common.annotations.RegisterCommand;
import com.limachi.lim_lib.common.annotations.RegisterMsg;
import com.limachi.lim_lib.common.network.IS2CMsg;
import com.mojang.brigadier.context.CommandContext;
import dev.architectury.networking.NetworkManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

@Environment(EnvType.CLIENT)
public class TestScreen extends SimpleScreen {
    public TestScreen(Component title, Screen screen) { super(title, screen); }

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(new WidgetEditor());
    }

    @RegisterMsg
    public record OpenTestScreen(String title) implements IS2CMsg<OpenTestScreen> {
        @Override
        public void run(NetworkManager.PacketContext ctx) {
            if (Minecraft.getInstance() instanceof Minecraft mc) {
                mc.setScreen(new TestScreen(Component.literal(title), mc.screen));
            }
        }
    }

    @RegisterCommand("lim_lib test_screen <title>")
    @RegisterCommand("lim_lib test_screen")
    public static int openScreenCommand(CommandContext<CommandSourceStack> ctx, @CmdArg("title") String title) {
        if (ctx.getSource().getPlayer() instanceof ServerPlayer player) {
            if (title == null)
                title = "<default title>";
            new OpenTestScreen(title).sendToClient(player);
            return 1;
        }
        return 0;
    }
}
