package com.jsburg.clash.util;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ViewportEvent;

import java.util.Random;

public class ScreenShaker {

    //TODO: Elden ring screenshake. Alternatively, try making the screenshake start faster

    private static final Random RANDOM = new Random();
    private static double ShakeX = 0;
    private static double ShakeY = 0;
    private static double LastX = 0;
    private static double LastY = 0;
    private static int    ShakeTime = 0;
    private static double Intensity = 0;
//    private static int    XFlip = -1;

    private static final Minecraft mc = Minecraft.getInstance();

    public static void tick() {
        if (ShakeTime >= 1) {
            ShakeTime -= 1;
            LastX = ShakeX;
            LastY = ShakeY;
            ShakeX = uRandom(.2) * Intensity /* * XFlip*/;
            ShakeY = uRandom(.4) * Intensity;
            Intensity *= .8;
//            Intensity = Math.max(Intensity, .2);
            //I know that uRandom flips at random, the idea is that this avoids directly alternating the offset
            //while allowing it to vary some more.
              //Fun Jsburg fact! This is not how statistics work!
//            XFlip *= -1;
        } else {
            ShakeX = 0;
            ShakeY = 0;
        }
    }

    private static double uRandom(double n) {
        double flip = (RANDOM.nextInt(2) - .5) * 2;
        return (1 - Math.pow(RANDOM.nextDouble(), 2)) * n * flip;
    }

    public static void setScreenShake(int time, double intensity) {
        ShakeTime = time;
        Intensity = intensity;
    }

    public static void setScreenShake(int time) {
        setScreenShake(time, .8);
    }

    public static void applyScreenShake(double partialTicks, ViewportEvent.ComputeCameraAngles event) {
        if (ShakeTime > 0) {
            //TODO: Try 1 - partialTicks
            double lerp = partialTicks;
            double x = LastX + (ShakeX - LastX) * lerp;
            double y = LastY + (ShakeY - LastY) * lerp;
            event.setPitch((float) (event.getPitch() + y * 5));
            event.setRoll((float) (event.getRoll() + x * 5));
//            renderInfo.movePosition(0, y * mc.gameSettings.screenEffectScale, x * mc.gameSettings.screenEffectScale);
        }

    }

}
