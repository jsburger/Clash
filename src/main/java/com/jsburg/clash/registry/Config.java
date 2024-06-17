package com.jsburg.clash.registry;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {

    public static ModConfigSpec SERVER_CONFIG;
    public static final ModConfigSpec.BooleanValue SWEPT_AXE_HEAD_DROP;


    public static ModConfigSpec CLIENT_CONFIG;
    public static final ModConfigSpec.DoubleValue SCREENSHAKE_MULTIPLIER;

    static {

        ModConfigSpec.Builder serverBuilder = new ModConfigSpec.Builder();

        SWEPT_AXE_HEAD_DROP = serverBuilder.comment("Enables the Swept Axe to drop its head upon breaking.")
                .define("enableSweptAxeHeadDrop", true);

        SERVER_CONFIG = serverBuilder.build();

        ModConfigSpec.Builder clientBuilder = new ModConfigSpec.Builder();

        SCREENSHAKE_MULTIPLIER = clientBuilder.comment("Multiplier for screen shake effects. Set to 0 to disable, 1 to full strength.")
                .defineInRange("screenshakeMultiplier", 1d, 0, 2);

        CLIENT_CONFIG = clientBuilder.build();
    }

}
