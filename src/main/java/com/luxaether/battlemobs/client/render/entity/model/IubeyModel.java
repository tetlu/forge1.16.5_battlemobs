package com.luxaether.battlemobs.client.render.entity.model;


import com.luxaether.battlemobs.common.entities.hostile.IubeyEntity;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class IubeyModel extends BipedModel<IubeyEntity> {
    
    public IubeyModel() {
        super(0.0f, 0.0f, 64, 64);
    }
    
    public void setRotationAngles(final IubeyEntity entityIn, final float limbSwing, final float limbSwingAmount, final float ageInTicks, final float netHeadYaw, final float headPitch) {
        super.setupAnim(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }
}
