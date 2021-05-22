package com.luxaether.battlemobs.common.entities.passive;

import com.google.common.collect.ImmutableList;
import com.luxaether.battlemobs.core.registry.EntityTypeInit;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IAngerable;
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
import net.minecraft.entity.ai.goal.PatrolVillageGoal;
import net.minecraft.entity.ai.goal.ResetAngerGoal;
import net.minecraft.entity.ai.goal.ReturnToVillageGoal;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.RangedInteger;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.TickRangeConverter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.BossInfo;
import net.minecraft.world.Explosion;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.spawner.WorldEntitySpawner;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class StarsilverGolemEntity extends GolemEntity implements IAngerable {
   protected static final DataParameter<Byte> DATA_FLAGS_ID = EntityDataManager.defineId(StarsilverGolemEntity.class, DataSerializers.BYTE);
   private int attackAnimationTick;
   private int randInterval = 400;
   private double additionalKnockback;
   private boolean moveReset = true;
   private static final RangedInteger PERSISTENT_ANGER_TIME = TickRangeConverter.rangeOfSeconds(20, 39);
   private int remainingPersistentAngerTime;
   private UUID persistentAngerTarget;
   
   private final ServerBossInfo bossEvent = (ServerBossInfo)(new ServerBossInfo(this.getDisplayName(), BossInfo.Color.GREEN, BossInfo.Overlay.PROGRESS));


   public StarsilverGolemEntity(EntityType<? extends StarsilverGolemEntity> type, World world) {
      super(type, world);
      this.maxUpStep = 1.0F;
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, true));
      this.goalSelector.addGoal(2, new MoveTowardsTargetGoal(this, 0.9D, 32.0F));
      this.goalSelector.addGoal(2, new ReturnToVillageGoal(this, 0.6D, false));
      this.goalSelector.addGoal(4, new PatrolVillageGoal(this, 0.6D));
      this.goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, 6.0F));
      this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
      //this.targetSelector.addGoal(1, new DefendVillageTargetGoal(this));
      this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, 10, true, false, this::isAngryAt));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, MobEntity.class, 5, false, false, (target) -> {
         return target instanceof IMob;
      }));
      this.targetSelector.addGoal(4, new ResetAngerGoal<>(this, false));
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_FLAGS_ID, (byte)0);
   }

   public static AttributeModifierMap.MutableAttribute createAttributes() {
      return MobEntity.createMobAttributes()
    		  .add(Attributes.FOLLOW_RANGE, 48.0D)
    		  .add(Attributes.MOVEMENT_SPEED, 0.3D)
    		  .add(Attributes.MAX_HEALTH, 150.0D)
    		  .add(Attributes.ARMOR, 5.0D)
    		  .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
    		  //.add(Attributes.ATTACK_SPEED, 6.0D)
    		  .add(Attributes.ATTACK_DAMAGE, 28.0D);
   }

   protected int decreaseAirSupply(int airSupply) {
      return airSupply;
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
      
      if (!this.isAngry()) {
    	  if (this.tickCount % 200 == 0) {
    		  this.heal(1);
    	  }
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

      if (!this.level.isClientSide) {
         this.updatePersistentAnger((ServerWorld)this.level, true);
      }
      
      this.bossEvent.setPercent(this.getHealth() / this.getMaxHealth());
   }

   public boolean canAttackType(EntityType<?> target) {
      if (this.isPlayerCreated() && target == EntityType.PLAYER) {
         return false;
      } else {
         return super.canAttackType(target);
      }
   }

   public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
      super.addAdditionalSaveData(p_213281_1_);
      p_213281_1_.putBoolean("PlayerCreated", this.isPlayerCreated());
      this.addPersistentAngerSaveData(p_213281_1_);
   }

   public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
      super.readAdditionalSaveData(p_70037_1_);
      this.setPlayerCreated(p_70037_1_.getBoolean("PlayerCreated"));
      if(!level.isClientSide) //FORGE: allow this entity to be read from nbt on client. (Fixes MC-189565)
      this.readPersistentAngerSaveData((ServerWorld)this.level, p_70037_1_);
   }
   
   /*public void startSeenByPlayer(ServerPlayerEntity player) {
	  super.startSeenByPlayer(player);
	  this.bossEvent.addPlayer(player);
   }

   public void stopSeenByPlayer(ServerPlayerEntity player) {
	   super.stopSeenByPlayer(player);
	   this.bossEvent.removePlayer(player);
   }*/

   public void startPersistentAngerTimer() {
      this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.randomValue(this.random));
   }

   public void setRemainingPersistentAngerTime(int p_230260_1_) {
      this.remainingPersistentAngerTime = p_230260_1_;
   }

   public int getRemainingPersistentAngerTime() {
      return this.remainingPersistentAngerTime;
   }

   public void setPersistentAngerTarget(@Nullable UUID p_230259_1_) {
      this.persistentAngerTarget = p_230259_1_;
   }

   public UUID getPersistentAngerTarget() {
      return this.persistentAngerTarget;
   }

   private float getAttackDamage() {
      return (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
   }

   public boolean doHurtTarget(Entity target) {
      this.attackAnimationTick = 10;
      this.level.broadcastEntityEvent(this, (byte)4);
      float attackPower = this.getAttackDamage();
      float damage = (int)attackPower > 0 ? attackPower / (4F/3F) + (float)random.nextInt((int)attackPower/2) : attackPower;
      boolean hitTarget = target.hurt(DamageSource.mobAttack(this), damage);
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

      this.playSound(SoundEvents.IRON_GOLEM_ATTACK, 1.0F, 1.0F);
      return hitTarget;
   }

   public boolean hurt(DamageSource source, float p_70097_2_) {
	   StarsilverGolemEntity.Cracks starsilvergolem$cracks = this.getCrackiness();
	  if (source.getEntity() == this) return false;
      boolean takeDamage = super.hurt(source, p_70097_2_);
      if (takeDamage && this.getCrackiness() != starsilvergolem$cracks) {
         this.playSound(SoundEvents.IRON_GOLEM_DAMAGE, 1.0F, 1.0F);
      }

      return takeDamage;
   }

   public StarsilverGolemEntity.Cracks getCrackiness() {
      return StarsilverGolemEntity.Cracks.byFraction(this.getHealth() / this.getMaxHealth());
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
      return SoundEvents.IRON_GOLEM_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.IRON_GOLEM_DEATH;
   }

   protected ActionResultType mobInteract(PlayerEntity player, Hand hand) {
      ItemStack itemstack = player.getItemInHand(hand);
      Item item = itemstack.getItem();
      if (item != Items.IRON_INGOT) {
         return ActionResultType.PASS;
      } else {
         float f = this.getHealth();
         this.heal(15.0F);
         if (this.getHealth() == f) {
            return ActionResultType.PASS;
         } else {
            float f1 = 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F;
            this.playSound(SoundEvents.IRON_GOLEM_REPAIR, 1.0F, f1);
            if (!player.abilities.instabuild) {
               itemstack.shrink(1);
            }

            return ActionResultType.sidedSuccess(this.level.isClientSide);
         }
      }
   }

   protected void playStepSound(BlockPos pos, BlockState state) {
      this.playSound(SoundEvents.IRON_GOLEM_STEP, 1.0F, 1.0F);
   }

   public boolean isPlayerCreated() {
      return (this.entityData.get(DATA_FLAGS_ID) & 1) != 0;
   }

   public void setPlayerCreated(boolean playerCreated) {
      byte b0 = this.entityData.get(DATA_FLAGS_ID);
      if (playerCreated) {
         this.entityData.set(DATA_FLAGS_ID, (byte)(b0 | 1));
      } else {
         this.entityData.set(DATA_FLAGS_ID, (byte)(b0 & -2));
      }

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
            if (!WorldEntitySpawner.isValidEmptySpawnBlock(p_205019_1_, blockpos2, blockstate1, blockstate1.getFluidState(), EntityTypeInit.STARSILVER_GOLEM.get())) {
               return false;
            }
         }

         return WorldEntitySpawner.isValidEmptySpawnBlock(p_205019_1_, blockpos, p_205019_1_.getBlockState(blockpos), Fluids.EMPTY.defaultFluidState(), EntityTypeInit.STARSILVER_GOLEM.get()) && p_205019_1_.isUnobstructed(this);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public Vector3d getLeashOffset() {
      return new Vector3d(0.0D, (double)(0.875F * this.getEyeHeight()), (double)(this.getBbWidth() * 0.4F));
   }

   public static enum Cracks {
      NONE(1.0F),
      LOW(0.75F),
      MEDIUM(0.5F),
      HIGH(0.25F);

      private static final List<StarsilverGolemEntity.Cracks> BY_DAMAGE = Stream.of(values()).sorted(Comparator.comparingDouble((p_226516_0_) -> {
         return (double)p_226516_0_.fraction;
      })).collect(ImmutableList.toImmutableList());
      private final float fraction;

      private Cracks(float p_i225732_3_) {
         this.fraction = p_i225732_3_;
      }

      public static StarsilverGolemEntity.Cracks byFraction(float p_226515_0_) {
         for(StarsilverGolemEntity.Cracks starsilvergolementity$cracks : BY_DAMAGE) {
            if (p_226515_0_ < starsilvergolementity$cracks.fraction) {
               return starsilvergolementity$cracks;
            }
         }

         return NONE;
      }
   }
}
