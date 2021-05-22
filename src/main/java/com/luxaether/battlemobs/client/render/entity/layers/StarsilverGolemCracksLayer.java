package com.luxaether.battlemobs.client.render.entity.layers;

import com.google.common.collect.ImmutableMap;
import com.luxaether.battlemobs.client.render.entity.model.StarsilverGolemModel;
import com.luxaether.battlemobs.common.entities.passive.StarsilverGolemEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.Map;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class StarsilverGolemCracksLayer extends LayerRenderer<StarsilverGolemEntity, StarsilverGolemModel<StarsilverGolemEntity>> {
   private static final Map<StarsilverGolemEntity.Cracks, ResourceLocation> resourceLocations = ImmutableMap.of(StarsilverGolemEntity.Cracks.LOW, new ResourceLocation("battlemobs:textures/entities/starsilver_golem/starsilver_golem_crackiness_low.png"), StarsilverGolemEntity.Cracks.MEDIUM, new ResourceLocation("battlemobs:textures/entities/starsilver_golem/starsilver_golem_crackiness_medium.png"), StarsilverGolemEntity.Cracks.HIGH, new ResourceLocation("battlemobs:textures/entities/starsilver_golem/starsilver_golem_crackiness_high.png"));

   public StarsilverGolemCracksLayer(IEntityRenderer<StarsilverGolemEntity, StarsilverGolemModel<StarsilverGolemEntity>> golemEntity) {
      super(golemEntity);
   }

   public void render(MatrixStack p_225628_1_, IRenderTypeBuffer buffer, int p_225628_3_, StarsilverGolemEntity golemEntity, float p_225628_5_, float p_225628_6_, float p_225628_7_, float p_225628_8_, float p_225628_9_, float p_225628_10_) {
      if (!golemEntity.isInvisible()) {
    	  StarsilverGolemEntity.Cracks starsilvergolementity$cracks = golemEntity.getCrackiness();
         if (starsilvergolementity$cracks != StarsilverGolemEntity.Cracks.NONE) {
            ResourceLocation resourcelocation = resourceLocations.get(starsilvergolementity$cracks);
            renderColoredCutoutModel(this.getParentModel(), resourcelocation, p_225628_1_, buffer, p_225628_3_, golemEntity, 1.0F, 1.0F, 1.0F);
         }
      }
   }
}