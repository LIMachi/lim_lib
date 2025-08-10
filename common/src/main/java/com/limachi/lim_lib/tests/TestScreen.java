package com.limachi.lim_lib.tests;

import com.limachi.lim_lib.client.screens.SimpleScreen;
import com.limachi.lim_lib.client.widgets.TextEditor;
import com.limachi.lim_lib.client.widgets.TextSuggestions;
import com.limachi.lim_lib.client.widgets.WidgetEditor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Util;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;

@Environment(EnvType.CLIENT)
public class TestScreen extends SimpleScreen {
    public TestScreen(Component title, Screen screen) { super(title, screen); }

    TextEditor text;
    TextSuggestions suggestions;
    ArrayList<String> test = Util.make(new ArrayList<>(3), l->{
        l.add("test1");
        l.add("test2");
        l.add("test3");
        l.add("test4");
    });

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(new WidgetEditor());
        addRenderableWidget(text = TextEditor.builder(100, 200, text).suggestions(test).build());
    }
}
