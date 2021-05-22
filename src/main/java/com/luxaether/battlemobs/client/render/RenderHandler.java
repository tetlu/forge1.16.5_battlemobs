package com.luxaether.battlemobs.client.render;

import com.luxaether.battlemobs.BattleMobs;
import com.luxaether.battlemobs.client.render.entity.IubeyRenderer;
import com.luxaether.battlemobs.client.render.entity.StarsilverGolemRenderer;
import com.luxaether.battlemobs.core.registry.EntityTypeInit;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = BattleMobs.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class RenderHandler {

	@SubscribeEvent
    public static void register(final FMLClientSetupEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(EntityTypeInit.STARSILVER_GOLEM.get(), StarsilverGolemRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityTypeInit.IUBEY.get(), IubeyRenderer::new);

	}
}