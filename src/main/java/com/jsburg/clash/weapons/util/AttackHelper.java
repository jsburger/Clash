package com.jsburg.clash.weapons.util;

import com.google.common.collect.Multimap;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public class AttackHelper {
    public static boolean canAttackEntity(Player player, Entity targetEntity) {
        return (targetEntity.isAttackable() && !targetEntity.skipAttackInteraction(player));
    }

    /**
     * Runs Forge's player attack event and the entity's hit check
     */
    public static boolean fullAttackEntityCheck(Player player, Entity targetEntity) {
        if (!net.minecraftforge.common.ForgeHooks.onPlayerAttackTarget(player, targetEntity)) return false;
        return canAttackEntity(player, targetEntity);
    }
    public static boolean weaponIsCharged(Player player) {
        return player.getAttackStrengthScale(0.5F) > 0.9F;
    }
    public static void playSound(Entity player, SoundEvent sound) {
        playSound(player, sound, 1.0F, 1.0F);
    }
    public static void playSound(Entity player, SoundEvent sound, float volume, float pitch) {
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), sound, player.getSoundSource(), volume, pitch);
    }

    /**
     * Calls the item's hit method, which needs a target. Will reduce weapon durability.
     */
    public static void doHitStuff(Player player, Entity target, ItemStack weapon) {
        doHitStuff(player, target, weapon, player.getUsedItemHand());
    }
    //Referenced from net.minecraft.entity.player.PlayerEntity.attackTargetEntityWithCurrentItem
    public static void doHitStuff(Player player, Entity target, ItemStack weapon, InteractionHand hand) {
        if (target instanceof EnderDragonPart) {
            target = ((EnderDragonPart)target).parentMob;
        }
        if (!player.level().isClientSide && target instanceof LivingEntity) {
            ItemStack copy = weapon.copy();
            weapon.hurtEnemy((LivingEntity) target, player);
            if (weapon.getItem() instanceof IHitListener) {
                ((IHitListener) weapon.getItem()).onHit(weapon, (LivingEntity) target, true);
            }
            if (weapon.isEmpty()) {
                net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(player, copy, hand);
                player.setItemInHand(hand, ItemStack.EMPTY);
            }
        }
    }

    public static void damageItem(int damage, ItemStack item, Player player) {
        damageItem(damage, item, player, InteractionHand.MAIN_HAND);
    }
    public static void damageItem(int damage, ItemStack item, Player player, InteractionHand hand) {
        item.hurtAndBreak(damage, player, (p) -> {
            p.broadcastBreakEvent(hand);
            net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(player, item, hand);
        });
    }

    /**
     * Gets player's attack range from their reach distance, accounting for the weird math done during targeting.
     */
    public static double getAttackRange(Player player) {
        return player.getEntityReach();
//        if (player.isCreative()) {
//            //This *should* be 6, but it doesn't act that way. Who knows why.
//            return 5;
//        }
//        //Default value is 5, actual range is 3. Thus, -2.
//        //In theory should be 3, but I like the idea of supporting reach distance bonuses.
//        return player.getAttributeValue(ForgeMod.ENTITY_REACH.get()) - 2;
    }

    /**
     * Hits entity, spawns particles, adds stats.
     */
    public static void attackEntity(Player player, Entity target, float damage) {

        float lastHealth = 0;
        if (target instanceof LivingEntity) {
            lastHealth = ((LivingEntity) target).getHealth();
        }
        target.hurt(player.level().damageSources().playerAttack(player), damage);

        if (target instanceof LivingEntity) {
            float healthDifference = lastHealth - ((LivingEntity) target).getHealth();
            player.awardStat(Stats.DAMAGE_DEALT, Math.round(healthDifference * 10));

            if (player.level() instanceof ServerLevel && healthDifference > 2.0F) {
                int k = (int)(healthDifference * 0.5D);
                ((ServerLevel)player.level())
                        .sendParticles(
                                ParticleTypes.DAMAGE_INDICATOR,
                                target.getX(),
                                target.getY(0.5),
                                target.getZ(),
                                k, 0.1, 0.0, 0.1, 0.2);
            }
        }

    }

    /**
     * Gets bonus from things like sharpness, smite, etc.
     * Returns the additional damage, which is flat, apparently.
     * Multiply it by the attack's charge percent. Add it after multiplying by crits.
     */
    public static float getBonusEnchantmentDamage(ItemStack item, Entity target) {
        float bonus;
        if (target instanceof LivingEntity) {
            bonus = EnchantmentHelper.getDamageBonus(item, ((LivingEntity)target).getMobType());
        } else {
            bonus = EnchantmentHelper.getDamageBonus(item, MobType.UNDEFINED);
        }
        return bonus;
    }

    public static float getCrit(Player player, Entity target, boolean shouldCrit) {
        return getCrit(player, target, shouldCrit, true, true);
    }

    public static float getCrit(Player player, Entity target, boolean shouldCrit, boolean doEffects, boolean playCritSound) {
        net.minecraftforge.event.entity.player.CriticalHitEvent hitResult = net.minecraftforge.common.ForgeHooks.getCriticalHit(player, target, shouldCrit, shouldCrit ? 1.5F : 1.0F);
        if (hitResult == null) {
            return 1;
        }
        if (hitResult.getDamageModifier() > 1 && doEffects) {
            if (playCritSound) playSound(player, SoundEvents.PLAYER_ATTACK_CRIT);
            player.crit(target);
        }
        return hitResult.getDamageModifier();
    }

    //I feel like this shouldn't be necessary. At least I don't have to write the packets myself, I guess.
    public static void makeParticle(Level world, SimpleParticleType particle, Vec3 position, Vec3 motion, double speed) {
        motion = motion.normalize().scale(speed);
        world.addParticle(particle, position.x(), position.y(), position.z(), motion.x(), motion.y(), motion.z());
    }

    public static void makeParticle(Level world, SimpleParticleType particle, Vec3 position, double xSpeed, double ySpeed, double zSpeed) {
        world.addParticle(particle, position.x(), position.y(), position.z(), xSpeed, ySpeed, zSpeed);
    }

    public static void makeParticle(Level world, SimpleParticleType particle, Vec3 position) {
        makeParticle(world, particle, position, Vec3.ZERO, 0);
    }

    public static void makeParticleServer(ServerLevel world, SimpleParticleType particle, Vec3 position, Vec3 motion, double speed) {
        motion = motion.normalize().scale(speed);
        world.sendParticles(particle, position.x(), position.y(), position.z(), 0, motion.x(), motion.y(), motion.z(), speed);
    }

    public static void makeParticleServer(Level world, RegistryObject<SimpleParticleType> particle, Vec3 position) {
        if (world.isClientSide()) return;
        ((ServerLevel) world).sendParticles(particle.get(), position.x(), position.y(), position.z(), 0, 0, 0, 0, 0);
    }
    public static void makeParticleServer(Level world, RegistryObject<SimpleParticleType> particle, Vec3 position, double xSpeed, double ySpeed, double zSpeed) {
        if (world.isClientSide()) return;
        ((ServerLevel) world).sendParticles(particle.get(), position.x(), position.y(), position.z(), 0, xSpeed, ySpeed, zSpeed, 1);
    }

    public static double getAttackDamage(ItemStack item, Player player, EquipmentSlot equipmentSlot) {
        Multimap<Attribute, AttributeModifier> multimap = item.getAttributeModifiers(equipmentSlot);

        for(Map.Entry<Attribute, AttributeModifier> entry : multimap.entries()) {
            if (entry.getKey().equals(Attributes.ATTACK_DAMAGE)) {
                return entry.getValue().getAmount() + player.getAttributeBaseValue(Attributes.ATTACK_DAMAGE);
            }
        }

        return player.getAttributeBaseValue(Attributes.ATTACK_DAMAGE);
    }

    /**
     * Does the normal entity raytrace but instead of adding .3 to size, it uses entity motion to make hitting easier.
     * Edited from vanilla code.
     */
    @Nullable
    public static EntityHitResult rayTraceWithMotion(Level worldIn, Entity projectile, Vec3 startVec, Vec3 endVec, AABB boundingBox, Predicate<Entity> filter) {
        double d0 = Double.MAX_VALUE;
        Entity entity = null;
        Vec3 hitVec = null;

        for(Entity target : worldIn.getEntities(projectile, boundingBox, filter)) {
            Vec3 targetMotion = target.getDeltaMovement().scale(.5f);
            AABB entityBox = target.getBoundingBox().expandTowards(targetMotion).expandTowards(targetMotion.reverse());
            Optional<Vec3> optional = entityBox.clip(startVec, endVec);

            if (optional.isPresent()) {
                double d1 = startVec.distanceToSqr(optional.get());
                if (d1 < d0) {
                    hitVec = optional.get();
                    entity = target;
                    d0 = d1;
                }
            }
        }

        return entity == null ? null : new EntityHitResult(entity, hitVec);
    }

    public static Vec3 getEntityPosition(Entity target) {
        return new Vec3(target.getX(), target.getY(), target.getZ());
    }

}
