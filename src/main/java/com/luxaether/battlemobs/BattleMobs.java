package com.luxaether.battlemobs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.luxaether.battlemobs.client.render.RenderHandler;
import com.luxaether.battlemobs.core.registry.EntityTypeInit;
import com.luxaether.battlemobs.core.registry.ItemInit;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(BattleMobs.MOD_ID)
public class BattleMobs
{
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "battlemobs";
    
    public BattleMobs() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
    	bus.addListener(this::setup);

        ItemInit.ITEMS.register(bus);
        EntityTypeInit.ENTITIES.register(bus);
    	
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLClientSetupEvent event) 
    {
    	RenderHandler.register(event);
    }
}
