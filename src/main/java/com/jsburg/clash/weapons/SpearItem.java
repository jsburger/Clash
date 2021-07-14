package com.jsburg.clash.weapons;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.jsburg.clash.Clash;
import com.jsburg.clash.enchantments.spear.FlurryEnchantment;
import com.jsburg.clash.registry.AllEnchantments;
import com.jsburg.clash.registry.AllParticles;
import com.jsburg.clash.registry.AllSounds;
import com.jsburg.clash.util.TextHelper;
import com.jsburg.clash.weapons.util.AttackHelper;
import com.jsburg.clash.weapons.util.IPoseItem;
import com.jsburg.clash.weapons.util.WeaponItem;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeMod;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

public class SpearItem extends WeaponItem implements IPoseItem {

    private static final Vector3d UP = new Vector3d(0, 1, 0);
    private static final float stabLengthBonus = 2.5f;
    private static final float sweetSpotSize = 4f;
    private final List<Multimap<Attribute, AttributeModifier>> flurryAttributes;

    public SpearItem(int attackDamage, float attackSpeed, Item.Properties properties) {
        super(attackDamage, attackSpeed, properties);
        attackDamage -= 1;
        attackSpeed = -(4 - attackSpeed);

        List<Multimap<Attribute, AttributeModifier>> multimaps = new LinkedList<>();
        //Could probably afford to include an extra level just for Quark tomes, hence MAX_LEVEL + 1
        for (int i = 1; i <= FlurryEnchantment.MAX_LEVEL + 1; i++) {
            ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
            builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", attackDamage, AttributeModifier.Operation.ADDITION));
            builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", attackSpeed + (FlurryEnchantment.SPEED_PER_LEVEL * i), AttributeModifier.Operation.ADDITION));
            multimaps.add(builder.build());
        }
        flurryAttributes = multimaps;

    }

    @Override
    public boolean canPlayerBreakBlockWhileHolding(BlockState state, World worldIn, BlockPos pos, PlayerEntity player) {
        return !player.isCreative();
    }

    @Override
    public int getItemEnchantability() {
        return 1;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add((new TranslationTextComponent("item.clash.spear.when_charged")).mergeStyle(TextFormatting.GRAY));
        tooltip.add(TextHelper.getBonusText("item.clash.spear.charge_range_bonus", stabLengthBonus));
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
        int flurry = Math.min(EnchantmentHelper.getEnchantmentLevel(AllEnchantments.FLURRY.get(), stack), FlurryEnchantment.MAX_LEVEL + 1);
        if (slot == EquipmentSlotType.MAINHAND && flurry > 0) {
            return flurryAttributes.get(flurry - 1);
        }
        return super.getAttributeModifiers(slot, stack);
    }

    @Override
    public List<Enchantment> vanillaEnchantments() {
        return Arrays.asList(Enchantments.LOOTING, Enchantments.IMPALING);
    }

    public int getUseDuration(ItemStack stack) {
        return 720000;
    }

    public int getMaxCharge(ItemStack stack) {
        if (EnchantmentHelper.getEnchantmentLevel(AllEnchantments.JAB.get(), stack) > 0) return 3;
        return 20;
    }

    public int getMinCharge(ItemStack stack) {
        if (EnchantmentHelper.getEnchantmentLevel(AllEnchantments.JAB.get(), stack) > 0) return 0;
        return 10 - EnchantmentHelper.getEnchantmentLevel(AllEnchantments.FLURRY.get(), stack);
    }

    protected void onStabHit(ItemStack stack, PlayerEntity player, LivingEntity target, float chargePercent) {
        Vector3d look = player.getLookVec();
        target.applyKnockback(chargePercent / 3, -look.getX(), -look.getZ());
    }

    protected boolean canStabCrit(ItemStack stack) {
        return true;
    }

    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
        if (entityLiving instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity)entityLiving;
            int chargeTime = getUseDuration(stack) - timeLeft;
            ItemStack spear = player.getActiveItemStack();

            float chargePercent = Math.min((float)chargeTime/getMaxCharge(spear), 1);

            int thrustLevel = EnchantmentHelper.getEnchantmentLevel(AllEnchantments.LUNGE.get(), spear);
            int flurryLevel = EnchantmentHelper.getEnchantmentLevel(AllEnchantments.FLURRY.get(), stack);
            boolean hasJab = EnchantmentHelper.getEnchantmentLevel(AllEnchantments.JAB.get(), stack) > 0;
            boolean doThrust = thrustLevel > 0 && !(player.isSneaking()) && chargeTime > (5 - flurryLevel/2) && player.isOnGround() && !player.isSwimming();

