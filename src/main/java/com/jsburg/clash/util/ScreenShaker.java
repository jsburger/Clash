package com.jsburg.clash.util;

import com.jsburg.clash.Clash;
import net.minecraft.client.renderer.ActiveRenderInfo;

import java.util.Random;

public class ScreenShaker {

    private static final Random RANDOM = new Random();
    private static double ShakeX = 0;
    private static double ShakeY = 0;
    private static double LastX = 0;
    private static double LastY = 0;
    private static int    ShakeTime = 0;
    private static double Intensity = 0;
    private static int    XFlip = -1;

    public static void tick() {
        if (ShakeTime >= 1) {
            ShakeTime -= 1;
            LastX = ShakeX;
            LastY = ShakeY;
            ShakeX = uRandom(.4) * Intensity * XFlip;
            ShakeY = uRandom(.1) * Intensity;
            Intensity *= .5;
//            Intensity = Math.max(Intensity, .2);
            //I know that uRandom flips at random, the idea is that this avoids directly alternating the offset
            //while allowing it to vary some more.
            XFlip *= -1;
        } else {
            ShakeX = 0;
            ShakeY = 0;
        }
    }

    private static double uRandom(double n) {
        double flip = (RANDOM.nextInt(1) - .5) * 2;
        return (1 - Math.pow(RANDOM.nextDouble(), 2)) * n * flip;
    }

    public static void setScreenShake(int time, double intensity) {
        ShakeTime = time;
        Intensity = intensity;
    }

    public static void setScreenShake(int time) {
        setScreenShake(time, .8);
    }

    public static void applyScreenShake(ActiveRenderInfo renderInfo, double partialTicks) {
        if (ShakeTime > 0) {
            double lerp = partialTicks;
            double x = LastX + (ShakeX - LastX) * lerp;
            double y = LastY + (ShakeY - LastY) * lerp;
            renderInfo.movePosition(0, y, x);
        }

    }

}
