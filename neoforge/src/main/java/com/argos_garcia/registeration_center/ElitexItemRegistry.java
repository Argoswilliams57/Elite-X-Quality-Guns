package com.argos_garcia.registeration_center;

import com.argos_garcia.elitex.EliteXQualityGuns;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ElitexItemRegistry {

    // 26.3 requires every item's Item.Properties to carry its registry id. The specialised
    // item DeferredRegister (registerSimpleItem) sets that id from the registration name.
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(EliteXQualityGuns.MOD_ID);

    // Register the Creative Tab Icon Item

      //Add More items here i am leaving this line here disabled if anything I want to add customs items  for crafting purposes in the future they will go here

      public static final DeferredItem<Item> CREATIVE_ICON = ITEMS.registerSimpleItem("creative_icon");

}
