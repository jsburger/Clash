package com.jsburg.clash.weapons;

import com.jsburg.clash.entity.GreatbladeSlashEntity;
import com.jsburg.clash.registry.AllEffects;
import com.jsburg.clash.registry.AllParticles;
import com.jsburg.clash.util.ItemAnimator;
import com.jsburg.clash.util.MiscHelper;
import com.jsburg.clash.util.TextHelper;
import com.jsburg.clash.weapons.util.*;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.IHasArm;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

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

    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add((new TranslationTextComponent("item.clash.spear.when_charged")).mergeStyle(TextFormatting.GRAY));
        tooltip.add(TextHelper.getBonusText("item.clash.greatblade.bonus_damage", getSlashDamage(stack)));
    }

    private static boolean hasSailing(ItemStack stack) {
        return EnchantmentHelper.getEnchantmentLevel(SAILING.get(), stack) > 0;
    }
    private static int crushingLevel(ItemStack stack) {
        return EnchantmentHelper.getEnchantmentLevel(CRUSHING.get(), stack);
    }
    public static boolean hasExecutioner(ItemStack stack) {
        return EnchantmentHelper.getEnchantmentLevel(EXECUTIONER.get(), stack) > 0;
    }
    private static int thrumLevel(ItemStack stack) {
        return EnchantmentHelper.getEnchantmentLevel(THRUM.get(), stack);
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
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    @Override
    public void onHit(ItemStack stack, LivingEntity target, boolean isCharged) {
        if (isCharged) {
            if (thrumLevel(stack) > 0) setThrum(stack, true);
            int crushing = crushingLevel(stack);
            target.addPotionEffect(new EffectInstance(AllEffects.STAGGERED.get(), (int) (25 * (1 + ((float)crushing/2))), crushing, false, true));
        }
    }

    private static final String THRUM_KEY = "has_thrum";
    private static void setThrum(ItemStack stack, boolean value) {
        CompoundNBT tag = stack.getOrCreateTag();
        tag.putBoolean(THRUM_KEY, value);
    }
    public static boolean hasThrum(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
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
            if (player instanceof ClientPlayerEntity) {
                ClientPlayerEntity client = (ClientPlayerEntity) player;
                MovementInput input = client.movementInput;

                if (input.forwardKeyDown) speed += .5;
                if (input.backKeyDown) speed -= .5;

            }
            Vector3d accel = MiscHelper.extractHorizontal(player.getLook(1)).scale(speed/((n + 2) /3));
            if (n < 10) {
                player.setMotion(accel.x, player.getMotion().y, accel.z);
//            player.addVelocity(accel.x, 0, accel.z);
                if (n % 2 == 0)
                    AttackHelper.makeParticleServer(player.world, AllParticles.SAILING_TRAIL, player.getPositionVec().add(0, 1, 0));
            }
            if (n > 15) {
                player.stopActiveHand();
            }
        }
        else {
            float n = getUseDuration(stack) - count;
            float threshold = 5;
            float diff = (getMaxCharge() - n);
            if (diff > 0 && diff <= threshold) {
                Vector3d accel = player.getLook(1);
                player.setMotion(player.getMotion().add(accel.scale((1/threshold)/2)));
            }
            if (n > (getMaxCharge())) {
                player.stopActiveHand();
            }
        }
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
        if (entityLiving instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entityLiving;
            int chargeTime = getUseDuration(stack) - timeLeft;

            ItemStack sword = player.getActiveItemStack();

            if (chargeTime >= getMaxCharge()) {
                GreatbladeSlashEntity slash = new GreatbladeSlashEntity(worldIn, sword, player.getPositionVec().add(0, .5, 0), player, hasExecutioner(stack));
                slash.setDamage(baseDamage + getSlashDamage(stack));
                if (hasThrum(stack)) {
                    slash.applyThrum();
                    setThrum(stack, false);
                }
                slash.spriteFlip = (player.getPrimaryHand() == HandSide.LEFT ^ player.getActiveHand() == Hand.OFF_HAND) ? 1 : 0;
                slash.setMotion(player.getLook(1).scale(2));
                slash.applyWhirlwind(EnchantmentHelper.getEnchantmentLevel(WHIRLING.get(), stack));
                worldIn.addEntity(slash);

                AttackHelper.playSound(player, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, 1, .5f);

                if (worldIn.isRemote) {
                    ItemAnimator.startAnimation(player, sword, player.getActiveHand(), new GreatbladeAnimation());
                    ItemAnimator.startAnimation(player, sword, player.getActiveHand(), new GreatbladeThirdPersonAnimation());
                }

                Vector3d look = player.getLook(1).scale(1);
                player.addVelocity(look.x/2, 0, look.z/2);

                player.resetCooldown();
                player.getCooldownTracker().setCooldown(stack.getItem(), 30);
                player.addExhaustion(.5f);
            }
        }
    }

    public void onSlashHit(ItemStack stack, LivingEntity target, Entity user) {
        AttackHelper.playSound(user, SoundEvents.ENTITY_PLAYER_ATTACK_STRONG);
        if (user instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) user;
            //AttackHelper.doHitStuff(player, target, stack, );

        }
    }

    @Override
    public void onHandReset(ItemStack stack, LivingEntity user) {
        if (user instanceof PlayerEntity && user.getItemInUseMaxCount() < getMaxCharge()) {
            PlayerEntity player = (PlayerEntity) user;
            if (hasSailing(stack)) {
                player.setMotion(player.getMotion().scale(.5));
                player.resetCooldown();
                player.getCooldownTracker().setCooldown(stack.getItem(), 30);
                player.addExhaustion(.5f);
            }
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);

        playerIn.setActiveHand(handIn);
        return ActionResult.resultConsume(stack);
    }

    @Override
    public AnimType hasThirdPersonAnim(PlayerEntity player, ItemStack stack, boolean isActive, Hand hand) {
        if (isActive) return AnimType.OVERWRITES;
        ItemAnimator.ItemAnimation animation = ItemAnimator.getAnimation(GreatbladeThirdPersonAnimation.class, player, hand);
        if (animation != null) return AnimType.OVERWRITES;
        return AnimType.FALSE;
    }

    @Override
    public <T extends LivingEntity> void doThirdPersonAnim(PlayerEntity player, BipedModel<T> model, ItemStack itemStack, float partialTicks, boolean leftHanded, boolean isActive, Hand hand) {
        ModelRenderer swordArm = leftHanded ? model.bipedLeftArm : model.bipedRightArm;
        ModelRenderer otherArm = leftHanded ? model.bipedRightArm : model.bipedLeftArm;
        int sideFlip = leftHanded ? -1 : 1;
        ItemAnimator.ItemAnimation animation = ItemAnimator.getAnimation(GreatbladeThirdPersonAnimation.class, player, hand);
        float swingProgress = 0;
        if (animation != null) swingProgress = animation.getProgress(partialTicks);

        int useCount = player.getItemInUseCount();
        int useDuration = getUseDuration(itemStack);
        int useTime = useDuration - useCount;
        float chargePercent = Math.min((useTime + partialTicks) / getMaxCharge(), 1);

        float chargeLerp = Math.min(chargePercent, 1 - swingProgress);
        //Charging pose, acts as the base for the swing too
        swordArm.rotateAngleX *= .4f * chargeLerp;
        swordArm.rotateAngleX -= .4f * chargeLerp;
        swordArm.rotateAngleY -= .3f * sideFlip * chargeLerp;
        swordArm.rotateAngleZ += .3f * sideFlip * chargeLerp;
        swordArm.rotationPointX += 1f * sideFlip * chargeLerp;
        swordArm.rotationPointY += 4f * chargeLerp;
        swordArm.rotationPointZ += 2f * chargeLerp;

        otherArm.rotateAngleX *= .4f * chargeLerp;
        otherArm.rotateAngleX -= .4f * chargeLerp;
        otherArm.rotateAngleZ += .9f * sideFlip * chargeLerp;
        otherArm.rotationPointX -= 2f * sideFlip * chargeLerp;
        otherArm.rotationPointY -= 2f * chargeLerp;
        otherArm.rotationPointZ -= 3f * chargeLerp;

        model.bipedBody.rotateAngleY += .3f * sideFlip * chargeLerp;

        if (hasSailing(itemStack) && useTime < 10) {
            model.bipedLeftLeg.rotateAngleX = 0;
            model.bipedRightLeg.rotateAngleX = .3f;
        }

        //Swing animation
        if (animation != null) {
            float pi = (float) Math.PI;
            float swingPercent = (float)Math.sin(Math.pow(animation.getProgress(partialTicks), .6) * pi);
            //float realSwingPercent = animation.getProgress(partialTicks);

            swordArm.rotateAngleX -= .5 * pi * swingPercent;
            swordArm.rotateAngleZ += .4 * pi * swingPercent * sideFlip;
            swordArm.rotateAngleY += .1 * pi * swingPercent * sideFlip;
            swordArm.rotationPointX += 2.5f * sideFlip * swingPercent;
            swordArm.rotationPointY -= 6f * swingPercent;
            swordArm.rotationPointZ -= 3f * swingPercent;

            otherArm.rotateAngleX -= .4 * pi * swingPercent;
            otherArm.rotateAngleZ += .4 * pi * swingPercent * sideFlip;
            otherArm.rotateAngleY += .1 * pi * swingPercent;
            otherArm.rotationPointX += 1f * sideFlip * swingPercent;
            otherArm.rotationPointY += 3f * swingPercent;
            otherArm.rotationPointZ += 4.5f * swingPercent;

            model.bipedBody.rotateAngleY -= .2f * pi * swingPercent * sideFlip;
        }

    }

    @Override
    public <T extends LivingEntity, M extends EntityModel<T> & IHasArm> boolean onThirdPersonRender(M model, LivingEntity entity, ItemStack itemStack, ItemCameraTransforms.TransformType transformType, HandSide side, MatrixStack poseStack, IRenderTypeBuffer renderBuffer, int light) {

        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            Hand swordHand = MiscHelper.getHandFromSide(player, side);
            ItemAnimator.ItemAnimation animation = ItemAnimator.getAnimation(GreatbladeThirdPersonAnimation.class, player, swordHand);
            if (animation != null) {
                float progress = animation.getProgress(Minecraft.getInstance().getRenderPartialTicks());
                float pi = (float) Math.PI;
                float sineProgress = (float)Math.sin(progress * pi);

                poseStack.rotate(Vector3f.XP.rotationDegrees(sineProgress * 180));
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
