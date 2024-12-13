package com.limachi.lim_lib.mixin.client;

import com.limachi.lim_lib.LimLib;
import com.limachi.lim_lib.menus.slots.TankSlot;
import com.limachi.lim_lib.render.RenderUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.text.DecimalFormat;
import java.util.Set;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin {

    @Shadow
    private Slot clickedSlot;
    @Shadow
    private ItemStack draggingItem;
    @Shadow
    private boolean isSplittingStack;
    @Final
    @Shadow
    protected AbstractContainerMenu menu;
    @Shadow
    protected boolean isQuickCrafting;
    @Final
    @Shadow
    protected Set<Slot> quickCraftSlots;
    @Shadow
    private int quickCraftingType;
    @Shadow
    protected int imageWidth;

    @Unique
    private static final DecimalFormat size_formatter = new DecimalFormat("0.#");

    @Unique
    private static final ResourceLocation FLUID_SLOT = new ResourceLocation(LimLib.COMMON_ID, "textures/screen/slots/fluid_slot.png");
    @Unique
    private static final ResourceLocation FLUID_SLOT_OVERLAY = new ResourceLocation(LimLib.COMMON_ID, "textures/screen/slots/fluid_slot_overlay.png");

    @Shadow
    private void recalculateQuickCraftRemaining(){}

    @Unique
    private static void lim_lib$renderFluid(Minecraft mc, GuiGraphics gui, FluidStack fluidStack, int depth, int x, int y, int w, int h) {
        if (fluidStack == null || fluidStack.isEmpty() || fluidStack.getFluid() == null) return;
        IClientFluidTypeExtensions renderProperties = IClientFluidTypeExtensions.of(fluidStack.getFluid());
        TextureAtlasSprite sprite = mc.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(renderProperties.getStillTexture(fluidStack));
        if (!MissingTextureAtlasSprite.getLocation().equals(sprite.atlasLocation())) {
            Vector4f color = RenderUtils.expandColor(renderProperties.getTintColor(fluidStack), false);
            gui.blit(x, y, depth, w, h, sprite, color.x, color.y, color.z, color.w); //FIXME: should use a tiling technique instead of stretching the texture
        }
    }

    @Inject(method = "renderSlot", at = @At(value = "HEAD"), cancellable = true)
    private void renderSlot(GuiGraphics gui, Slot slot, CallbackInfo ci) {
        if (slot instanceof TankSlot rs) {
            Minecraft mc = Minecraft.getInstance();
            RenderSystem.enableBlend();
            gui.blit(FLUID_SLOT, slot.x - 1, slot.y - 1, 100, 0, 0, 18, 18, 18, 18);
            FluidStack fluid = rs.getFluid();
            lim_lib$renderFluid(mc, gui, fluid, 150, slot.x, slot.y, 16, 16);
            gui.blit(FLUID_SLOT_OVERLAY, slot.x - 1, slot.y - 1, 200, 0, 0, 18, 18, 18, 18);
            int amountInMB = fluid.getAmount();
            if (amountInMB > 0) {
                String amount = amountInMB >= 1000 ? (amountInMB / 1000) + I18n.get("gui.fluid.bucket") : amountInMB + I18n.get("gui.fluid.milli-bucket");
                PoseStack pose = gui.pose();
                pose.pushPose();
                pose.translate(slot.x, slot.y, 250);
                if (amount.length() > 3) {
                    pose.scale(.5f, .5f, 1f);
                    gui.drawString(mc.font, amount, 31 - mc.font.width(amount), 23, 16777215);
                } else
                    gui.drawString(mc.font, amount, 17 - mc.font.width(amount), 9, 16777215);
                pose.popPose();
            }
            RenderSystem.disableBlend();
            ci.cancel();
        } else if (slot.getItem().getCount() > 64 || slot.getItem().getCount() < 0) {
            Minecraft minecraft = Minecraft.getInstance();
            ItemStack itemstack = slot.getItem();
            boolean drag_merge = false;
            boolean drag_fill = slot == this.clickedSlot && !this.draggingItem.isEmpty() && !this.isSplittingStack;
            ItemStack itemstack1 = this.menu.getCarried();
            String s = null;
            if (slot == this.clickedSlot && !this.draggingItem.isEmpty() && this.isSplittingStack && !itemstack.isEmpty()) {
                itemstack = itemstack.copyWithCount(itemstack.getCount() / 2);
            } else if (this.isQuickCrafting && this.quickCraftSlots.contains(slot) && !itemstack1.isEmpty()) {
                if (this.quickCraftSlots.size() == 1) {
                    return;
                }

                if (AbstractContainerMenu.canItemQuickReplace(slot, itemstack1, true) && this.menu.canDragTo(slot)) {
                    drag_merge = true;
                    int k = Math.min(itemstack1.getMaxStackSize(), slot.getMaxStackSize(itemstack1));
                    int l = slot.getItem().getCount();
                    int i1 = AbstractContainerMenu.getQuickCraftPlaceCount(this.quickCraftSlots, this.quickCraftingType, itemstack1) + l;
                    if (i1 > k) {
                        i1 = k;
                        s = ChatFormatting.YELLOW.toString() + k;
                    }

                    itemstack = itemstack1.copyWithCount(i1);
                } else {
                    this.quickCraftSlots.remove(slot);
                    this.recalculateQuickCraftRemaining();
                }
            }

            gui.pose().pushPose();
            gui.pose().translate(0.0F, 0.0F, 100.0F);
            if (itemstack.isEmpty() && slot.isActive()) {
                Pair<ResourceLocation, ResourceLocation> pair = slot.getNoItemIcon();
                if (pair != null) {
                    TextureAtlasSprite textureatlassprite = minecraft.getTextureAtlas(pair.getFirst()).apply(pair.getSecond());
                    gui.blit(slot.x, slot.y, 0, 16, 16, textureatlassprite);
                    drag_fill = true;
                }
            }

            if (!drag_fill) {
                if (drag_merge) {
                    gui.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, -2130706433);
                }

                int count = itemstack.count;

                if (s == null) {
                    if (count >= 1_000_000_000)
                        s = size_formatter.format(count / 1_000_000_000f) + "b";
                    else if (count >= 1_000_000)
                        s = size_formatter.format(count / 1_000_000f) + "m";
                    else if (count >= 1_000)
                        s = size_formatter.format(count / 1_000f) + "k";
                    else
                        s = (count < 0 ? ChatFormatting.RED : "") + (count != 0 ? Float.toString(count).replaceAll("\\.?0*$", "") : "");
                }

                ItemStack safe = new ItemStack(itemstack.item != null ? itemstack.item : Items.AIR);
                gui.renderItem(safe, slot.x, slot.y, slot.x + slot.y * this.imageWidth);
                gui.renderItemDecorations(minecraft.font, safe, slot.x, slot.y, s);
            }

            gui.pose().popPose();

            ci.cancel();
        }
    }
}
