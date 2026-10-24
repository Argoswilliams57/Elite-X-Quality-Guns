package com.argos_garcia.elitex.fabric.registry;

import com.argos_garcia.elitex.fabric.EliteXFabric;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.gun.FireMode;
import com.tacz.guns.init.ModItems;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Fabric port of {@code com.argos_garcia.registeration_center.EliteXCreativeTabRegistry}.
 *
 * <p>The gun list, ammo counts, fire modes and bullet-in-barrel flags are copied
 * 1:1 from the NeoForge version. Item data lives in item components, so the
 * stacks are built with TaCZ's own gun API ({@link IGun}); TaCZ stores exactly the same
 * four values ({@code GunId}, {@code GunCurrentAmmoCount}, {@code GunFireMode},
 * {@code HasBulletInBarrel}) in the item's data components on both loaders.</p>
 *
 * <p>Fabric API 26.3 renamed the item group API to the creative tab API
 * ({@code FabricCreativeModeTab}), whose builder supplies the row/column that vanilla's
 * 26.3 builder requires.</p>
 */
public final class EliteXFabricCreativeTab {

    private static final Identifier ELITEX_TAB_ID = Identifier.fromNamespaceAndPath(EliteXFabric.MOD_ID, "elitex");

    public static CreativeModeTab ELITE_X_CREATIVE_TAB;

    private EliteXFabricCreativeTab() {
    }

