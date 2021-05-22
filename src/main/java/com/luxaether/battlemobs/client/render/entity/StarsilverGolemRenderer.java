package com.luxaether.battlemobs.client.render.entity;

import com.luxaether.battlemobs.BattleMobs;
import com.luxaether.battlemobs.client.render.entity.layers.StarsilverGolemCracksLayer;
import com.luxaether.battlemobs.client.render.entity.model.StarsilverGolemModel;
import com.luxaether.battlemobs.common.entities.passive.StarsilverGolemEntity;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;


public class StarsilverGolemRenderer extends MobRenderer<StarsilverGolemEntity, StarsilverGolemModel<StarsilverGolemEntity>> {
   private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(BattleMobs.MOD_ID, "textures/entities/starsilver_golem/starsilver_golem.png");

   public ResourceLocation getTextureLocation(StarsilverGolemEntity p_110775_1_) {
	  return TEXTURE_LOCATION;
   }
   
   public StarsilverGolemRenderer(EntityRendererManager renderManagerIn) {
      super(renderManagerIn, new StarsilverGolemModel<>(), 0.7F);
      this.addLayer(new StarsilverGolemCracksLayer(this));
   }
   
   public void render(final StarsilverGolemEntity entityIn, final float entityYaw, final float partialTicks, final MatrixStack matrixStackIn, final IRenderTypeBuffer bufferIn, final int packedLightIn) {
       super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
   }

   protected void setupRotations(StarsilverGolemEntity entityIn, MatrixStack matrixStackIn, float p_225621_3_, float p_225621_4_, float p_225621_5_) {
      super.setupRotations(entityIn, matrixStackIn, p_225621_3_, p_225621_4_, p_225621_5_);
      if (!((double)entityIn.animationSpeed < 0.01D)) {
         float f1 = entityIn.animationPosition - entityIn.animationSpeed * (1.0F - p_225621_5_) + 6.0F;
         float f2 = (Math.abs(f1 % 13.0F - 6.5F) - 3.25F) / 3.25F;
         matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(6.5F * f2));
      }
   }
}
