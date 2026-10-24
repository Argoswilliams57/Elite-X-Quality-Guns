package com.argos_garcia.registeration_center;

import com.argos_garcia.elitex.EliteXQualityGuns;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ElitexItemRegistry {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS , EliteXQualityGuns.MOD_ID);

    // Register the Creative Tab Icon Item

      //Add More items here i am leaving this line here disabled if anything I want to add customs items  for crafting purposes in the future they will go here

      public static final RegistryObject<Item> CREATIVE_ICON = ITEMS.register("creative_icon", () -> new Item(new Item.Properties()));

    //public static final RegistryObject<Item> CREATIVE_ICON = ITEMS.register("creative_icon", () -> new Item(new Item.Properties()));

}
