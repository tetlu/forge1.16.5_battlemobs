package com.luxaether.battlemobs.core.registry;

import java.util.function.Supplier;

import com.luxaether.battlemobs.BattleMobs;
import com.luxaether.battlemobs.common.entities.hostile.IubeyEntity;
import com.luxaether.battlemobs.common.entities.passive.StarsilverGolemEntity;
import com.luxaether.battlemobs.common.entities.passive.TestGolemEntity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = BattleMobs.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EntityTypeInit {
	
	public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, BattleMobs.MOD_ID);

	
	public static final RegistryObject<EntityType<StarsilverGolemEntity>> STARSILVER_GOLEM = registerEntityType("starsilver_golem",
            () -> EntityType.Builder.<StarsilverGolemEntity>of((StarsilverGolemEntity::new), EntityClassification.CREATURE));
	public static final RegistryObject<EntityType<IubeyEntity>> IUBEY = registerEntityType("iubey",
            () -> EntityType.Builder.<IubeyEntity>of((IubeyEntity::new), EntityClassification.MONSTER));
	
	private static <T extends Entity> RegistryObject<EntityType<T>> registerEntityType(final String name, final Supplier<EntityType.Builder<T>> factory) {
        return ENTITIES.register(name,
                () -> factory.get().build(new ResourceLocation(BattleMobs.MOD_ID, name).toString())
        );
    }
	
	@SubscribeEvent
	public static void initializeAttributes(final EntityAttributeCreationEvent event) {
	    event.put(STARSILVER_GOLEM.get(), StarsilverGolemEntity.createAttributes().build());
	    event.put(IUBEY.get(), IubeyEntity.createAttributes().build());
	}
}
