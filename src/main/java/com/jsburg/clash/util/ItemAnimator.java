package com.jsburg.clash.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import java.sql.Array;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@OnlyIn(Dist.CLIENT)
public class ItemAnimator {

    private static final List<AnimationHolder> animationList = new LinkedList<>();
//    private static final HashMap<ItemStack, AnimationHolder> animationMap = new HashMap<>();

    public static void tick() {

        if (animationList.isEmpty()) return;

        List<AnimationHolder> toRemove = new ArrayList<>();
        animationList.forEach((animationHolder) -> {
            boolean doRemove;
            if (animationHolder.isValid()) {
                ItemAnimation animation = animationHolder.animation;
                animation.tick();
                doRemove = animation.isComplete();
            }
            else {
                doRemove = true;
            }
            if (doRemove) {
                toRemove.add(animationHolder);
            }
        });
        toRemove.forEach(animationList::remove);
    }

//    public static ItemAnimation getAnimation(ItemStack stack) {
//        AnimationHolder holder = animationMap.get(stack);
//        if (holder != null) {
//            return holder.animation;
//        }
//        return null;
//    }

//    public static void startAnimation(PlayerEntity player, ItemStack item, Hand hand, ItemAnimation animation) {
//        animationMap.put(item, new AnimationHolder(player, animation, item, hand));
//    }
    public static void startAnimation(PlayerEntity player, ItemStack item, Hand hand, ItemAnimation animation) {
        animationList.add(new AnimationHolder(player, animation, item, hand));
    }

    @CheckForNull
    public static <T extends ItemAnimation> ItemAnimation getAnimation(Class<T> animationType, PlayerEntity player, Hand hand) {
        for (AnimationHolder holder : animationList) {
            ItemAnimation animation = holder.animation;
            if (animationType.isInstance(animation) && holder.boundEntity == player && holder.heldHand == hand) {
                return animation;
            }
        }
        return null;
    }


    //All this code is basically begging for a refactor once I actually figure out how I want to use it.
    //It's currently built with a very vague concept of scalability. Once I find something limiting me I'll fix it.
    private static class AnimationHolder {
        public final PlayerEntity boundEntity;
        public final ItemAnimation animation;
        public final ItemStack boundItem;
        public final Hand heldHand;

        public AnimationHolder(PlayerEntity target, ItemAnimation animation, ItemStack item, Hand itemHand) {
            boundEntity = target;
            this.animation = animation;
            boundItem = item;
            heldHand = itemHand;
        }

        public boolean isValid() {
            boolean isAlive = boundEntity.isAlive();
            boolean stackCheck = ItemStack.areItemsEqual(boundEntity.getHeldItem(heldHand), boundItem);
            return (isAlive && stackCheck);
        }
    }

    public abstract static class ItemAnimation {
        int lifetime;
        int lifetimeMax;

        protected abstract void tick();

        public abstract float getProgress();

        public abstract float getProgress(float partialTicks);

        protected boolean isComplete() {
            return lifetime > lifetimeMax;
        }
    }
    public static class SimpleItemAnimation extends ItemAnimation {

        public SimpleItemAnimation(int duration) {
            lifetime = 0;
            lifetimeMax = duration;
        }

        @Override
        protected void tick() {
            lifetime += 1;
        }

        @Override
        public float getProgress() {
            return ((float)lifetime / lifetimeMax);
        }

        @Override
        public float getProgress(float partialTicks) {
            return Math.min((lifetime + partialTicks) / lifetimeMax, 1);
        }
    }
}
