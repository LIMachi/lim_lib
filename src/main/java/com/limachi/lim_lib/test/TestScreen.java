package com.limachi.lim_lib.test;

import com.limachi.lim_lib.LimLib;
import com.limachi.lim_lib.registries.client.annotations.RegisterMenuScreen;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

@RegisterMenuScreen
public class TestScreen extends AbstractContainerScreen<TestMenu> {
    public static final ResourceLocation DEFAULT_BACKGROUND = new ResourceLocation(LimLib.COMMON_ID, "textures/screen/default_background.png");

    public TestScreen(TestMenu menu, Inventory inventory, Component title) { super(menu, inventory, title); }

    @Override
    protected void renderBg(PoseStack ps, float tick, int mouseX, int mouseY) {
        renderBackground(ps);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, DEFAULT_BACKGROUND);
        blit(ps, leftPos, topPos, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);
    }
}
