package com.jsburg.clash.util;

import java.util.function.Function;

public class Easing {

    public static Ease Quart = new Ease() {
        @Override
        public float in(float x) { return x * x * x * x; }

        @Override
        public float out(float x) { return 1 - pow(1 - x, 4); }

        @Override
        public float inOut(float x) {
            return x < 0.5 ? 8 * x * x * x * x : 1 - pow(-2 * x + 2, 4) / 2;
        }
    };

    public static Ease Circ = new Ease() {
        @Override
        public float in(float x) { return 1 - sqrt(1 - pow(x, 2)); }

        @Override
        public float out(float x) { return sqrt(1 - pow(x - 1, 2)); }

        @Override
        public float inOut(float x) {
            return x < 0.5
                    ? (1 - sqrt(1 - pow(2 * x, 2))) / 2
                    : (sqrt(1 - pow(-2 * x + 2, 2)) + 1) / 2;
        }
    };

    public static Ease Back = new Ease() {
        private static final float c1 = 1.70158f;
        private static final float c2 = c1 * 1.525f;
        private static final float c3 = c1 + 1;
        @Override
        public float in(float x) { return c3 * x * x * x - c1 * x * x; }

        @Override
        public float out(float x) { return 1 + c3 * pow(x - 1, 3) + c1 * pow(x - 1, 2); }

        @Override
        public float inOut(float x) {
            return x < 0.5
                    ? (pow(2 * x, 2) * ((c2 + 1) * 2 * x - c2)) / 2
                    : (pow(2 * x - 2, 2) * ((c2 + 1) * (x * 2 - 2) + c2) + 2) / 2;
        }
    };

    public abstract static class Ease {
        abstract public float in(float x);
        abstract public float out(float x);
        abstract public float inOut(float x);
    }

    public static float combine(Function<Float, Float> a, Function<Float, Float> b, float t, float blendRatio) {
        return combine(a.apply(t), b.apply(t), blendRatio);
    }
    public static float combine(float a, float b, float blendRatio) {
        return (a * blendRatio) + b * (1 - blendRatio);
    }

    private static float pow(float a, float b) {
        return (float) Math.pow(a, b);
    }
    private static float sqrt(float a) {
        return (float) Math.sqrt(a);
    }
}
