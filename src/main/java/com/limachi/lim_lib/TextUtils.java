package com.limachi.lim_lib;

import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class TextUtils {
    private static final int PRETTY_TAG_MAX_WIDTH = 32;
    private static final int PRETTY_TAG_MARGIN = 2;

    private static MutableComponent lst(String v) { return new TextComponent(v).withStyle(ChatFormatting.YELLOW); }
    private static MutableComponent obj(String v) { return new TextComponent(v).withStyle(ChatFormatting.GOLD); }
    private static MutableComponent key(String v) { return new TextComponent(v).withStyle(ChatFormatting.DARK_AQUA); }
    private static MutableComponent str(String v) { return new TextComponent(v).withStyle(ChatFormatting.GREEN); }
    private static MutableComponent def(String v) { return new TextComponent(v).withStyle(ChatFormatting.AQUA); }

    private static Pair<Integer, MutableComponent> plst(int depth, String v) { return new Pair<>(depth, lst(v)); }
    private static Pair<Integer, MutableComponent> pobj(int depth, String v) { return new Pair<>(depth, obj(v)); }
    private static Pair<Integer, MutableComponent> pkey(int depth, String v) { return new Pair<>(depth, key(v)); }
    private static Pair<Integer, MutableComponent> pstr(int depth, String v) { return new Pair<>(depth, str(v)); }
    private static Pair<Integer, MutableComponent> pdef(int depth, String v) { return new Pair<>(depth, def(v)); }

    @SuppressWarnings("unchecked")
    private static ArrayList<Pair<Integer, MutableComponent>> prettyTagInternal(Tag nbt, int depth, boolean tryUuid) {
        ArrayList<Pair<Integer, MutableComponent>> out = new ArrayList<>();
        int width = PRETTY_TAG_MAX_WIDTH - depth * PRETTY_TAG_MARGIN;
        if (width <= 0) return out;
        if (nbt instanceof CollectionTag) { //list style
            CollectionTag<Tag> l = (CollectionTag<Tag>)nbt;
            boolean mightBeArrayOfUUID = l.size() > 0 && l.stream().allMatch(n -> n.getType() == IntArrayTag.TYPE && ((IntArrayTag) n).getAsIntArray().length == 4);
            if (tryUuid && l.size() == 4 && nbt instanceof IntArrayTag)
                out.add(pstr(depth, NbtUtils.loadUUID(nbt).toString()));
            else if (l.size() == 0)
                out.add(plst(depth, "[]"));
            else if (l.size() == 1) {
                ArrayList<Pair<Integer, MutableComponent>> t = prettyTagInternal(l.get(0), depth + 1, mightBeArrayOfUUID);
                if (t.size() == 1)
                    out.add(new Pair<>(depth, lst("[ ").append(t.get(0).getSecond()).append(lst(" ]"))));
                else {
                    out.add(new Pair<>(depth, lst("[ ").append(t.get(0).getSecond())));
                    for (int i = 1; i < t.size() - 1; ++i)
                        out.add(t.get(i));
                    out.add(new Pair<>(depth, t.get(t.size() - 1).getSecond().append(lst(" ]"))));
                }
            } else {
                out.add(plst(depth, "["));
                for (int i = 0; i < l.size(); ++i) {
                    ArrayList<Pair<Integer, MutableComponent>> t = prettyTagInternal(l.get(i), depth + 1, mightBeArrayOfUUID);
                    for (int j = 0; j < t.size() - 1; ++j)
                        out.add(t.get(j));
                    if (i < l.size() - 1)
                        out.add(new Pair<>(depth + 1, t.get(t.size() - 1).getSecond().append(lst(","))));
                    else
                        out.add(t.get(t.size() - 1));
                }
                out.add(plst(depth, "]"));
            }
        } else if (nbt instanceof CompoundTag c) { //object style
            if (c.isEmpty())
                out.add(pobj(depth, "{}"));
            else if (c.getAllKeys().size() == 1) {
                String k = (String)c.getAllKeys().toArray()[0];
                ArrayList<Pair<Integer, MutableComponent>> t = prettyTagInternal(c.get(k), depth + 1, k.matches("[uU]{0,2}[iI][dD]|[Uu]nique[iI][dD]"));
                if (t.size() == 1)
                    out.add(new Pair<>(depth, obj("{ ").append(key(k)).append(obj(" : ")).append(t.get(0).getSecond()).append(obj(" }"))));
                else {
                    out.add(new Pair<>(depth, obj("{ ").append(key(k)).append(obj(" : ")).append(t.get(0).getSecond())));
                    for (int i = 1; i < t.size() - 1; ++i)
                        out.add(t.get(i));
                    out.add(new Pair<>(depth, t.get(t.size() - 1).getSecond().append(obj(" }"))));
                }
            } else {
                out.add(pobj(depth, "{"));
                String[] ks = c.getAllKeys().toArray(new String[]{});
                for (int i = 0; i < ks.length; ++i) {
                    ArrayList<Pair<Integer, MutableComponent>> t = prettyTagInternal(c.get(ks[i]), depth + 1, ks[i].matches("[uU]{0,2}[iI][dD]|[Uu]nique[iI][dD]"));
                    if (t.size() > 1) {
                        out.add(new Pair<>(depth + 1, key(ks[i]).append(obj(" : ")).append(t.get(0).getSecond())));
                        for (int j = 1; j < t.size() - 1; ++j)
                            out.add(t.get(j));
                        if (i < c.size() - 1)
                            out.add(new Pair<>(depth + 1, t.get(t.size() - 1).getSecond().append(obj(","))));
                        else
                            out.add(t.get(t.size() - 1));
                    } else {
                        if (i < c.size() - 1)
                            out.add(new Pair<>(depth + 1, key(ks[i]).append(obj(" : ")).append(t.get(0).getSecond()).append(obj(","))));
                        else
                            out.add(new Pair<>(depth + 1, key(ks[i]).append(obj(" : ")).append(t.get(0).getSecond())));
                    }
                }
                out.add(pobj(depth, "}"));
            }
        } else
            out.add(nbt instanceof StringTag ? pstr(depth, nbt.toString()) : pdef(depth, nbt.toString()));
        return out;
    }

    private static TextComponent margin(int depth) { return new TextComponent("                                                                ".substring(0, depth * PRETTY_TAG_MARGIN)); }

    public static List<Component> prettyTag(Tag nbt) {
        ArrayList<Pair<Integer, MutableComponent>> in = prettyTagInternal(nbt, 0, false);
        List<Component> out = new ArrayList<>();
        if (in.size() == 1)
            out.add(in.get(0).getSecond());
        if (in.size() < 2)
            return out;
        for (int i = 0; i < in.size() - 1; ++i) {
            String c1 = in.get(i).getSecond().getString();
            int d = in.get(i).getFirst();
            String c2 = in.get(i + 1).getSecond().getString();
            if ((c1.startsWith("}") || c1.startsWith("]")) && (c2.startsWith("}") || c2.startsWith("]") || c2.endsWith("{") || c2.endsWith("["))) {
                out.add(margin(d).append(in.get(i).getSecond()).append(" ").append(in.get(i + 1).getSecond()));
                i += 1;
            } else if (c1.endsWith("[") && (c2.endsWith("{") || c2.endsWith("["))) {
                out.add(margin(d).append(in.get(i).getSecond()).append(" ").append(in.get(i + 1).getSecond()));
                i += 1;
            } else if (i == in.size() - 2) {
                out.add(margin(d).append(in.get(i).getSecond()));
                out.add(margin(in.get(i + 1).getFirst()).append(in.get(i + 1).getSecond()));
            } else {
                out.add(margin(d).append(in.get(i).getSecond()));
            }
        }
        return out;
    }

    public static String prettyTagString(Tag nbt) {
        StringBuilder out = new StringBuilder();
        List<Component> list = prettyTag(nbt);
        for (Component c : list)
            out.append(c.getString()).append('\n');
        return out.deleteCharAt(out.length() - 1).toString();
    }

    public static TextComponent prettyTagText(Tag nbt) {
        TextComponent out = new TextComponent("");
        List<Component> list = prettyTag(nbt);
        for (int i = 0; i < list.size() - 1; ++i)
            out.append(list.get(i)).append("\n");
        return (TextComponent)out.append(list.get(list.size() - 1));
    }
}