            if (chargeTime >= getMinCharge(stack)) {
                player.addStat(Stats.ITEM_USED.get(this));
                player.swingArm(player.getActiveHand());

                if (hasJab) {
                    CooldownTracker tracker = player.getCooldownTracker();
                    tracker.removeCooldown(this);
                    tracker.setCooldown(this, 40);
                    player.addPotionEffect(new EffectInstance(Effects.SLOWNESS, 40, 1));
                }

                double stabLength = player.getAttributeValue(ForgeMod.REACH_DISTANCE.get()) + stabLengthBonus;
                Vector3d look = player.getLookVec();
                Vector3d endVec = look.scale(stabLength);
                Vector3d eyePos = player.getEyePosition(1.0F);
                Vector3d endPos = eyePos.add(endVec);
                AxisAlignedBB boundingBox = new AxisAlignedBB(eyePos.x, eyePos.y, eyePos.z, endPos.x, endPos.y, endPos.z).grow(1);
                Predicate<Entity> predicate = (e) -> !e.isSpectator() && e.canBeCollidedWith();
                EntityRayTraceResult rayTraceResult = AttackHelper.rayTraceWithMotion(worldIn, player, eyePos, endPos, boundingBox, predicate);

                //Get vector to the side of the player
                Vector3d side = look.crossProduct(UP).scale(0.75);
                //bro i fucking love xor
                //Flips side based on where the spear actually is on screen
                if (player.getPrimaryHand() == HandSide.LEFT ^ player.getActiveHand() == Hand.OFF_HAND) {
                    side = side.scale(-1);
                }
                side = side.add(eyePos).subtract(UP.scale(.2));

                if (rayTraceResult != null) {
                    Entity target = rayTraceResult.getEntity();
//                    Vector3d targetMotion = target.getMotion().scale(.5f);
//                    AxisAlignedBB entityBox = target.getBoundingBox().expand(targetMotion).expand(targetMotion.inverse());
//                    Optional<Vector3d> cast = entityBox.rayTrace(eyePos, endPos);
//                    Vector3d hitLocation = cast.orElseGet(rayTraceResult::getHitVec);
                    Vector3d hitLocation = rayTraceResult.getHitVec();

                    BlockRayTraceResult blockRayTraceResult = player.world.rayTraceBlocks(new RayTraceContext(eyePos, hitLocation, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, null));
                    if (blockRayTraceResult.getType() == RayTraceResult.Type.MISS) {
                        doThrust = false;

                        //Server side logic time
                        if (!worldIn.isRemote) {
                            //Calls Forge's attacking hook, this is definitely a direct attack.
                            boolean canAttack = AttackHelper.fullAttackEntityCheck(player, target);
                            if (canAttack) {
                                float damage = (float) AttackHelper.getAttackDamage(spear, player, EquipmentSlotType.MAINHAND);
                                if (canStabCrit(stack) && chargeTime > getMaxCharge(stack) - 4) damage *= AttackHelper.getCrit(player, target, true);
                                player.resetCooldown();

                                //Sweet Spot check, works by comparing the distance from the furthest point of the attack to the point of contact
                                if (EnchantmentHelper.getEnchantmentLevel(AllEnchantments.SWEET_SPOT.get(), spear) > 0) {
                                    double distance = endPos.squareDistanceTo(hitLocation);
                                    if (distance <= sweetSpotSize) {
                                        damage *= 2;
                                        AttackHelper.playSound(player, AllSounds.WEAPON_SPEAR_MEGA_CRIT.get(), 2f, 1.0f);
                                        for (int i = 0; i <= 5; i++) {
                                            Random rand = worldIn.getRandom();
                                            Vector3d motion = new Vector3d(rand.nextDouble() - .5, rand.nextDouble() - .5, rand.nextDouble() - .5);
                                            AttackHelper.makeParticleServer((ServerWorld) worldIn, AllParticles.SPEAR_CRIT.get(), hitLocation, motion, 1.5f);
                                        }
                                    }
                                }
                                damage += AttackHelper.getBonusEnchantmentDamage(spear, target);

                                this.onStabHit(stack, player, (LivingEntity) target, chargePercent);

                                AttackHelper.attackEntity(player, target, damage);
                                AttackHelper.doHitStuff(player, target, spear);
                                AttackHelper.playSound(player, SoundEvents.ENTITY_PLAYER_ATTACK_STRONG);

                            }
                            player.addExhaustion(0.2f);
                        }
                    }
                }

                if (!doThrust) {
                    AttackHelper.playSound(player, AllSounds.WEAPON_SPEAR_STAB.get());
                    AttackHelper.makeParticle(player.getEntityWorld(), AllParticles.SPEAR_STAB.get(), side.add(look), side.subtractReverse(endPos), 1.4);
                }

            }
            if (doThrust) {
                AttackHelper.playSound(player, AllSounds.WEAPON_SPEAR_WHOOSH.get(), 0.3f, 1.0f);

                double boostedPercentage = Math.min(1, chargePercent * 1.4);
                Vector3d dir = player.getLookVec().scale(Math.sqrt(thrustLevel) * 2 * boostedPercentage);
                player.addVelocity(dir.x, dir.y / 2 + 0.2, dir.z);
                AttackHelper.damageItem(1, spear, player, player.getActiveHand());

                player.addExhaustion(0.1f);
            }
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);

        if (EnchantmentHelper.getEnchantmentLevel(AllEnchantments.AGILITY.get(), stack) > 0) {
            if (!(playerIn.isSneaking() || playerIn.isElytraFlying() || playerIn.isSwimming())) {
                double ySpeed = playerIn.isOnGround() ? .25 : -.4;
                final float dashSpeed = playerIn.isOnGround() ? .7f : .6f;

                Vector3d motion = playerIn.getMotion();
                Vector3d dir = new Vector3d(motion.getX(), 0, motion.getZ());

                if (Math.abs(dir.x) <= 0.05 && Math.abs(dir.z) <= 0.05) {
                    Vector3d look = playerIn.getLookVec();
                    dir = new Vector3d(-look.getX(), 0, -look.getZ());
                }
                dir = dir.normalize().scale(dashSpeed);

                playerIn.addVelocity(dir.x, ySpeed, dir.z);
                Vector3d dashDir = new Vector3d(dir.x, ySpeed/2, dir.z);
                Random rand = worldIn.getRandom();
                int o = 7 + rand.nextInt(4);
                for (int i = 0; i <= o; i++){
                    Vector3d d = dashDir.rotateYaw((rand.nextFloat() * .5f) - .25f).scale(rand.nextFloat() * .5 + .25);
                    worldIn.addParticle(AllParticles.DAST_DUST.get(),
                            playerIn.getPosX() + d.getX()/2, playerIn.getPosY() + d.getY() + .1, playerIn.getPosZ() + d.getZ()/2,
                            d.getX(), d.getY() + rand.nextFloat() * .1 - .05, d.getZ());
                }

                playerIn.addExhaustion(0.1f);
                playerIn.getCooldownTracker().setCooldown(this, 15);
            }
        }

        playerIn.setActiveHand(handIn);
        return ActionResult.resultConsume(stack);
    }

    @Override
    public boolean hasPose(PlayerEntity player, ItemStack stack, boolean isActive) {
        return isActive;
    }

    @Override
    public <T extends LivingEntity> void doPose(PlayerEntity player, BipedModel<T> model, ItemStack itemStack, boolean leftHanded, boolean isActive) {
        ModelRenderer spearArm = leftHanded ? model.bipedLeftArm : model.bipedRightArm;
        ModelRenderer otherArm = leftHanded ? model.bipedRightArm : model.bipedLeftArm;
        int sideFlip = leftHanded ? -1 : 1;

        spearArm.rotateAngleX *= .4f;
        spearArm.rotateAngleX -= .4f;
        spearArm.rotateAngleY -= .3f * sideFlip;
        spearArm.rotateAngleZ += .3f * sideFlip;
        spearArm.rotationPointX += 1f * sideFlip;
        spearArm.rotationPointY += 4f;
        spearArm.rotationPointZ += 2f;

        otherArm.rotateAngleX *= .4f;
        otherArm.rotateAngleX -= .4f;
        otherArm.rotateAngleZ += .9f * sideFlip;
        otherArm.rotationPointX -= 2f * sideFlip;
        otherArm.rotationPointY -= 2f;
        otherArm.rotationPointZ -= 3f;

        model.bipedBody.rotateAngleY += .3f * sideFlip;
    }
}
