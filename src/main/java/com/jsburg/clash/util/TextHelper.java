package com.jsburg.clash.util;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class TextHelper {

    public static String formatNumber(float n) {
        String s = String.valueOf(n);
        //Regex magic I got off stackoverflow, gets rid of trailing 0's.
        s = s.contains(".") ? s.replaceAll("0*$", "").replaceAll("\\.$", "") : s;
        return s;
    }

    public static ITextComponent getBonusText(String langString, float bonus) {
        StringTextComponent text = new StringTextComponent((bonus > 0) ? " +" : " -");
        text.appendString(formatNumber(bonus) + " ");
        text.appendSibling(new TranslationTextComponent(langString));
        text.mergeStyle(bonus > 0 ? TextFormatting.DARK_GREEN : TextFormatting.RED);
        return text;
    }

}
