package com.argos_garcia.registeration_center;

import com.argos_garcia.elitex.EliteXQualityGuns;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ElitexItemRegistry {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, EliteXQualityGuns.MOD_ID);

    // Register the Creative Tab Icon Item

      //Add More items here i am leaving this line here disabled if anything I want to add customs items  for crafting purposes in the future they will go here

      public static final DeferredHolder<Item, Item> CREATIVE_ICON = ITEMS.register("creative_icon", () -> new Item(new Item.Properties()));

}
