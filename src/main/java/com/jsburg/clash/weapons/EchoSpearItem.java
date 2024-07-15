package com.jsburg.clash.weapons;

import com.jsburg.clash.registry.AllParticles;
import com.jsburg.clash.registry.AllSounds;
import com.jsburg.clash.weapons.util.AttackHelper;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class EchoSpearItem extends SpearItem {

    public EchoSpearItem(float attackDamage, float attackSpeed, Properties properties) {
        super(attackDamage, attackSpeed, properties);
        stabLengthBonus = 20f;
    }

    @Override
    protected void doStabEffect(Player player, Level worldIn, Vec3 endPos, Vec3 sidePos) {
        AttackHelper.playSound(player, AllSounds.WEAPON_SPEAR_STAB.get());
        AttackHelper.playSound(player, SoundEvents.WARDEN_SONIC_BOOM);
        //AttackHelper.makeParticle(player.getCommandSenderWorld(), AllParticles.SPEAR_STAB.get(), side.add(look), side.vectorTo(endPos), 1.4);
        if (!worldIn.isClientSide) {
            var dif = sidePos.vectorTo(endPos).normalize();
            for(int i = 1; i < Mth.floor(stabLengthBonus); ++i) {
                Vec3 pos = sidePos.add(dif.scale(i));
                ((ServerLevel)worldIn).sendParticles(ParticleTypes.SONIC_BOOM, pos.x, pos.y, pos.z, 1, 0.0, 0.0, 0.0, 0.0);
            }
        }
        var look = player.getLookAngle().reverse();
        look = look.multiply(new Vec3(1, .5, 1));
        player.addDeltaMovement(look.scale(3));
        player.getCooldowns().addCooldown(this, 40);
    }

    @Override
    protected void onStabHit(ItemStack stack, Player player, LivingEntity target, float chargePercent) {
        super.onStabHit(stack, player, target, chargePercent);
        Vec3 look = player.getLookAngle();
        target.knockback(chargePercent * 2, -look.x(), -look.z());
    }
}
