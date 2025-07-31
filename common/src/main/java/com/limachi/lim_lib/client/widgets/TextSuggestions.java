package com.limachi.lim_lib.client.widgets;

import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.commands.SharedSuggestionProvider;

import java.util.ArrayList;
import java.util.Collection;

@Environment(EnvType.CLIENT)
public class TextSuggestions extends CommandSuggestions {

    protected ArrayList<String> values = new ArrayList<>();
    protected boolean below = false;

    public TextSuggestions(TextEditor parent, int visibleSuggestions, Collection<String> suggestions) {
        super(Minecraft.getInstance(), Minecraft.getInstance().screen, parent, Minecraft.getInstance().font, false, false, 0, visibleSuggestions, false, Integer.MIN_VALUE);
        setAllowSuggestions(true);
        parent.setSuggestions(this);
        updateSuggestions(suggestions);
        parent.setResponder(this::responder);
    }

    protected void responder(String input) {
        updateCommandInfo();
    }

    public void updateSuggestions(Collection<String> suggestions) {
        values.clear();
        values.addAll(suggestions);
        updateCommandInfo();
    }

    @Override
    public void updateCommandInfo() {
        String string = this.input.getValue();
        if (this.currentParse != null && !this.currentParse.getReader().getString().equals(string)) {
            this.currentParse = null;
        }
        if (!this.keepSuggestions) {
            this.input.setSuggestion((String)null);
            this.suggestions = null;
        }
        this.commandUsage.clear();
        int i = this.input.getCursorPosition();
        String string2 = string.substring(0, i);
        int j = getLastWordIndex(string2);
        this.pendingSuggestions = SharedSuggestionProvider.suggest(values, new SuggestionsBuilder(string2, j));
        showSuggestions(true);
    }

    public void setBelow(boolean below) {
        this.below = below;
    }

    @Override
    public void showSuggestions(boolean bl) {
        super.showSuggestions(bl);
        if (suggestions != null)
            suggestions.rect.setY(below ? input.getY() + input.getHeight() : input.getY() - suggestions.rect.getHeight());
    }
}
