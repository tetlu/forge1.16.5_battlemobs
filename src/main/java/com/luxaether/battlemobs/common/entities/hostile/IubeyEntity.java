package com.luxaether.battlemobs.common.entities.hostile;

import com.luxaether.battlemobs.core.registry.EntityTypeInit;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.MoveTowardsTargetGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BossInfo;
import net.minecraft.world.Explosion;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraft.world.spawner.WorldEntitySpawner;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class IubeyEntity extends MonsterEntity {
   protected static final DataParameter<Byte> DATA_FLAGS_ID = EntityDataManager.defineId(IubeyEntity.class, DataSerializers.BYTE);
   private int attackAnimationTick;
   private int randInterval = 400;
   private double additionalKnockback;
   private boolean moveReset = true;
   
   private final ServerBossInfo bossEvent = (ServerBossInfo)(new ServerBossInfo(this.getDisplayName(), BossInfo.Color.RED, BossInfo.Overlay.NOTCHED_20));


   public IubeyEntity(EntityType<? extends IubeyEntity> type, World world) {
      super(type, world);
      this.maxUpStep = 1.0F;
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, 6.0F));
      this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
      this.addBehaviourGoals();
   }

   protected void addBehaviourGoals() {
	   this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, true));
	   this.goalSelector.addGoal(2, new MoveTowardsTargetGoal(this, 0.9D, 32.0F));
	   this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
	   this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, MobEntity.class, true));
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_FLAGS_ID, (byte)0);
   }

   public static AttributeModifierMap.MutableAttribute createAttributes() {
      return MobEntity.createMobAttributes()
    		  .add(Attributes.FOLLOW_RANGE, 32.0D)
    		  .add(Attributes.MOVEMENT_SPEED, 0.28D)
    		  .add(Attributes.MAX_HEALTH, 225.0D)
    		  .add(Attributes.ARMOR, 0.0D)
    		  .add(Attributes.KNOCKBACK_RESISTANCE, 0.1D)
    		  .add(Attributes.ATTACK_DAMAGE, 15.0D)
      		  .add(Attributes.ATTACK_SPEED, 8.0D);
   }

   protected void doPush(Entity entity) {
      if (entity instanceof IMob && this.getRandom().nextInt(20) == 0) {
         this.setTarget((LivingEntity)entity);
      }

      super.doPush(entity);
   }

   @SuppressWarnings("deprecation")
   public void aiStep() {
      super.aiStep();
      if (this.attackAnimationTick > 0) {
         --this.attackAnimationTick;
      }
      
      if (this.tickCount % 100 == 0) {
    	  this.heal(2);
      }
      
      if (moveReset) {
    	  this.additionalKnockback = 0D;
      }
      
      if (this.tickCount % randInterval == 0) {
    	  this.additionalKnockback = 1D;
    	  moveReset = false;
          randInterval = 300 + this.getRandom().nextInt(200);
       }

      if (getHorizontalDistanceSqr(this.getDeltaMovement()) > (double)2.5000003E-7F && this.random.nextInt(5) == 0) {
         int i = MathHelper.floor(this.getX());
         int j = MathHelper.floor(this.getY() - (double)0.2F);
         int k = MathHelper.floor(this.getZ());
         BlockPos pos = new BlockPos(i, j, k);
         BlockState blockstate = this.level.getBlockState(pos);
         if (!blockstate.isAir(this.level, pos)) {
            this.level.addParticle(new BlockParticleData(ParticleTypes.BLOCK, blockstate).setPos(pos), this.getX() + ((double)this.random.nextFloat() - 0.5D) * (double)this.getBbWidth(), this.getY() + 0.1D, this.getZ() + ((double)this.random.nextFloat() - 0.5D) * (double)this.getBbWidth(), 4.0D * ((double)this.random.nextFloat() - 0.5D), 0.5D, ((double)this.random.nextFloat() - 0.5D) * 4.0D);
         }
      }
      
      this.bossEvent.setPercent(this.getHealth() / this.getMaxHealth());
   }

   public boolean canAttackType(EntityType<?> target) { 
	   return super.canAttackType(target);
   }
   
   /*public void startSeenByPlayer(ServerPlayerEntity player) {
	  super.startSeenByPlayer(player);
	  this.bossEvent.addPlayer(player);
   }

   public void stopSeenByPlayer(ServerPlayerEntity player) {
	   super.stopSeenByPlayer(player);
	   this.bossEvent.removePlayer(player);
   }*/

   private float getAttackDamage() {
      return (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
   }

   public boolean doHurtTarget(Entity target) {
      this.attackAnimationTick = 10;
      this.level.broadcastEntityEvent(this, (byte)4);
      boolean hitTarget = target.hurt(DamageSource.mobAttack(this), this.getAttackDamage());
      if (hitTarget) {
    	  double xOffset = target.getX() - this.getX();
          double zOffset = target.getZ() - this.getZ();
          double length = Math.sqrt(Math.pow(xOffset, 2) + Math.pow(zOffset, 2));
    	  target.setDeltaMovement(target.getDeltaMovement().add((xOffset / length)*0.5D, additionalKnockback + (double)0.5F, (zOffset / length)*0.5D));
         this.doEnchantDamageEffects(this, target);
         if (!moveReset) {
        	 this.heal(25.0F);
             this.level.explode(this, this.getX(), this.getY(), this.getZ(), 4F, Explosion.Mode.NONE);
             //this.ignoreExplosion();
             moveReset = true;
         }
      }

      this.playSound(SoundEvents.PLAYER_ATTACK_WEAK, 1.0F, 1.0F);
      return hitTarget;
   }

   public boolean hurt(DamageSource source, float p_70097_2_) {
	  if (source.getEntity() == this) return false;
      boolean takeDamage = super.hurt(source, p_70097_2_);
      return takeDamage;
   }

   @OnlyIn(Dist.CLIENT)
   public void handleEntityEvent(byte p_70103_1_) {
      if (p_70103_1_ == 4) {
         this.attackAnimationTick = 10;
         this.playSound(SoundEvents.IRON_GOLEM_ATTACK, 1.0F, 1.0F);
      } else {
         super.handleEntityEvent(p_70103_1_);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public int getAttackAnimationTick() {
      return this.attackAnimationTick;
   }

   protected SoundEvent getHurtSound(DamageSource source) {
      return SoundEvents.PLAYER_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.PLAYER_DEATH;
   }

   protected void playStepSound(BlockPos pos, BlockState state) {
   }

   public void die(DamageSource fromSource) {
      super.die(fromSource);
   }

   public boolean checkSpawnObstruction(IWorldReader p_205019_1_) {
      BlockPos blockpos = this.blockPosition();
      BlockPos blockpos1 = blockpos.below();
      BlockState blockstate = p_205019_1_.getBlockState(blockpos1);
      if (!blockstate.entityCanStandOn(p_205019_1_, blockpos1, this)) {
         return false;
      } else {
         for(int i = 1; i < 3; ++i) {
            BlockPos blockpos2 = blockpos.above(i);
            BlockState blockstate1 = p_205019_1_.getBlockState(blockpos2);
            if (!WorldEntitySpawner.isValidEmptySpawnBlock(p_205019_1_, blockpos2, blockstate1, blockstate1.getFluidState(), EntityTypeInit.IUBEY.get())) {
               return false;
            }
         }

         return WorldEntitySpawner.isValidEmptySpawnBlock(p_205019_1_, blockpos, p_205019_1_.getBlockState(blockpos), Fluids.EMPTY.defaultFluidState(), EntityTypeInit.IUBEY.get()) && p_205019_1_.isUnobstructed(this);
      }
   }
}
