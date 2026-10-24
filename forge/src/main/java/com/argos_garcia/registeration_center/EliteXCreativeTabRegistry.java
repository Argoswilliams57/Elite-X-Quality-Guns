package com.argos_garcia.registeration_center;

import com.argos_garcia.elitex.EliteXQualityGuns;
import com.tacz.guns.init.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

//Author Enfield Garcia

public class EliteXCreativeTabRegistry {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create( Registries.CREATIVE_MODE_TAB, EliteXQualityGuns.MOD_ID);

    public static final RegistryObject<CreativeModeTab> ELITE_X_CREATIVE_TAB = CREATIVE_MODE_TABS.register("elitex",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ElitexItemRegistry.CREATIVE_ICON.get()))// Your custom icon
                    .title(Component.translatable("creative_tab.elitex")) // Custom tab title
                    .displayItems((parameters, output) -> {

                        // First you Get base item 'modern_kinetic_gun' from the main mod
                        Item GunItem = ModItems.MODERN_KINETIC_GUN.get();

                        // Add items with proper NBT data , Fire Modes are  "SEMI , AUTO , BURST"

                        // if I wanna Keep things organized and clean code then imma be dividing them into different categories to make sure they fit the same
                        //Pistols
                        output.accept(createCustomNBTItem(GunItem, "elitex:colt_m1911x1", 7, "SEMI", true, "elitex.gun.colt_m1911x1.name"));
                        output.accept(createCustomNBTItem(GunItem, "elitex:jrx", 20, "SEMI", true, "elitex.gun.jrx.name"));
                        output.accept(createCustomNBTItem(GunItem, "elitex:five_seven", 20, "SEMI", true, "elitex.gun.five_seven.name"));
                        output.accept(createCustomNBTItem(GunItem, "elitex:srz_2011", 16, "SEMI", true, "elitex.gun.srz_2011.name"));
                        output.accept(createCustomNBTItem(GunItem, "elitex:bo1_1911", 8, "SEMI", true, "elitex.gun.bo1_1911.name"));
                        output.accept(createCustomNBTItem(GunItem, "elitex:op1911", 7, "SEMI", true, "elitex.gun.op1911.name"));
                        output.accept(createCustomNBTItem(GunItem, "elitex:springfield_m1911", 7, "SEMI", true, "elitex.gun.springfield_m1911.name"));
                        output.accept(createCustomNBTItem(GunItem, "elitex:raygun_bo6", 20, "AUTO", false, "elitex.gun.raygun_bo6.name"));
                        output.accept(createCustomNBTItem(GunItem, "elitex:trs_bull", 5, "SEMI", false, "elitex.gun.trs_bull.name"));
                        output.accept(createCustomNBTItem(GunItem, "elitex:dual_hr", 5, "SEMI", false, "elitex.gun.dual_hr.name"));
                        output.accept(createCustomNBTItem(GunItem, "elitex:glock40", 18, "SEMI", true, "elitex.gun.glock40.name"));
                        output.accept(createCustomNBTItem(GunItem, "elitex:glock40_normal", 18, "SEMI", true, "elitex.gun.glock40_normal.name"));


                        //Smgs
                        output.accept(createCustomNBTItem(GunItem, "elitex:ro635v3", 32, "AUTO", true, "elitex.gun.ro635v3.name"));
                        output.accept(createCustomNBTItem(GunItem, "elitex:mp5_classic", 30, "AUTO", true, "elitex.gun.mp5_classic.name"));
                        output.accept(createCustomNBTItem(GunItem, "elitex:vector45acp", 35, "AUTO", true, "elitex.gun.vector45acp.name"));
                        output.accept(createCustomNBTItem(GunItem, "elitex:mp7r", 30, "AUTO", true, "elitex.gun.mp7r.name"));



                        //Assault Rifles
                        output.accept(createCustomNBTItem(GunItem, "elitex:ak47", 30, "AUTO", true, "elitex.gun.ak47.name"));
                        output.accept(createCustomNBTItem(GunItem, "elitex:m4_sopmod", 35, "AUTO", true, "elitex.gun.m4_sopmod.name"));
                        output.accept(createCustomNBTItem(GunItem, "elitex:mcs_spear", 20, "AUTO", true, "elitex.gun.mcs_spear.name"));
                        output.accept(createCustomNBTItem(GunItem, "elitex:famas_f1", 25, "AUTO", true, "elitex.gun.famas_f1.name"));
                        output.accept(createCustomNBTItem(GunItem, "elitex:akx105", 30, "AUTO", true, "elitex.gun.akx105.name"));
                        output.accept(createCustomNBTItem(GunItem, "elitex:fh_scar18", 20, "SEMI", true, "elitex.gun.fh_scar18.name"));
                        output.accept(createCustomNBTItem(GunItem, "elitex:fs_scar19", 30, "AUTO", true, "elitex.gun.fs_scar19.name"));
                        output.accept(createCustomNBTItem(GunItem, "elitex:m4a1_classic", 30, "AUTO", true, "elitex.gun.m4a1_classic.name"));
                        output.accept(createCustomNBTItem(GunItem, "elitex:m16", 30, "BURST", true, "elitex.gun.m16.name"));
                        output.accept(createCustomNBTItem(GunItem, "elitex:golden_ak", 30, "AUTO", true, "elitex.gun.golden_ak.name"));
                        output.accept(createCustomNBTItem(GunItem, "elitex:an94", 30, "AUTO", true, "elitex.gun.an94.name"));
                        output.accept(createCustomNBTItem(GunItem, "elitex:g11", 50, "AUTO", false, "elitex.gun.g11.name"));



                        // Dmrs Mrrksman Rifles
                        output.accept(createCustomNBTItem(GunItem, "elitex:sassy_dmr", 20, "SEMI", true, "elitex.gun.sassy_dmr.name"));



                        //Wonder Weapons
                        output.accept(createCustomNBTItem(GunItem, "elitex:ray_gun_mk2", 21, "BURST", true, "elitex.gun.ray_gun_mk2.name"));
                        output.accept(createCustomNBTItem(GunItem, "elitex:wonder_waffle", 3, "SEMI", true, "elitex.gun.wonder_waffle.name"));
                        output.accept(createCustomNBTItem(GunItem, "elitex:mustang_and_sally", 12, "SEMI", true, "elitex.gun.mustang_and_sally.name"));
                        output.accept(createCustomNBTItem(GunItem, "elitex:origin_electric_staff", 9, "SEMI", false, "elitex.gun.origin_electric_staff.name"));
                        output.accept(createCustomNBTItem(GunItem, "elitex:origin_fire_staff", 10, "SEMI", false, "elitex.gun.origin_fire_staff.name"));
                        output.accept(createCustomNBTItem(GunItem, "elitex:origin_ice_staff", 10, "SEMI", false, "elitex.gun.origin_ice_staff.name"));
                        output.accept(createCustomNBTItem(GunItem, "elitex:origin_wind_staff", 10, "SEMI", false, "elitex.gun.origin_wind_staff.name"));


                        //Shotguns
                        output.accept(createCustomNBTItem(GunItem, "elitex:benelli_super", 8, "SEMI", true, "elitex.gun.benelli_super.name"));
                        output.accept(createCustomNBTItem(GunItem, "elitex:spaz12", 8, "SEMI", true, "elitex.gun.spaz12.name"));
                        output.accept(createCustomNBTItem(GunItem, "elitex:eb800", 8, "SEMI", true, "elitex.gun.eb800.name"));


                        //Bolt Action Rifles
                        output.accept(createCustomNBTItem(GunItem, "elitex:kar98k", 5, "SEMI", false, "elitex.gun.kar98k.name"));
                        output.accept(createCustomNBTItem(GunItem, "elitex:m1_garand", 8, "SEMI", false, "elitex.gun.m1_garand.name"));
                        output.accept(createCustomNBTItem(GunItem, "elitex:lee", 10, "SEMI", false, "elitex.gun.lee.name"));
                        output.accept(createCustomNBTItem(GunItem, "elitex:sp_m1903", 5, "SEMI", false, "elitex.gun.sp_m1903.name"));


                        //Snipers
                        output.accept(createCustomNBTItem(GunItem, "elitex:inter_m200", 7, "SEMI", true, "elitex.gun.inter_m200.name"));
                        output.accept(createCustomNBTItem(GunItem, "elitex:psg1", 5, "SEMI", true, "elitex.gun.psg1.name"));

                        //Lmgs
                        output.accept(createCustomNBTItem(GunItem, "elitex:m249x", 150, "AUTO", false, "elitex.gun.m249x.name"));
                        output.accept(createCustomNBTItem(GunItem, "elitex:mgr338", 200, "AUTO", true, "elitex.gun.mgr338.name"));



                        // Later add normal items like this if I wanna add custom items for crafting purposes
                        // output.accept(ElitexItemRegistry.Custom_ITEM.get());

                    })
                    .build());



    private static ItemStack createCustomNBTItem(Item baseItem, String gunId, int ammoCount, String fireMode, boolean hasBulletInBarrel, String displayName) {
        ItemStack stack = new ItemStack(baseItem);
        CompoundTag tag = new CompoundTag();

        // Add all required NBT data
        tag.putString("GunId", gunId); // Unique gun identifier
        tag.putInt("GunCurrentAmmoCount", ammoCount); // Current ammo count
        tag.putString("GunFireMode", fireMode); // Fire mode (e.g., SEMI, AUTO , Burst)
        tag.putByte("HasBulletInBarrel", (byte) (hasBulletInBarrel ? 1 : 0)); // 1 for true, 0 for true

        stack.setTag(tag); // Attach the tag to the item stack

        // Set the custom display name and disable italics
        stack.setHoverName(Component.translatable(displayName).withStyle(style -> style.withItalic(false)));

        return stack;
    }


}


