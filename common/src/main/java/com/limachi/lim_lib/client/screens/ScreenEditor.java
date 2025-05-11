package com.limachi.lim_lib.client.screens;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class ScreenEditor extends SimpleScreen {
    @Override
    public boolean charTyped(char character, int modifiers) {
        if ((modifiers & GLFW.GLFW_MOD_ALT) != 0 && switch (character) {
            case '?' -> true; //show help in personal player chat
            case 'p' -> true; //pop up to edit from where the selected asset is loaded (usually texture or translation key)
            case 's' -> true; //select the screen itself (allow manipulating it's properties like position, scale, etc)
            default -> false;
        })
            return true;
        return super.charTyped(character, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false; //required to update the player chat (maybe could just allow a single tick when needing to print the help)
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double startX, double startY, int button, double mouseX, double mouseY) {
        return super.mouseDragged(startX, startY, button, mouseX, mouseY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }
}
