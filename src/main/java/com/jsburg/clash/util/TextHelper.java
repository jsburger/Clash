package com.jsburg.clash.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class TextHelper {

    public static String formatNumber(float n) {
        String s = String.valueOf(n);
        //Regex magic I got off stackoverflow, gets rid of trailing 0's.
        s = s.contains(".") ? s.replaceAll("0*$", "").replaceAll("\\.$", "") : s;
        return s;
    }

    public static Component getBonusText(String langString, float bonus) {
        MutableComponent text = Component.literal((bonus > 0) ? " +" : " -");
        text.append(formatNumber(bonus) + " ");
        text.append(Component.translatable(langString));
        text.withStyle(bonus > 0 ? ChatFormatting.DARK_GREEN : ChatFormatting.RED);
        return text;
    }

}
