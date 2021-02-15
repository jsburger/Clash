package com.jsburg.clash.weapons;

import com.jsburg.clash.Clash;
import com.jsburg.clash.registry.AllParticles;
import com.jsburg.clash.registry.AllSounds;
import com.jsburg.clash.weapons.util.AttackHelper;
import com.jsburg.clash.weapons.util.WeaponItem;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeMod;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public class SpearItem extends WeaponItem {

    private static final Vector3d UP = new Vector3d(0, 1, 0);
    private final int stabLengthBonus = 2;
    private final int stabDamageBonus = 2;

    public SpearItem(int attackDamage, float attackSpeed, Item.Properties properties) {
        super(attackDamage, attackSpeed, properties);
    }

    public boolean canPlayerBreakBlockWhileHolding(BlockState state, World worldIn, BlockPos pos, PlayerEntity player) {
        return !player.isCreative();
    }

    @Override
    public void onHit(ItemStack stack, PlayerEntity player, Entity target) {
//        if (AttackHelper.weaponIsCharged(player)) {
//            AttackHelper.playSound(player, AllSounds.WEAPON_SPEAR_STAB.get());
//        }
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    public String formatNumber(float n) {
        String s = String.valueOf(n);
        //Regex magic I got off stackoverflow, gets rid of trailing 0's.
        s = s.contains(".") ? s.replaceAll("0*$", "").replaceAll("\\.$", "") : s;
        return s;
    }

    public ITextComponent getBonusText(String langString, float bonus) {
        StringTextComponent text = new StringTextComponent((bonus > 0) ? " +" : " -");
        text.appendString(formatNumber(bonus) + " ");
        text.append(new TranslationTextComponent(langString));
        text.mergeStyle(bonus > 0 ? TextFormatting.DARK_GREEN : TextFormatting.RED);
        return text;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add((new TranslationTextComponent("item.clash.spear.when_charged")).mergeStyle(TextFormatting.GRAY));
        tooltip.add(getBonusText("item.clash.spear.charge_range_bonus", stabLengthBonus));
    }

    public int getUseDuration(ItemStack stack) {
        return 720000;
    }

    private int getMaxCharge() {
        return 24;
    }

    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
        if (entityLiving instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity)entityLiving;
            int chargeTime = getUseDuration(stack) - timeLeft;
            float chargePercent = Math.min((float)chargeTime/getMaxCharge(), 1);
//            Clash.LOGGER.debug(chargeTime);
            if (chargeTime >= 10) {
                player.addStat(Stats.ITEM_USED.get(this));
                AttackHelper.playSound(player, AllSounds.WEAPON_SPEAR_STAB.get());
                player.swingArm(player.getActiveHand());

                double stabLength = player.getAttributeValue(ForgeMod.REACH_DISTANCE.get()) + stabLengthBonus;
                Vector3d look = player.getLookVec();
                Vector3d endVec = look.scale(stabLength);
                Vector3d eyePos = player.getEyePosition(1.0F);
                Vector3d endPos = eyePos.add(endVec);
                AxisAlignedBB boundingBox = player.getBoundingBox().expand(endVec).grow(1);
                Predicate<Entity> predicate = (e) -> !e.isSpectator() && e.canBeCollidedWith();
                EntityRayTraceResult rayTraceResult = ProjectileHelper.rayTraceEntities(player, eyePos, endPos, boundingBox, predicate, 1000);

                Vector3d side = look.crossProduct(UP).scale(0.75);
                if (player.getPrimaryHand() == HandSide.LEFT ^ player.getActiveHand() == Hand.OFF_HAND) {
                    side = side.scale(-1);
                }
                side = side.add(eyePos).subtract(UP.scale(.2));

                AttackHelper.makeParticle(player.getEntityWorld(), AllParticles.SPEAR_STAB.get(), side.add(look), side.subtractReverse(endPos), 1.4);

                if (rayTraceResult != null) {
                    Entity target = rayTraceResult.getEntity();

                    //Calls Forge's attacking hook, this is definitely a direct attack.
                    boolean canAttack = AttackHelper.fullAttackEntityCheck(player, target);
                    if (canAttack) {
                        ItemStack spear = player.getActiveItemStack();
                        float damage = this.getAttackDamage() + (chargeTime > 20 ? stabDamageBonus : 0);
                        player.resetCooldown();

                        AttackHelper.attackEntity(player, target, damage);
                        AttackHelper.damageTool(player, target, spear);
                        AttackHelper.playSound(player, SoundEvents.ENTITY_PLAYER_ATTACK_STRONG);

                        player.addExhaustion(0.2f);

                    }
                }
            }
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        playerIn.setActiveHand(handIn);
        return ActionResult.resultConsume(stack);
    }

}
