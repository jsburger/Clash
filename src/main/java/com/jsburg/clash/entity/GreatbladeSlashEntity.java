package com.jsburg.clash.entity;

import com.google.common.collect.Lists;
import com.jsburg.clash.registry.AllParticles;
import com.jsburg.clash.registry.MiscRegistry;
import com.jsburg.clash.weapons.GreatbladeItem;
import com.jsburg.clash.weapons.util.AttackHelper;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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

    public GreatbladeSlashEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    public GreatbladeSlashEntity(World world, ItemStack sword, Vector3d pos, Entity owner, boolean executioner) {
        this(executioner ? MiscRegistry.GREATBLADE_SLASH_EXECUTIONER.get() : MiscRegistry.GREATBLADE_SLASH.get(), world);
        this.setPosition(pos.getX(), pos.getY(), pos.getZ());
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
            ownerUUID = owner.getUniqueID();
            ownerEntityId = owner.getEntityId();
        }
    }

    @Nullable
    public Entity getOwner() {
        if (this.ownerUUID != null && this.world instanceof ServerWorld) {
            return ((ServerWorld)this.world).getEntityByUuid(this.ownerUUID);
        } else {
            return this.ownerEntityId != 0 ? this.world.getEntityByID(this.ownerEntityId) : null;
        }
    }

    @Override
    public void tick() {
        super.tick();

        Vector3d motion = getMotion();
        double speed = motion.length();
        Vector3d centerPos = getBoundingBox().getCenter();
        double yOff = centerPos.getY() - getPosY();
        if (speed > 0) {
            //Friction
            double deceleration = .2;
            speed = Math.max(0, speed - deceleration);
            setMotion(motion.normalize().scale(speed));
            motion = getMotion();

            //Movement
            Vector3d nextPos = getPositionVec().add(motion);
            BlockRayTraceResult raytrace = world.rayTraceBlocks(
                    new RayTraceContext(centerPos, nextPos.add(0, yOff, 0), RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, null));
            if (raytrace.getType() == RayTraceResult.Type.MISS) {
                this.moveForced(nextPos);
            }
            else {
                this.moveForced(raytrace.getHitVec().subtract(0, yOff, 0));
            }

        }

        AttackHelper.makeParticleServer(world, AllParticles.GREATBLADE_SLASH, getBoundingBox().getCenter().add(motion.scale(.5)),
                //isExecutioner, isBlue, isFlipped
                isExecutioner ? 1 : 0, hasThrumParticle, spriteFlip);

        //Collision
        Entity owner = getOwner();
        Function<LivingEntity, Boolean> enemyChecker;
        if (owner == null) {
            enemyChecker = (a) -> true;
        }
        else {
            enemyChecker = (livingentity) -> !owner.isOnSameTeam(livingentity);
        }
        //Uhhhhh. Damage source.
        DamageSource damageSource = owner instanceof PlayerEntity ?
                DamageSource.causePlayerDamage((PlayerEntity) owner) :
                owner == null ? DamageSource.causeThrownDamage(this, null):
                    DamageSource.causeMobDamage((LivingEntity) owner);

        for(LivingEntity livingentity : world.getEntitiesWithinAABB(LivingEntity.class, this.getBoundingBox())) {
            if (livingentity != owner && enemyChecker.apply(livingentity) && !hitEntities.contains(livingentity) && (!(livingentity instanceof ArmorStandEntity) || !((ArmorStandEntity) livingentity).hasMarker())) {
                //Skip over pets tamed by the player
                if (livingentity instanceof TameableEntity) {
                    if (owner != null && ((TameableEntity) livingentity).isOwner((LivingEntity) owner)) {
                        continue;
                    }
                }
                hitEntities.add(livingentity);
                if (!world.isRemote) {
                    Vector3d diff = livingentity.getPositionVec().subtract(this.getPositionVec()).normalize();
                    livingentity.applyKnockback(0.4f, -diff.getX(), -diff.getZ());

                    //Damage entity
                    float lastHealth = (livingentity).getHealth();
                    if (livingentity.attackEntityFrom(damageSource, damage + AttackHelper.getBonusEnchantmentDamage(swordStack, livingentity))) {
                        float healthDifference = lastHealth - (livingentity).getHealth();

                        //player.addStat(Stats.DAMAGE_DEALT, Math.round(healthDifference * 10));
                        // Create hurt effects
                        if (world instanceof ServerWorld && healthDifference > 2.0F) {
                            int k = (int) (healthDifference * 0.5D);
                            ((ServerWorld) world)
                                    .spawnParticle(
                                            ParticleTypes.DAMAGE_INDICATOR,
                                            livingentity.getPosX(),
                                            livingentity.getPosYHeight(0.5),
                                            livingentity.getPosZ(),
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
            remove();
            hitEntities.clear();
        }

    }

    //Entity data stuff
    @Override
    protected void registerData() {

    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {
        if (this.ownerUUID != null) {
            compound.putUniqueId("Owner", this.ownerUUID);
        }
//        if (this.leftOwner) {
//            compound.putBoolean("LeftOwner", true);
//        }
        compound.putFloat("Damage", damage);
        compound.putInt("TimeLeft", timeLeft);
    }

    @Override
    protected void readAdditional(CompoundNBT compound) {
        if (compound.hasUniqueId("Owner")) {
            this.ownerUUID = compound.getUniqueId("Owner");
        }
//        this.leftOwner = compound.getBoolean("LeftOwner");
        this.damage = compound.getFloat("Damage");
        this.timeLeft = compound.getInt("TimeLeft");

    }

    //Copied from AbstractArrowEntity. Dunno why it isn't just on ProjectileEntity
    @Override
    public IPacket<?> createSpawnPacket() {
        Entity entity = this.getOwner();
        return new SSpawnObjectPacket(this, entity == null ? 0 : entity.getEntityId());
    }



    @OnlyIn(Dist.CLIENT)
    public static class SlashRenderer extends EntityRenderer<Entity> {
        public SlashRenderer(EntityRendererManager manager) {
            super(manager);
        }

        /**
         * Returns the location of an entity's texture.
         */
        public ResourceLocation getEntityTexture(Entity entity) {
            return AtlasTexture.LOCATION_BLOCKS_TEXTURE;
        }
    }

}
