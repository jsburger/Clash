package com.jsburg.clash.weapons;

import com.jsburg.clash.entity.GreatbladeSlashEntity;
import com.jsburg.clash.registry.AllEffects;
import com.jsburg.clash.registry.AllParticles;
import com.jsburg.clash.util.ItemAnimator;
import com.jsburg.clash.util.MiscHelper;
import com.jsburg.clash.util.TextHelper;
import com.jsburg.clash.weapons.util.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

import static com.jsburg.clash.registry.AllEnchantments.*;

public class GreatbladeItem extends WeaponItem implements IThirdPersonArmController, IThirdPersonRenderHook, IHitListener, IActiveResetListener {

    private final float baseDamage;
    public GreatbladeItem(float attackDamage, float attackSpeed, Properties properties) {
        super(attackDamage, attackSpeed, properties);
        baseDamage = attackDamage;
    }

    public float getSlashDamage(ItemStack stack) {
        float base = hasExecutioner(stack) ? 10 : 5;
        if (hasThrum(stack)) {
            base += 2 * thrumLevel(stack);
        }
        return base;
    }

    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add((new TranslatableComponent("item.clash.spear.when_charged")).withStyle(ChatFormatting.GRAY));
        tooltip.add(TextHelper.getBonusText("item.clash.greatblade.bonus_damage", getSlashDamage(stack)));
    }

    private static boolean hasSailing(ItemStack stack) {
        return EnchantmentHelper.getItemEnchantmentLevel(SAILING.get(), stack) > 0;
    }
    private static int crushingLevel(ItemStack stack) {
        return EnchantmentHelper.getItemEnchantmentLevel(CRUSHING.get(), stack);
    }
    public static boolean hasExecutioner(ItemStack stack) {
        return EnchantmentHelper.getItemEnchantmentLevel(EXECUTIONER.get(), stack) > 0;
    }
    private static int thrumLevel(ItemStack stack) {
        return EnchantmentHelper.getItemEnchantmentLevel(THRUM.get(), stack);
    }

    public int getUseDuration(ItemStack stack) {
//        if (hasSailing(stack)) return 30;
        return 720000;
    }

    public int getMaxCharge() {
        return 15;
    }

    public static int swingTimeMax() {
        return 7;
    }

    @Override
    public boolean canContinueUsing(ItemStack oldStack, ItemStack newStack) {
        return super.canContinueUsing(oldStack, newStack);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    @Override
    public void onHit(ItemStack stack, LivingEntity target, boolean isCharged) {
        if (isCharged) {
            if (thrumLevel(stack) > 0) setThrum(stack, true);
            int crushing = crushingLevel(stack);
            target.addEffect(new MobEffectInstance(AllEffects.STAGGERED.get(), (int) (25 * (1 + ((float)crushing/2))), crushing, false, true));
        }
    }

    private static final String THRUM_KEY = "has_thrum";
    private static void setThrum(ItemStack stack, boolean value) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putBoolean(THRUM_KEY, value);
    }
    public static boolean hasThrum(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(THRUM_KEY)) {
            return tag.getBoolean(THRUM_KEY);
        }
        return false;
    }

    @Override
    public void onUsingTick(ItemStack stack, LivingEntity player, int count) {
        super.onUsingTick(stack, player, count);
        if (hasSailing(stack)) {
            float n = getUseDuration(stack) - count;
            float speed = 1;
            if (player instanceof LocalPlayer) {
                LocalPlayer client = (LocalPlayer) player;
                Input input = client.input;

                if (input.up) speed += .5;
                if (input.down) speed -= .5;

            }
            Vec3 accel = MiscHelper.extractHorizontal(player.getViewVector(1)).scale(speed/((n + 2) /3));
            if (n < 10) {
                player.setDeltaMovement(accel.x, player.getDeltaMovement().y, accel.z);
//            player.addVelocity(accel.x, 0, accel.z);
                if (n % 2 == 0)
                    AttackHelper.makeParticleServer(player.level, AllParticles.SAILING_TRAIL, player.position().add(0, 1, 0));
            }
            if (n > 15) {
                player.releaseUsingItem();
            }
        }
        else {
            float n = getUseDuration(stack) - count;
            float threshold = 5;
            float diff = (getMaxCharge() - n);
            if (diff > 0 && diff <= threshold) {
                Vec3 accel = player.getViewVector(1);
                player.setDeltaMovement(player.getDeltaMovement().add(accel.scale((1/threshold)/2)));
            }
            if (n > (getMaxCharge())) {
                player.releaseUsingItem();
            }
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level worldIn, LivingEntity entityLiving, int timeLeft) {
        if (entityLiving instanceof Player) {
            Player player = (Player) entityLiving;
            int chargeTime = getUseDuration(stack) - timeLeft;

            ItemStack sword = player.getUseItem();

            if (chargeTime >= getMaxCharge()) {
                GreatbladeSlashEntity slash = new GreatbladeSlashEntity(worldIn, sword, player.position().add(0, .0, 0), player, hasExecutioner(stack));
                slash.setDamage(baseDamage + getSlashDamage(stack));
                if (hasThrum(stack)) {
                    slash.applyThrum();
                    setThrum(stack, false);
                }
                slash.spriteFlip = (player.getMainArm() == HumanoidArm.LEFT ^ player.getUsedItemHand() == InteractionHand.OFF_HAND) ? 1 : 0;
                slash.setDeltaMovement(player.getViewVector(1).scale(2));
                slash.applyWhirlwind(EnchantmentHelper.getItemEnchantmentLevel(WHIRLING.get(), stack));
                worldIn.addFreshEntity(slash);

                AttackHelper.playSound(player, SoundEvents.PLAYER_ATTACK_SWEEP, 1, .5f);

                if (worldIn.isClientSide) {
                    ItemAnimator.startAnimation(player, sword, player.getUsedItemHand(), new GreatbladeAnimation());
                    ItemAnimator.startAnimation(player, sword, player.getUsedItemHand(), new GreatbladeThirdPersonAnimation());
                }

                Vec3 look = player.getViewVector(1).scale(1);
                player.push(look.x/2, 0, look.z/2);

                player.resetAttackStrengthTicker();
                player.getCooldowns().addCooldown(stack.getItem(), 20);
                InteractionHand otherHand = player.getUsedItemHand() == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
                ItemStack otherStack = player.getItemInHand(otherHand);
                if (!otherStack.isEmpty()) player.getCooldowns().addCooldown(otherStack.getItem(), 10);
                player.causeFoodExhaustion(.5f);
            }
        }
    }

    public void onSlashHit(ItemStack stack, LivingEntity target, Entity user) {
        AttackHelper.playSound(user, SoundEvents.PLAYER_ATTACK_STRONG);
        if (user instanceof Player) {
            Player player = (Player) user;
            //AttackHelper.doHitStuff(player, target, stack, );

        }
    }

    @Override
    public void onHandReset(ItemStack stack, LivingEntity user) {
        if (user instanceof Player && user.getTicksUsingItem() < getMaxCharge()) {
            Player player = (Player) user;
            if (hasSailing(stack)) {
                player.setDeltaMovement(player.getDeltaMovement().scale(.5));
                player.resetAttackStrengthTicker();
                player.getCooldowns().addCooldown(stack.getItem(), 30);
                player.causeFoodExhaustion(.5f);
            }
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);

        playerIn.startUsingItem(handIn);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public AnimType hasThirdPersonAnim(Player player, ItemStack stack, boolean isActive, InteractionHand hand) {
        if (isActive) return AnimType.OVERWRITES;
        ItemAnimator.ItemAnimation animation = ItemAnimator.getAnimation(GreatbladeThirdPersonAnimation.class, player, hand);
        if (animation != null) return AnimType.OVERWRITES;
        return AnimType.FALSE;
    }

    @Override
    public <T extends LivingEntity> void doThirdPersonAnim(Player player, HumanoidModel<T> model, ItemStack itemStack, float partialTicks, boolean leftHanded, boolean isActive, InteractionHand hand) {
        ModelPart swordArm = leftHanded ? model.leftArm : model.rightArm;
        ModelPart otherArm = leftHanded ? model.rightArm : model.leftArm;
        int sideFlip = leftHanded ? -1 : 1;
        ItemAnimator.ItemAnimation animation = ItemAnimator.getAnimation(GreatbladeThirdPersonAnimation.class, player, hand);
        float swingProgress = 0;
        if (animation != null) swingProgress = animation.getProgress(partialTicks);

        int useCount = player.getUseItemRemainingTicks();
        int useDuration = getUseDuration(itemStack);
        int useTime = useDuration - useCount;
        float chargePercent = Math.min((useTime + partialTicks) / getMaxCharge(), 1);

        float chargeLerp = Math.min(chargePercent, 1 - swingProgress);
        //Charging pose, acts as the base for the swing too
        swordArm.xRot *= .4f * chargeLerp;
        swordArm.xRot -= .4f * chargeLerp;
        swordArm.yRot -= .3f * sideFlip * chargeLerp;
        swordArm.zRot += .3f * sideFlip * chargeLerp;
        swordArm.x += 1f * sideFlip * chargeLerp;
        swordArm.y += 4f * chargeLerp;
        swordArm.z += 2f * chargeLerp;

        otherArm.xRot *= .4f * chargeLerp;
        otherArm.xRot -= .4f * chargeLerp;
        otherArm.zRot += .9f * sideFlip * chargeLerp;
        otherArm.x -= 2f * sideFlip * chargeLerp;
        otherArm.y -= 2f * chargeLerp;
        otherArm.z -= 3f * chargeLerp;

        model.body.yRot += .3f * sideFlip * chargeLerp;

        if (hasSailing(itemStack) && useTime < 10) {
            model.leftLeg.xRot = 0;
            model.rightLeg.xRot = .3f;
        }

        //Swing animation
        if (animation != null) {
            float pi = (float) Math.PI;
            float swingPercent = (float)Math.sin(Math.pow(animation.getProgress(partialTicks), .6) * pi);
            //float realSwingPercent = animation.getProgress(partialTicks);

            swordArm.xRot -= .5 * pi * swingPercent;
            swordArm.zRot += .4 * pi * swingPercent * sideFlip;
            swordArm.yRot += .1 * pi * swingPercent * sideFlip;
            swordArm.x += 2.5f * sideFlip * swingPercent;
            swordArm.y -= 6f * swingPercent;
            swordArm.z -= 3f * swingPercent;

            otherArm.xRot -= .4 * pi * swingPercent;
            otherArm.zRot += .4 * pi * swingPercent * sideFlip;
            otherArm.yRot += .1 * pi * swingPercent;
            otherArm.x += 1f * sideFlip * swingPercent;
            otherArm.y += 3f * swingPercent;
            otherArm.z += 4.5f * swingPercent;

            model.body.yRot -= .2f * pi * swingPercent * sideFlip;
        }

    }

    @Override
    public <T extends LivingEntity, M extends EntityModel<T> & ArmedModel> boolean onThirdPersonRender(M model, LivingEntity entity, ItemStack itemStack, ItemTransforms.TransformType transformType, HumanoidArm side, PoseStack poseStack, MultiBufferSource renderBuffer, int light) {

        if (entity instanceof Player) {
            Player player = (Player) entity;
            InteractionHand swordHand = MiscHelper.getHandFromSide(player, side);
            ItemAnimator.ItemAnimation animation = ItemAnimator.getAnimation(GreatbladeThirdPersonAnimation.class, player, swordHand);
            if (animation != null) {
                float progress = animation.getProgress(Minecraft.getInstance().getFrameTime());
                float pi = (float) Math.PI;
                float sineProgress = (float)Math.sin(progress * pi);

                poseStack.mulPose(Vector3f.XP.rotationDegrees(sineProgress * 180));
                poseStack.translate(0, sineProgress * .25, sineProgress * -.25);
            }
        }
        return false;
    }

    public static class GreatbladeAnimation extends ItemAnimator.SimpleItemAnimation {
        public GreatbladeAnimation() {
            super((int) (swingTimeMax()));
        }
    }
    public static class GreatbladeThirdPersonAnimation extends ItemAnimator.SimpleItemAnimation {
        public GreatbladeThirdPersonAnimation() {
            super(swingTimeMax() + 4);
        }
    }
}
