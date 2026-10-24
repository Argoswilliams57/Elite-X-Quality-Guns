package com.argos_garcia.elitex.fabric.registry;

import com.argos_garcia.elitex.fabric.EliteXFabric;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

/**
 * Registers the item used as the icon of the Elite X creative tab.
 *
 * <p>The registry name ({@code elitex:creative_icon}) is kept identical to the
 * NeoForge version so the shared model/texture files under
 * {@code assets/elitex/models/item} and {@code assets/elitex/textures/item}
 * work on both loaders.</p>
 *
 * <p>26.3 requires every item's {@code Item.Properties} to carry its registry id,
 * so the id is set explicitly before the item is constructed.</p>
 */
public final class EliteXFabricItems {

    private static final Identifier CREATIVE_ICON_ID =
            Identifier.fromNamespaceAndPath(EliteXFabric.MOD_ID, "creative_icon");

    public static Item CREATIVE_ICON;

    private EliteXFabricItems() {
    }

    public static void register() {
        CREATIVE_ICON = Registry.register(
                BuiltInRegistries.ITEM,
                CREATIVE_ICON_ID,
                new Item(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, CREATIVE_ICON_ID)))
        );
    }
}
