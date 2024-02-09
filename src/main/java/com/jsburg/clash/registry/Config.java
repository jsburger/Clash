package com.jsburg.clash.registry;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {

    public static ForgeConfigSpec SERVER_CONFIG;
    public static final ForgeConfigSpec.BooleanValue SWEPT_AXE_HEAD_DROP;


    public static ForgeConfigSpec CLIENT_CONFIG;
    public static final ForgeConfigSpec.DoubleValue SCREENSHAKE_MULTIPLIER;

    static {

        ForgeConfigSpec.Builder serverBuilder = new ForgeConfigSpec.Builder();

        SWEPT_AXE_HEAD_DROP = serverBuilder.comment("Enables the Swept Axe to drop its head upon breaking.")
                .define("enableSweptAxeHeadDrop", true);

        SERVER_CONFIG = serverBuilder.build();

        ForgeConfigSpec.Builder clientBuilder = new ForgeConfigSpec.Builder();

        SCREENSHAKE_MULTIPLIER = clientBuilder.comment("Multiplier for screen shake effects. Set to 0 to disable, 1 to full strength.")
                .defineInRange("screenshakeMultiplier", 1d, 0, 2);

        CLIENT_CONFIG = clientBuilder.build();
    }

}
