package com.luxaether.battlemobs.client.render.entity;

import com.luxaether.battlemobs.BattleMobs;
import com.luxaether.battlemobs.client.render.entity.model.IubeyModel;
import com.luxaether.battlemobs.common.entities.hostile.IubeyEntity;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;


public class IubeyRenderer extends BipedRenderer<IubeyEntity, IubeyModel> {
   private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(BattleMobs.MOD_ID, "textures/entities/iubey/skin_theDarkness.png");

   public ResourceLocation getTextureLocation(IubeyEntity entity) {
      return TEXTURE_LOCATION;
   }
   
   public IubeyRenderer(EntityRendererManager renderManagerIn) {
      super(renderManagerIn, new IubeyModel(), 0.7F);
      //this.addLayer(new BipedArmorLayer<>(this, p_i50974_3_, p_i50974_4_));
   }
   
   public void render(final IubeyEntity entityIn, final float entityYaw, final float partialTicks, final MatrixStack matrixStackIn, final IRenderTypeBuffer bufferIn, final int packedLightIn) {
       super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
   }

   protected void setupRotations(IubeyEntity entityIn, MatrixStack matrixStackIn, float p_225621_3_, float p_225621_4_, float p_225621_5_) {
      super.setupRotations(entityIn, matrixStackIn, p_225621_3_, p_225621_4_, p_225621_5_);
      if (!((double)entityIn.animationSpeed < 0.01D)) {
         float f1 = entityIn.animationPosition - entityIn.animationSpeed * (1.0F - p_225621_5_) + 6.0F;
         float f2 = (Math.abs(f1 % 13.0F - 6.5F) - 3.25F) / 3.25F;
         matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(6.5F * f2));
      }
   }
}
