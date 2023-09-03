package com.limachi.lim_lib.widgetsOld;

/**
 * multi widget
 * text input on the top + validation button
 * scroll bar on the side
 * radio buttons (toggle buttons to edit existing text)
 * button on the right of entries to remove it
 * duplicate protection
 */

/*
public class EditableTextList extends BaseWidget<EditableTextList> {
    public static final ResourceLocation checkboxTexture = new ResourceLocation("minecraft", "textures/gui/container/beacon.png");
    public static final Box2d checkboxYesCutout = new Box2d(90, 222, 16, 16);
    public static final Box2d checkboxNoCutout = new Box2d(113, 222, 16, 16);
    protected ArrayList<String> strings = new ArrayList<>();
    protected final TextFieldWidget textInput;
    protected final ButtonWidget validate;
    protected final TextListSection list;
    protected final ViewPortWidget scrollArea;
    protected final ScrollBarWidget scrollbar;
    protected class TextListSection extends BaseWidget<TextListSection> {
        protected class TextListEntry extends BaseWidget<TextListEntry> {
            protected TextListEntry(@NotNull AnchoredBox box, String value) {
                super(box, new WidgetOptions().canAnimate(true));
                addChild(new TextWidget(AnchoredBox.topLeftDeltaBox(0, 0, box.width() - 16, 16), Component.literal(value)));
                addChild(new ButtonWidget(AnchoredBox.topRightBox(16, 16), Component.empty(), b->{if (b.pressedState) {
                    removeString(strings.indexOf(value));
                }}).addChild(new ImageWidget(AnchoredBox.fill(), checkboxTexture, checkboxNoCutout)));
            }
        }
        protected HashMap<String, TextListEntry> entries = new HashMap<>();
        protected TextListSection(@NotNull AnchoredBox box) { super(box, new WidgetOptions()); }

        protected void addString(String entry) {
            int y = (int)(strings.size() > 0 ? entries.get(strings.get(strings.size() - 1)).currentArea().getY1() + 16 : 0);
            TextListEntry ne = new TextListEntry(AnchoredBox.topLeftDeltaBox(0, y, (int)currentArea().getWidth(), 16), entry);
            entries.put(entry, ne);
            addChild(ne);
            strings.add(entry);
        }

        protected void removeString(int index) {
            for (int i = strings.size() - 1; i > index; --i) {
                TextListEntry e = entries.get(strings.get(i));
                e.animateArea(AnchoredBox.topLeftDeltaBox(0, (int)(e.currentArea().getY1() - 16), (int)currentArea().getWidth(), 16), 1);
            }
            String k = strings.get(index);
            entries.get(k).node.removeNode();
            strings.remove(index);
        }
    }

    public EditableTextList(@Nonnull AnchoredBox box, List<String> strings) {
        super(box, new WidgetOptions());
        textInput = new TextFieldWidget(AnchoredBox.topLeftDeltaBox(0, 0, box.width() - 16, 16)).setOnFinish(w->addString(w.getText()));
        addChild(textInput);
        validate = new ButtonWidget(AnchoredBox.topRightBox(16, 16), Component.empty(), b->textInput.onKeyPressed(GLFW.GLFW_KEY_ENTER, 0, 0));
        validate.addChild(new ImageWidget(AnchoredBox.fill(), checkboxTexture, checkboxYesCutout));
        addChild(validate);
        scrollArea = new ViewPortWidget(AnchoredBox.bottomLeftBox(box.width() - 16, box.height() - 16), new IVec2i(box.width() - 16, box.height() - 16));
        list = new TextListSection(AnchoredBox.fill());
        scrollArea.addChild(list);
        addChild(scrollArea);
        scrollbar = new ScrollBarWidget(AnchoredBox.bottomRightBox(16, box.height() - 16), 0, 0, 0, 0, w->scrollArea.animateArea(AnchoredBox.topLeftDeltaBox(0, w.scroll, (int)scrollArea.currentArea().getWidth(), (int)scrollArea.currentArea().getHeight()), 1, ()->{}));
        addChild(scrollbar);
        for (String e : strings)
            addString(e);
    }

    public void addString(String entry) {
        if (entry != null && !strings.contains(entry))
            list.addString(entry);
    }

    public void removeString(int index) {
        if (index < 0 || index >= strings.size()) return;
        list.removeString(index);
    }

    public void removeString(String entry) {
        if (entry != null)
            for (int i = strings.size() - 1; i >= 0; --i)
                if (strings.get(i).equals(entry))
                    removeString(i);
    }

    public ArrayList<String> getStrings() { return strings; }
}
*/