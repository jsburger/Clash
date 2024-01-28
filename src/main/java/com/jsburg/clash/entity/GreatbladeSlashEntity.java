package com.jsburg.clash.entity;

import com.google.common.collect.Lists;
import com.jsburg.clash.registry.AllParticles;
import com.jsburg.clash.registry.MiscRegistry;
import com.jsburg.clash.weapons.GreatbladeItem;
import com.jsburg.clash.weapons.util.AttackHelper;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public class GreatbladeSlashEntity extends Entity {

    public int spriteFlip = 0;
    private UUID ownerUUID;
    private int ownerEntityId;
    //Checks for having left the owner's hitbox. Dont need it
//    private boolean leftOwner;
    private int timeLeft = 3;
    public float damage = 10;
    public ItemStack swordStack = ItemStack.EMPTY;
    private final List<Entity> hitEntities = Lists.newLinkedList();
    private boolean isExecutioner = false;
    private int hasThrumParticle = 0;

    public GreatbladeSlashEntity(EntityType<?> type, Level world) {
        super(type, world);
    }

    public GreatbladeSlashEntity(Level world, ItemStack sword, Vec3 pos, Entity owner, boolean executioner) {
        this(executioner ? MiscRegistry.GREATBLADE_SLASH_EXECUTIONER.get() : MiscRegistry.GREATBLADE_SLASH.get(), world);
        this.setPos(pos.x(), pos.y(), pos.z());
        swordStack = sword;
        setOwner(owner);
        if (executioner) {
            damage += 5;
            isExecutioner = true;
        }
    }
    public void applyThrum() {
        hasThrumParticle = 1;
    }
    public void applyWhirlwind(int level) {
        timeLeft += level;
    }
    public void setDamage(float damage) {
        this.damage = damage;
    }

    public void setOwner(Entity owner) {
        if (owner != null) {
            ownerUUID = owner.getUUID();
            ownerEntityId = owner.getId();
        }
    }

    @Nullable
    public Entity getOwner() {
        if (this.ownerUUID != null && this.level() instanceof ServerLevel) {
            return ((ServerLevel)this.level()).getEntity(this.ownerUUID);
        } else {
            return this.ownerEntityId != 0 ? this.level().getEntity(this.ownerEntityId) : null;
        }
    }

    @Override
    public void tick() {
        super.tick();

        Vec3 motion = getDeltaMovement();
        double speed = motion.length();
        Vec3 centerPos = getBoundingBox().getCenter();
        double yOff = centerPos.y() - getY();
        if (speed > 0) {
            //Friction
            double deceleration = .2;
            speed = Math.max(0, speed - deceleration);
            setDeltaMovement(motion.normalize().scale(speed));
            motion = getDeltaMovement();

            //Movement
            Vec3 nextPos = position().add(motion);
            BlockHitResult raytrace = level().clip(
                    new ClipContext(centerPos, nextPos.add(0, yOff, 0), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null));
            if (raytrace.getType() == HitResult.Type.MISS) {
                this.moveTo(nextPos);
            }
            else {
                this.moveTo(raytrace.getLocation().subtract(0, yOff, 0));
            }

        }

        AttackHelper.makeParticleServer(level(), AllParticles.GREATBLADE_SLASH, getBoundingBox().getCenter().add(motion.scale(.5)),
                //isExecutioner, isBlue, isFlipped
                isExecutioner ? 1 : 0, hasThrumParticle, spriteFlip);

        //Collision
        Entity owner = getOwner();
        Function<LivingEntity, Boolean> enemyChecker;
        if (owner == null) {
            enemyChecker = (a) -> true;
        }
        else {
            enemyChecker = (livingentity) -> !owner.isAlliedTo(livingentity);
        }
        //Yeah
        Level level = level();
        //Uhhhhh. Damage source.
        DamageSource damageSource = owner instanceof Player ?
                level.damageSources().playerAttack((Player) owner) :
                owner == null ? level.damageSources().thrown(this, null):
                        level.damageSources().mobAttack((LivingEntity) owner);

        for(LivingEntity livingentity : level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox())) {
            if (livingentity != owner && enemyChecker.apply(livingentity) && !hitEntities.contains(livingentity) && (!(livingentity instanceof ArmorStand) || !((ArmorStand) livingentity).isMarker())) {
                //Skip over pets tamed by the player
                if (livingentity instanceof TamableAnimal) {
                    if (owner != null && ((TamableAnimal) livingentity).isOwnedBy((LivingEntity) owner)) {
                        continue;
                    }
                }
                hitEntities.add(livingentity);
                if (!level.isClientSide) {
                    Vec3 diff = livingentity.position().subtract(this.position()).normalize();
                    livingentity.knockback(0.4f, -diff.x(), -diff.z());

                    //Damage entity
                    float lastHealth = (livingentity).getHealth();
                    if (livingentity.hurt(damageSource, damage + AttackHelper.getBonusEnchantmentDamage(swordStack, livingentity))) {
                        float healthDifference = lastHealth - (livingentity).getHealth();

                        //player.addStat(Stats.DAMAGE_DEALT, Math.round(healthDifference * 10));
                        // Create hurt effects
                        if (level instanceof ServerLevel && healthDifference > 2.0F) {
                            int k = (int) (healthDifference * 0.5D);
                            ((ServerLevel) level)
                                    .sendParticles(
                                            ParticleTypes.DAMAGE_INDICATOR,
                                            livingentity.getX(),
                                            livingentity.getY(0.5),
                                            livingentity.getZ(),
                                            k, 0.1, 0.0, 0.1, 0.2);
                        }
                        if (swordStack.getItem() instanceof GreatbladeItem) {
                            ((GreatbladeItem)swordStack.getItem()).onSlashHit(swordStack, livingentity, owner);
                        }
                    }
                }
            }
        }

//        AttackHelper.makeParticleServer((ServerWorld) this.world, AllParticles.BUTCHER_SPARK.get(), centerPos, Vector3d.ZERO, 0);

        //Limited duration
        timeLeft -= 1;
        if (timeLeft == 0) {
            remove(RemovalReason.DISCARDED);
            hitEntities.clear();
        }

    }

    //Entity data stuff
    @Override
    protected void defineSynchedData() {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        if (this.ownerUUID != null) {
            compound.putUUID("Owner", this.ownerUUID);
        }
//        if (this.leftOwner) {
//            compound.putBoolean("LeftOwner", true);
//        }
        compound.putFloat("Damage", damage);
        compound.putInt("TimeLeft", timeLeft);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        if (compound.hasUUID("Owner")) {
            this.ownerUUID = compound.getUUID("Owner");
        }
//        this.leftOwner = compound.getBoolean("LeftOwner");
        this.damage = compound.getFloat("Damage");
        this.timeLeft = compound.getInt("TimeLeft");

    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        Entity entity = this.getOwner();
        return new ClientboundAddEntityPacket(this, entity == null ? 0 : entity.getId());
    }
}
