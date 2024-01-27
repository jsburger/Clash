package com.jsburg.clash.weapons;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.jsburg.clash.enchantments.spear.DashEnchantment;
import com.jsburg.clash.enchantments.spear.FlurryEnchantment;
import com.jsburg.clash.registry.AllEnchantments;
import com.jsburg.clash.registry.AllParticles;
import com.jsburg.clash.registry.AllSounds;
import com.jsburg.clash.util.TextHelper;
import com.jsburg.clash.weapons.util.AttackHelper;
import com.jsburg.clash.weapons.util.ISpearAnimation;
import com.jsburg.clash.weapons.util.IThirdPersonArmController;
import com.jsburg.clash.weapons.util.WeaponItem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class SpearItem extends WeaponItem implements ISpearAnimation, IThirdPersonArmController {

    private static final Vec3 UP = new Vec3(0, 1, 0);
    private static final float stabLengthBonus = 2.5f;
    private static final float sweetSpotSize = 2.5f;
    private final List<Multimap<Attribute, AttributeModifier>> flurryAttributes;

    public SpearItem(float attackDamage, float attackSpeed, Item.Properties properties) {
        super(attackDamage, attackSpeed, properties);
        attackDamage -= 1;
        attackSpeed = -(4 - attackSpeed);

        List<Multimap<Attribute, AttributeModifier>> multimaps = new ArrayList<>();
        //Could probably afford to include an extra level just for Quark tomes, hence MAX_LEVEL + 1
        for (int i = 1; i <= FlurryEnchantment.MAX_LEVEL + 1; i++) {
            ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
            builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", attackDamage, AttributeModifier.Operation.ADDITION));
            builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", attackSpeed + (FlurryEnchantment.SPEED_PER_LEVEL * i), AttributeModifier.Operation.ADDITION));
            multimaps.add(builder.build());
        }
        flurryAttributes = multimaps;

    }



    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add((Component.translatable("item.clash.spear.when_charged")).withStyle(ChatFormatting.GRAY));
        tooltip.add(TextHelper.getBonusText("item.clash.spear.charge_range_bonus", stabLengthBonus));
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        int flurry = Math.min(EnchantmentHelper.getItemEnchantmentLevel(AllEnchantments.FLURRY.get(), stack), FlurryEnchantment.MAX_LEVEL + 1);
        if (slot == EquipmentSlot.MAINHAND && flurry > 0) {
            return flurryAttributes.get(flurry - 1);
        }
        return super.getAttributeModifiers(slot, stack);
    }

    @Override
    public List<Enchantment> vanillaEnchantments() {
        return Arrays.asList(Enchantments.MOB_LOOTING, Enchantments.IMPALING);
    }

    public int getUseDuration(ItemStack stack) {
        return 720000;
    }

    public int getMaxCharge(ItemStack stack) {
        if (EnchantmentHelper.getItemEnchantmentLevel(AllEnchantments.JAB.get(), stack) > 0) return 3;
        return 20;
    }

    public int getMinCharge(ItemStack stack) {
        if (EnchantmentHelper.getItemEnchantmentLevel(AllEnchantments.JAB.get(), stack) > 0) return 0;
        return 10 - EnchantmentHelper.getItemEnchantmentLevel(AllEnchantments.FLURRY.get(), stack);
    }

    protected void onStabHit(ItemStack stack, Player player, LivingEntity target, float chargePercent) {
        Vec3 look = player.getLookAngle();
        target.knockback(chargePercent / 3, -look.x(), -look.z());
    }

    protected boolean canStabCrit(ItemStack stack) {
        return true;
    }

    public void releaseUsing(ItemStack stack, Level worldIn, LivingEntity entityLiving, int timeLeft) {
        if (entityLiving instanceof Player) {
            Player player = (Player)entityLiving;
            int chargeTime = getUseDuration(stack) - timeLeft;
            ItemStack spear = player.getUseItem();

            float chargePercent = Math.min((float)chargeTime/getMaxCharge(spear), 1);

            boolean hasJab = EnchantmentHelper.getItemEnchantmentLevel(AllEnchantments.JAB.get(), stack) > 0;

            if (chargeTime >= getMinCharge(stack)) {
                player.awardStat(Stats.ITEM_USED.get(this));
                player.swing(player.getUsedItemHand());

                if (hasJab) {
                    ItemCooldowns tracker = player.getCooldowns();
                    tracker.removeCooldown(this);
                    tracker.addCooldown(this, 40);
                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 1));
                }

                double stabLength = AttackHelper.getAttackRange(player) + stabLengthBonus;
                Vec3 look = player.getLookAngle();
                Vec3 endVec = look.scale(stabLength);
                Vec3 eyePos = player.getEyePosition(1.0F);
                Vec3 endPos = eyePos.add(endVec);
                AABB boundingBox = new AABB(eyePos.x, eyePos.y, eyePos.z, endPos.x, endPos.y, endPos.z).inflate(1);
                Predicate<Entity> predicate = (e) -> !e.isSpectator() && e.isPickable();
                EntityHitResult rayTraceResult = AttackHelper.rayTraceWithMotion(worldIn, player, eyePos, endPos, boundingBox, predicate);

                //Get vector to the side of the player
                Vec3 side = look.cross(UP).scale(0.75);
                //bro i love xor
                //Flips side based on where the spear actually is on screen
                if (player.getMainArm() == HumanoidArm.LEFT ^ player.getUsedItemHand() == InteractionHand.OFF_HAND) {
                    side = side.scale(-1);
                }
                side = side.add(eyePos).subtract(UP.scale(.2));

                if (rayTraceResult != null) {
                    Entity target = rayTraceResult.getEntity();
                    Vec3 hitLocation = rayTraceResult.getLocation();

                    BlockHitResult blockRayTraceResult = player.level.clip(new ClipContext(eyePos, hitLocation, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null));
                    if (blockRayTraceResult.getType() == HitResult.Type.MISS) {

                        //Server side logic time
                        if (!worldIn.isClientSide) {
                            //Target dragon properly
                            if (target instanceof EnderDragonPart) {
                                target = ((EnderDragonPart)target).parentMob;
                            }
                            //Calls Forge's attacking hook, as this is definitely a direct attack.
                            boolean canAttack = AttackHelper.fullAttackEntityCheck(player, target);
                            if (canAttack) {
                                float damage = (float) AttackHelper.getAttackDamage(spear, player, EquipmentSlot.MAINHAND);
                                if (canStabCrit(stack) && chargeTime > getMaxCharge(stack) - 4) damage *= AttackHelper.getCrit(player, target, true);
                                player.resetAttackStrengthTicker();

                                //Sweet Spot check, works by comparing the distance from the furthest point of the attack to the point of contact
                                if (EnchantmentHelper.getItemEnchantmentLevel(AllEnchantments.SWEET_SPOT.get(), spear) > 0) {
                                    double distance = endPos.distanceTo(hitLocation);
                                    if (distance <= sweetSpotSize) {
                                        damage *= 2;
                                        AttackHelper.playSound(player, AllSounds.WEAPON_SPEAR_MEGA_CRIT.get(), 2f, 1.0f);
                                        for (int i = 0; i <= 5; i++) {
                                            RandomSource rand = worldIn.getRandom();
                                            Vec3 motion = new Vec3(rand.nextDouble() - .5, rand.nextDouble() - .5, rand.nextDouble() - .5);
                                            AttackHelper.makeParticleServer((ServerLevel) worldIn, AllParticles.SPEAR_CRIT.get(), hitLocation, motion, 1.5f);
                                        }
                                    }
                                }
                                damage += AttackHelper.getBonusEnchantmentDamage(spear, target);

                                AttackHelper.attackEntity(player, target, damage);
                                AttackHelper.doHitStuff(player, target, spear);
                                AttackHelper.playSound(player, SoundEvents.PLAYER_ATTACK_STRONG);

                                this.onStabHit(stack, player, (LivingEntity) target, chargePercent);

                            }
                            player.causeFoodExhaustion(0.2f);
                        }
                    }
                }

                AttackHelper.playSound(player, AllSounds.WEAPON_SPEAR_STAB.get());
                AttackHelper.makeParticle(player.getCommandSenderWorld(), AllParticles.SPEAR_STAB.get(), side.add(look), side.vectorTo(endPos), 1.4);

            }
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);

        DashEnchantment.tryAgilityDash(worldIn, playerIn, stack);

        playerIn.startUsingItem(handIn);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public AnimType hasThirdPersonAnim(Player player, ItemStack stack, boolean isActive, InteractionHand hand) {
        return AnimType.ifTrue(isActive);
    }

    @Override
    public <T extends LivingEntity> void doThirdPersonAnim(Player player, HumanoidModel<T> model, ItemStack itemStack, float partialTicks, boolean leftHanded, boolean isActive, InteractionHand hand) {
        ModelPart spearArm = leftHanded ? model.leftArm : model.rightArm;
        ModelPart otherArm = leftHanded ? model.rightArm : model.leftArm;
        int sideFlip = leftHanded ? -1 : 1;

        spearArm.xRot *= .4f;
        spearArm.xRot -= .4f;
        spearArm.yRot -= .3f * sideFlip;
        spearArm.zRot += .3f * sideFlip;
        spearArm.x += 1f * sideFlip;
        spearArm.y += 4f;
        spearArm.z += 2f;

        otherArm.xRot *= .4f;
        otherArm.xRot -= .4f;
        otherArm.zRot += .9f * sideFlip;
        otherArm.x -= 2f * sideFlip;
        otherArm.y -= 2f;
        otherArm.z -= 3f;

        model.body.yRot += .3f * sideFlip;
    }
}
