package com.luxaether.battlemobs.core.registry;

import com.luxaether.battlemobs.BattleMobs;
import com.luxaether.battlemobs.common.items.UpgradeableBow;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = BattleMobs.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ItemInit {

	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, BattleMobs.MOD_ID);
	
	public static final RegistryObject<UpgradeableBow> UPGRADEABLE_BOW = ITEMS.register("upgradeable_bow",
			() -> new UpgradeableBow(new Item.Properties().tab(ItemGroup.TAB_COMBAT)));
}
