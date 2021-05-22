package com.luxaether.battlemobs.common.items;

import java.util.function.Predicate;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.IVanishable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShootableItem;
import net.minecraft.item.UseAction;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class UpgradeableBow extends ShootableItem implements IVanishable {
   public UpgradeableBow(Item.Properties properties) {
      super(properties);
   }

   public void releaseUsing(ItemStack stack, World world, LivingEntity entity, int useCount) {
      if (entity instanceof PlayerEntity) {
         PlayerEntity playerentity = (PlayerEntity)entity;
         boolean flag = playerentity.abilities.instabuild || EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, stack) > 0;
         ItemStack itemstack = playerentity.getProjectile(stack);

         int i = this.getUseDuration(stack) - useCount;
         i = net.minecraftforge.event.ForgeEventFactory.onArrowLoose(stack, world, playerentity, i, !itemstack.isEmpty() || flag);
         if (i < 0) return;

         if (!itemstack.isEmpty() || flag) {
            if (itemstack.isEmpty()) {
               itemstack = new ItemStack(Items.ARROW);
            }

            float f = getPowerForTime(i);
            if (!((double)f < 0.1D)) {
               boolean flag1 = playerentity.abilities.instabuild || (itemstack.getItem() instanceof ArrowItem && ((ArrowItem)itemstack.getItem()).isInfinite(itemstack, stack, playerentity));
               if (!world.isClientSide) {
                  ArrowItem arrowitem = (ArrowItem)(itemstack.getItem() instanceof ArrowItem ? itemstack.getItem() : Items.ARROW);
                  AbstractArrowEntity abstractarrowentity = arrowitem.createArrow(world, itemstack, playerentity);
                  abstractarrowentity = customArrow(abstractarrowentity);
                  abstractarrowentity.shootFromRotation(playerentity, playerentity.xRot, playerentity.yRot, 0.0F, f * 3.0F, 1.0F);
                  if (f == 1.0F) {
                     abstractarrowentity.setCritArrow(true);
                  }

                  int j = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, stack);
                  if (j > 0) {
                     abstractarrowentity.setBaseDamage(abstractarrowentity.getBaseDamage() + (double)j * 0.5D + 0.5D);
                  }

                  int k = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, stack);
                  if (k > 0) {
                     abstractarrowentity.setKnockback(k);
                  }

                  if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FLAMING_ARROWS, stack) > 0) {
                     abstractarrowentity.setSecondsOnFire(100);
                  }

                  stack.hurtAndBreak(1, playerentity, (player) -> {
                     player.broadcastBreakEvent(playerentity.getUsedItemHand());
                  });
                  if (flag1 || playerentity.abilities.instabuild && (itemstack.getItem() == Items.SPECTRAL_ARROW || itemstack.getItem() == Items.TIPPED_ARROW)) {
                     abstractarrowentity.pickup = AbstractArrowEntity.PickupStatus.CREATIVE_ONLY;
                  }

                  world.addFreshEntity(abstractarrowentity);
               }

               world.playSound((PlayerEntity)null, playerentity.getX(), playerentity.getY(), playerentity.getZ(), SoundEvents.ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (random.nextFloat() * 0.4F + 1.2F) + f * 0.5F);
               if (!flag1 && !playerentity.abilities.instabuild) {
                  itemstack.shrink(1);
                  if (itemstack.isEmpty()) {
                     playerentity.inventory.removeItem(itemstack);
                  }
               }

               playerentity.awardStat(Stats.ITEM_USED.get(this));
            }
         }
      }
   }

   public static float getPowerForTime(int power) {
      float f = (float)power / 20.0F;
      f = (f * f + f * 2.0F) / 3.0F;
      if (f > 1.0F) {
         f = 1.0F;
      }

      return f;
   }

   public int getUseDuration(ItemStack bow) {
      return 72000;
   }

   public UseAction getUseAnimation(ItemStack bow) {
      return UseAction.BOW;
   }

   public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
      ItemStack itemstack = player.getItemInHand(hand);
      boolean flag = !player.getProjectile(itemstack).isEmpty();

      ActionResult<ItemStack> ret = net.minecraftforge.event.ForgeEventFactory.onArrowNock(itemstack, world, player, hand, flag);
      if (ret != null) return ret;

      if (!player.abilities.instabuild && !flag) {
         return ActionResult.fail(itemstack);
      } else {
    	  player.startUsingItem(hand);
         return ActionResult.consume(itemstack);
      }
   }

   public Predicate<ItemStack> getAllSupportedProjectiles() {
      return ARROW_ONLY;
   }

   public AbstractArrowEntity customArrow(AbstractArrowEntity arrow) {
      return arrow;
   }

   public int getDefaultProjectileRange() {
      return 15;
   }
}
