package com.argos_garcia.registeration_center;

import com.argos_garcia.elitex.EliteXQualityGuns;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.gun.FireMode;
import com.tacz.guns.init.ModItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

//Author Argos Garcia

public class EliteXCreativeTabRegistry {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, EliteXQualityGuns.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ELITE_X_CREATIVE_TAB = CREATIVE_MODE_TABS.register("elitex",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ElitexItemRegistry.CREATIVE_ICON.get()))// Your custom icon
                    .title(Component.translatable("creative_tab.elitex")) // Custom tab title
                    .displayItems((parameters, output) -> {

                        // First you Get base item 'modern_kinetic_gun' from the main mod
                        Item GunItem = ModItems.MODERN_KINETIC_GUN.get();

                        output.accept(createCustomItem(GunItem, "elitex:colt_m1911x1", 7, FireMode.SEMI, true, "elitex.gun.colt_m1911x1.name"));
                        output.accept(createCustomItem(GunItem, "elitex:jrx", 20, FireMode.SEMI, true, "elitex.gun.jrx.name"));
                        output.accept(createCustomItem(GunItem, "elitex:five_seven", 20, FireMode.SEMI, true, "elitex.gun.five_seven.name"));
                        output.accept(createCustomItem(GunItem, "elitex:srz_2011", 16, FireMode.SEMI, true, "elitex.gun.srz_2011.name"));
                        output.accept(createCustomItem(GunItem, "elitex:bo1_1911", 8, FireMode.SEMI, true, "elitex.gun.bo1_1911.name"));
                        output.accept(createCustomItem(GunItem, "elitex:op1911", 7, FireMode.SEMI, true, "elitex.gun.op1911.name"));
                        output.accept(createCustomItem(GunItem, "elitex:springfield_m1911", 7, FireMode.SEMI, true, "elitex.gun.springfield_m1911.name"));
                        output.accept(createCustomItem(GunItem, "elitex:raygun_bo6", 20, FireMode.AUTO, false, "elitex.gun.raygun_bo6.name"));
                        output.accept(createCustomItem(GunItem, "elitex:trs_bull", 5, FireMode.SEMI, false, "elitex.gun.trs_bull.name"));
                        output.accept(createCustomItem(GunItem, "elitex:dual_hr", 5, FireMode.SEMI, false, "elitex.gun.dual_hr.name"));
                        output.accept(createCustomItem(GunItem, "elitex:glock40", 18, FireMode.SEMI, true, "elitex.gun.glock40.name"));
                        output.accept(createCustomItem(GunItem, "elitex:glock40_normal", 18, FireMode.SEMI, true, "elitex.gun.glock40_normal.name"));


                        //Smgs
                        output.accept(createCustomItem(GunItem, "elitex:ro635v3", 32, FireMode.AUTO, true, "elitex.gun.ro635v3.name"));
                        output.accept(createCustomItem(GunItem, "elitex:mp5_classic", 30, FireMode.AUTO, true, "elitex.gun.mp5_classic.name"));
                        output.accept(createCustomItem(GunItem, "elitex:vector45acp", 35, FireMode.AUTO, true, "elitex.gun.vector45acp.name"));
                        output.accept(createCustomItem(GunItem, "elitex:mp7r", 30, FireMode.AUTO, true, "elitex.gun.mp7r.name"));



                        //Assault Rifles
                        output.accept(createCustomItem(GunItem, "elitex:ak47", 30, FireMode.AUTO, true, "elitex.gun.ak47.name"));
                        output.accept(createCustomItem(GunItem, "elitex:m4_sopmod", 35, FireMode.AUTO, true, "elitex.gun.m4_sopmod.name"));
                        output.accept(createCustomItem(GunItem, "elitex:mcs_spear", 20, FireMode.AUTO, true, "elitex.gun.mcs_spear.name"));
                        output.accept(createCustomItem(GunItem, "elitex:famas_f1", 25, FireMode.AUTO, true, "elitex.gun.famas_f1.name"));
                        output.accept(createCustomItem(GunItem, "elitex:akx105", 30, FireMode.AUTO, true, "elitex.gun.akx105.name"));
                        output.accept(createCustomItem(GunItem, "elitex:fh_scar18", 20, FireMode.SEMI, true, "elitex.gun.fh_scar18.name"));
                        output.accept(createCustomItem(GunItem, "elitex:fs_scar19", 30, FireMode.AUTO, true, "elitex.gun.fs_scar19.name"));
                        output.accept(createCustomItem(GunItem, "elitex:m4a1_classic", 30, FireMode.AUTO, true, "elitex.gun.m4a1_classic.name"));
                        output.accept(createCustomItem(GunItem, "elitex:m16", 30, FireMode.BURST, true, "elitex.gun.m16.name"));
                        output.accept(createCustomItem(GunItem, "elitex:golden_ak", 30, FireMode.AUTO, true, "elitex.gun.golden_ak.name"));
                        output.accept(createCustomItem(GunItem, "elitex:an94", 30, FireMode.AUTO, true, "elitex.gun.an94.name"));
                        output.accept(createCustomItem(GunItem, "elitex:g11", 50, FireMode.AUTO, false, "elitex.gun.g11.name"));



                        // Dmrs Mrrksman Rifles
                        output.accept(createCustomItem(GunItem, "elitex:sassy_dmr", 20, FireMode.SEMI, true, "elitex.gun.sassy_dmr.name"));



                        //Wonder Weapons
                        output.accept(createCustomItem(GunItem, "elitex:ray_gun_mk2", 21, FireMode.BURST, true, "elitex.gun.ray_gun_mk2.name"));
                        output.accept(createCustomItem(GunItem, "elitex:wonder_waffle", 3, FireMode.SEMI, true, "elitex.gun.wonder_waffle.name"));
                        output.accept(createCustomItem(GunItem, "elitex:mustang_and_sally", 12, FireMode.SEMI, true, "elitex.gun.mustang_and_sally.name"));
                        output.accept(createCustomItem(GunItem, "elitex:origin_electric_staff", 9, FireMode.SEMI, false, "elitex.gun.origin_electric_staff.name"));
                        output.accept(createCustomItem(GunItem, "elitex:origin_fire_staff", 10, FireMode.SEMI, false, "elitex.gun.origin_fire_staff.name"));
                        output.accept(createCustomItem(GunItem, "elitex:origin_ice_staff", 10, FireMode.SEMI, false, "elitex.gun.origin_ice_staff.name"));
                        output.accept(createCustomItem(GunItem, "elitex:origin_wind_staff", 10, FireMode.SEMI, false, "elitex.gun.origin_wind_staff.name"));


                        //Shotguns
                        output.accept(createCustomItem(GunItem, "elitex:benelli_super", 8, FireMode.SEMI, true, "elitex.gun.benelli_super.name"));
                        output.accept(createCustomItem(GunItem, "elitex:spaz12", 8, FireMode.SEMI, true, "elitex.gun.spaz12.name"));
                        output.accept(createCustomItem(GunItem, "elitex:eb800", 8, FireMode.SEMI, true, "elitex.gun.eb800.name"));


                        //Bolt Action Rifles
                        output.accept(createCustomItem(GunItem, "elitex:kar98k", 5, FireMode.SEMI, false, "elitex.gun.kar98k.name"));
                        output.accept(createCustomItem(GunItem, "elitex:m1_garand", 8, FireMode.SEMI, false, "elitex.gun.m1_garand.name"));
                        output.accept(createCustomItem(GunItem, "elitex:lee", 10, FireMode.SEMI, false, "elitex.gun.lee.name"));
                        output.accept(createCustomItem(GunItem, "elitex:sp_m1903", 5, FireMode.SEMI, false, "elitex.gun.sp_m1903.name"));


                        //Snipers
                        output.accept(createCustomItem(GunItem, "elitex:inter_m200", 7, FireMode.SEMI, true, "elitex.gun.inter_m200.name"));
                        output.accept(createCustomItem(GunItem, "elitex:psg1", 5, FireMode.SEMI, true, "elitex.gun.psg1.name"));

                        //Lmgs
                        output.accept(createCustomItem(GunItem, "elitex:m249x", 150, FireMode.AUTO, false, "elitex.gun.m249x.name"));
                        output.accept(createCustomItem(GunItem, "elitex:mgr338", 200, FireMode.AUTO, true, "elitex.gun.mgr338.name"));



                        // Later add normal items like this if I wanna add custom items for crafting purposes
                        // output.accept(ElitexItemRegistry.Custom_ITEM.get());

                    })
                    .build());

    private static ItemStack createCustomItem(Item baseItem, String gunId, int ammoCount, FireMode fireMode,
                                              boolean hasBulletInBarrel, String displayName) {
        ItemStack stack = new ItemStack(baseItem);

        // 1.21.1 stores item data in components. TaCZ's IGun API writes the exact same keys the
        // 1.20.1 NBT layout used (GunId, GunCurrentAmmoCount, GunFireMode, HasBulletInBarrel).
        IGun gun = IGun.getIGunOrNull(stack);
        if (gun != null) {
            gun.setGunId(stack, ResourceLocation.parse(gunId)); // Unique gun identifier
            gun.setCurrentAmmoCount(stack, ammoCount); // Current ammo count
            gun.setFireMode(stack, fireMode); // Fire mode (SEMI, AUTO, BURST)
            gun.setBulletInBarrel(stack, hasBulletInBarrel); // hidden round in the chamber
        }

        // Set the custom display name and disable italics
        stack.set(DataComponents.CUSTOM_NAME, Component.translatable(displayName).withStyle(style -> style.withItalic(false)));

        return stack;
    }
}
