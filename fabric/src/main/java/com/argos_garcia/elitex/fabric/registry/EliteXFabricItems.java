package com.argos_garcia.elitex.fabric.registry;

import com.argos_garcia.elitex.fabric.EliteXFabric;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;


public final class EliteXFabricItems {

    public static Item CREATIVE_ICON;

    private EliteXFabricItems() {
    }

    public static void register() {
        CREATIVE_ICON = Registry.register(
                Registries.ITEM,
                new Identifier(EliteXFabric.MOD_ID, "creative_icon"),
                new Item(new FabricItemSettings())
        );
    }
}