    public static void register() {
        ELITE_X_CREATIVE_TAB = Registry.register(
                BuiltInRegistries.CREATIVE_MODE_TAB,
                ELITEX_TAB_ID,
                FabricCreativeModeTab.builder()
                        .title(Component.translatable("creative_tab.elitex"))
                        .icon(() -> new ItemStack(EliteXFabricItems.CREATIVE_ICON))
                        .displayItems((displayContext, entries) -> {

                            // Base item from TaCZ: Refabricated.
                            Item GunItem = ModItems.MODERN_KINETIC_GUN;

                            // Fire modes are "SEMI", "AUTO", "BURST".

                            // Pistols
                            entries.accept(createCustomItem(GunItem, "elitex:colt_m1911x1", 7, FireMode.SEMI, true, "elitex.gun.colt_m1911x1.name"));
                            entries.accept(createCustomItem(GunItem, "elitex:jrx", 20, FireMode.SEMI, true, "elitex.gun.jrx.name"));
                            entries.accept(createCustomItem(GunItem, "elitex:five_seven", 20, FireMode.SEMI, true, "elitex.gun.five_seven.name"));
                            entries.accept(createCustomItem(GunItem, "elitex:srz_2011", 16, FireMode.SEMI, true, "elitex.gun.srz_2011.name"));
                            entries.accept(createCustomItem(GunItem, "elitex:bo1_1911", 8, FireMode.SEMI, true, "elitex.gun.bo1_1911.name"));
                            entries.accept(createCustomItem(GunItem, "elitex:op1911", 7, FireMode.SEMI, true, "elitex.gun.op1911.name"));
                            entries.accept(createCustomItem(GunItem, "elitex:springfield_m1911", 7, FireMode.SEMI, true, "elitex.gun.springfield_m1911.name"));
                            entries.accept(createCustomItem(GunItem, "elitex:raygun_bo6", 20, FireMode.AUTO, false, "elitex.gun.raygun_bo6.name"));
                            entries.accept(createCustomItem(GunItem, "elitex:trs_bull", 5, FireMode.SEMI, false, "elitex.gun.trs_bull.name"));
                            entries.accept(createCustomItem(GunItem, "elitex:dual_hr", 5, FireMode.SEMI, false, "elitex.gun.dual_hr.name"));
                            entries.accept(createCustomItem(GunItem, "elitex:glock40", 18, FireMode.SEMI, true, "elitex.gun.glock40.name"));
                            entries.accept(createCustomItem(GunItem, "elitex:glock40_normal", 18, FireMode.SEMI, true, "elitex.gun.glock40_normal.name"));

                            // SMGs
                            entries.accept(createCustomItem(GunItem, "elitex:ro635v3", 32, FireMode.AUTO, true, "elitex.gun.ro635v3.name"));
                            entries.accept(createCustomItem(GunItem, "elitex:mp5_classic", 30, FireMode.AUTO, true, "elitex.gun.mp5_classic.name"));
                            entries.accept(createCustomItem(GunItem, "elitex:vector45acp", 35, FireMode.AUTO, true, "elitex.gun.vector45acp.name"));
                            entries.accept(createCustomItem(GunItem, "elitex:mp7r", 30, FireMode.AUTO, true, "elitex.gun.mp7r.name"));


                            // Assault Rifles
                            entries.accept(createCustomItem(GunItem, "elitex:ak47", 30, FireMode.AUTO, true, "elitex.gun.ak47.name"));
                            entries.accept(createCustomItem(GunItem, "elitex:m4_sopmod", 35, FireMode.AUTO, true, "elitex.gun.m4_sopmod.name"));
                            entries.accept(createCustomItem(GunItem, "elitex:mcs_spear", 20, FireMode.AUTO, true, "elitex.gun.mcs_spear.name"));
                            entries.accept(createCustomItem(GunItem, "elitex:famas_f1", 25, FireMode.AUTO, true, "elitex.gun.famas_f1.name"));
                            entries.accept(createCustomItem(GunItem, "elitex:akx105", 30, FireMode.AUTO, true, "elitex.gun.akx105.name"));
                            entries.accept(createCustomItem(GunItem, "elitex:fh_scar18", 20, FireMode.SEMI, true, "elitex.gun.fh_scar18.name"));
                            entries.accept(createCustomItem(GunItem, "elitex:fs_scar19", 30, FireMode.AUTO, true, "elitex.gun.fs_scar19.name"));
                            entries.accept(createCustomItem(GunItem, "elitex:m4a1_classic", 30, FireMode.AUTO, true, "elitex.gun.m4a1_classic.name"));
                            entries.accept(createCustomItem(GunItem, "elitex:m16", 30, FireMode.BURST, true, "elitex.gun.m16.name"));
                            entries.accept(createCustomItem(GunItem, "elitex:golden_ak", 30, FireMode.AUTO, true, "elitex.gun.golden_ak.name"));
                            entries.accept(createCustomItem(GunItem, "elitex:an94", 30, FireMode.AUTO, true, "elitex.gun.an94.name"));
                            entries.accept(createCustomItem(GunItem, "elitex:g11", 50, FireMode.AUTO, false, "elitex.gun.g11.name"));

                            // DMRs / Marksman Rifles
                            entries.accept(createCustomItem(GunItem, "elitex:sassy_dmr", 20, FireMode.SEMI, true, "elitex.gun.sassy_dmr.name"));

                            // Wonder Weapons
                            entries.accept(createCustomItem(GunItem, "elitex:ray_gun_mk2", 21, FireMode.BURST, true, "elitex.gun.ray_gun_mk2.name"));
                            entries.accept(createCustomItem(GunItem, "elitex:wonder_waffle", 3, FireMode.SEMI, true, "elitex.gun.wonder_waffle.name"));
                            entries.accept(createCustomItem(GunItem, "elitex:mustang_and_sally", 12, FireMode.SEMI, true, "elitex.gun.mustang_and_sally.name"));
                            entries.accept(createCustomItem(GunItem, "elitex:origin_electric_staff", 9, FireMode.SEMI, false, "elitex.gun.origin_electric_staff.name"));
                            entries.accept(createCustomItem(GunItem, "elitex:origin_fire_staff", 10, FireMode.SEMI, false, "elitex.gun.origin_fire_staff.name"));
                            entries.accept(createCustomItem(GunItem, "elitex:origin_ice_staff", 10, FireMode.SEMI, false, "elitex.gun.origin_ice_staff.name"));
                            entries.accept(createCustomItem(GunItem, "elitex:origin_wind_staff", 10, FireMode.SEMI, false, "elitex.gun.origin_wind_staff.name"));

                            // Shotguns
                            entries.accept(createCustomItem(GunItem, "elitex:benelli_super", 8, FireMode.SEMI, true, "elitex.gun.benelli_super.name"));
                            entries.accept(createCustomItem(GunItem, "elitex:spaz12", 8, FireMode.SEMI, true, "elitex.gun.spaz12.name"));
                            entries.accept(createCustomItem(GunItem, "elitex:eb800", 8, FireMode.SEMI, true, "elitex.gun.eb800.name"));

                            // Bolt Action Rifles
                            entries.accept(createCustomItem(GunItem, "elitex:kar98k", 5, FireMode.SEMI, false, "elitex.gun.kar98k.name"));
                            entries.accept(createCustomItem(GunItem, "elitex:m1_garand", 8, FireMode.SEMI, false, "elitex.gun.m1_garand.name"));
                            entries.accept(createCustomItem(GunItem, "elitex:lee", 10, FireMode.SEMI, false, "elitex.gun.lee.name"));
                            entries.accept(createCustomItem(GunItem, "elitex:sp_m1903", 5, FireMode.SEMI, false, "elitex.gun.sp_m1903.name"));

                            // Snipers
                            entries.accept(createCustomItem(GunItem, "elitex:inter_m200", 7, FireMode.SEMI, true, "elitex.gun.inter_m200.name"));
                            entries.accept(createCustomItem(GunItem, "elitex:psg1", 5, FireMode.SEMI, true, "elitex.gun.psg1.name"));

                            // LMGs
                            entries.accept(createCustomItem(GunItem, "elitex:m249x", 150, FireMode.AUTO, false, "elitex.gun.m249x.name"));
                            entries.accept(createCustomItem(GunItem, "elitex:mgr338", 200, FireMode.AUTO, true, "elitex.gun.mgr338.name"));

                        })
                        .build()
        );
    }

    /**
     * Builds a gun stack with the same gun data the NeoForge version writes.
     */
    private static ItemStack createCustomItem(Item baseItem, String gunId, int ammoCount, FireMode fireMode,
                                             boolean hasBulletInBarrel, String displayName) {
        ItemStack stack = new ItemStack(baseItem);

        // Item data lives in components. TaCZ's IGun API writes the exact same keys the
        // 1.20.1 NBT layout used (GunId, GunCurrentAmmoCount, GunFireMode, HasBulletInBarrel).
        IGun gun = IGun.getIGunOrNull(stack);
        if (gun != null) {
            gun.setGunId(stack, Identifier.parse(gunId)); // Unique gun identifier
            gun.setCurrentAmmoCount(stack, ammoCount); // Current ammo count
            gun.setFireMode(stack, fireMode); // Fire mode (SEMI, AUTO, BURST)
            gun.setBulletInBarrel(stack, hasBulletInBarrel); // hidden round in the chamber
        }

        // Set the custom display name and disable italics
        stack.set(DataComponents.CUSTOM_NAME, Component.translatable(displayName).withStyle(style -> style.withItalic(false)));

        return stack;
    }
}
