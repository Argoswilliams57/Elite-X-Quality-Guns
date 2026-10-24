package com.argos_garcia.elitex.fabric.registry;

import com.argos_garcia.elitex.fabric.EliteXFabric;
import com.tacz.guns.init.ModItems;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/**
 * Fabric port of {@code com.argos_garcia.registeration_center.EliteXCreativeTabRegistry}.
 *
 * <p>The gun list, ammo counts, fire modes and bullet-in-barrel flags are copied
 * 1:1 from the Forge version. The NBT layout is produced with the same keys that
 * TaCZ uses on both loaders ({@code GunId}, {@code GunCurrentAmmoCount},
 * {@code GunFireMode}, {@code HasBulletInBarrel}); the keys were verified against
 * both TaCZ Forge 1.1.8 and TaCZ: Refabricated 0.7.1.</p>
 */
public final class EliteXFabricCreativeTab {

    public static ItemGroup ELITE_X_CREATIVE_TAB;

    private EliteXFabricCreativeTab() {
    }

    public static void register() {
        ELITE_X_CREATIVE_TAB = Registry.register(
                Registries.ITEM_GROUP,
                new Identifier(EliteXFabric.MOD_ID, "elitex"),
                FabricItemGroup.builder()
                        .displayName(Text.translatable("creative_tab.elitex"))
                        .icon(() -> new ItemStack(EliteXFabricItems.CREATIVE_ICON))
                        .entries((displayContext, entries) -> {

                            // Base item from TaCZ: Refabricated.
                            Item GunItem = ModItems.MODERN_KINETIC_GUN;

                            // Fire modes are "SEMI", "AUTO", "BURST".

                            // Pistols
                            entries.add(createCustomNBTItem(GunItem, "elitex:colt_m1911x1", 7, "SEMI", true, "elitex.gun.colt_m1911x1.name"));
                            entries.add(createCustomNBTItem(GunItem, "elitex:jrx", 20, "SEMI", true, "elitex.gun.jrx.name"));
                            entries.add(createCustomNBTItem(GunItem, "elitex:five_seven", 20, "SEMI", true, "elitex.gun.five_seven.name"));
                            entries.add(createCustomNBTItem(GunItem, "elitex:srz_2011", 16, "SEMI", true, "elitex.gun.srz_2011.name"));
                            entries.add(createCustomNBTItem(GunItem, "elitex:bo1_1911", 8, "SEMI", true, "elitex.gun.bo1_1911.name"));
                            entries.add(createCustomNBTItem(GunItem, "elitex:op1911", 7, "SEMI", true, "elitex.gun.op1911.name"));
                            entries.add(createCustomNBTItem(GunItem, "elitex:springfield_m1911", 7, "SEMI", true, "elitex.gun.springfield_m1911.name"));
                            entries.add(createCustomNBTItem(GunItem, "elitex:raygun_bo6", 20, "AUTO", false, "elitex.gun.raygun_bo6.name"));
                            entries.add(createCustomNBTItem(GunItem, "elitex:trs_bull", 5, "SEMI", false, "elitex.gun.trs_bull.name"));
                            entries.add(createCustomNBTItem(GunItem, "elitex:dual_hr", 5, "SEMI", false, "elitex.gun.dual_hr.name"));
                            entries.add(createCustomNBTItem(GunItem, "elitex:glock40", 18, "SEMI", true, "elitex.gun.glock40.name"));
                            entries.add(createCustomNBTItem(GunItem, "elitex:glock40_normal", 18, "SEMI", true, "elitex.gun.glock40_normal.name"));

                            // SMGs
                            entries.add(createCustomNBTItem(GunItem, "elitex:ro635v3", 32, "AUTO", true, "elitex.gun.ro635v3.name"));
                            entries.add(createCustomNBTItem(GunItem, "elitex:mp5_classic", 30, "AUTO", true, "elitex.gun.mp5_classic.name"));
                            entries.add(createCustomNBTItem(GunItem, "elitex:vector45acp", 35, "AUTO", true, "elitex.gun.vector45acp.name"));
                            entries.add(createCustomNBTItem(GunItem, "elitex:mp7r", 30, "AUTO", true, "elitex.gun.mp7r.name"));

                            // Assault Rifles
                            entries.add(createCustomNBTItem(GunItem, "elitex:ak47", 30, "AUTO", true, "elitex.gun.ak47.name"));
                            entries.add(createCustomNBTItem(GunItem, "elitex:m4_sopmod", 35, "AUTO", true, "elitex.gun.m4_sopmod.name"));
                            entries.add(createCustomNBTItem(GunItem, "elitex:mcs_spear", 20, "AUTO", true, "elitex.gun.mcs_spear.name"));
                            entries.add(createCustomNBTItem(GunItem, "elitex:famas_f1", 25, "AUTO", true, "elitex.gun.famas_f1.name"));
                            entries.add(createCustomNBTItem(GunItem, "elitex:akx105", 30, "AUTO", true, "elitex.gun.akx105.name"));
                            entries.add(createCustomNBTItem(GunItem, "elitex:fh_scar18", 20, "SEMI", true, "elitex.gun.fh_scar18.name"));
                            entries.add(createCustomNBTItem(GunItem, "elitex:fs_scar19", 30, "AUTO", true, "elitex.gun.fs_scar19.name"));
                            entries.add(createCustomNBTItem(GunItem, "elitex:m4a1_classic", 30, "AUTO", true, "elitex.gun.m4a1_classic.name"));
                            entries.add(createCustomNBTItem(GunItem, "elitex:m16", 30, "BURST", true, "elitex.gun.m16.name"));
                            entries.add(createCustomNBTItem(GunItem, "elitex:golden_ak", 30, "AUTO", true, "elitex.gun.golden_ak.name"));
                            entries.add(createCustomNBTItem(GunItem, "elitex:an94", 30, "AUTO", true, "elitex.gun.an94.name"));
                            entries.add(createCustomNBTItem(GunItem, "elitex:g11", 50, "AUTO", false, "elitex.gun.g11.name"));

                            // DMRs / Marksman Rifles
                            entries.add(createCustomNBTItem(GunItem, "elitex:sassy_dmr", 20, "SEMI", true, "elitex.gun.sassy_dmr.name"));

                            // Wonder Weapons
                            entries.add(createCustomNBTItem(GunItem, "elitex:ray_gun_mk2", 21, "BURST", true, "elitex.gun.ray_gun_mk2.name"));
                            entries.add(createCustomNBTItem(GunItem, "elitex:wonder_waffle", 3, "SEMI", true, "elitex.gun.wonder_waffle.name"));
                            entries.add(createCustomNBTItem(GunItem, "elitex:mustang_and_sally", 12, "SEMI", true, "elitex.gun.mustang_and_sally.name"));
                            entries.add(createCustomNBTItem(GunItem, "elitex:origin_electric_staff", 9, "SEMI", false, "elitex.gun.origin_electric_staff.name"));
                            entries.add(createCustomNBTItem(GunItem, "elitex:origin_fire_staff", 10, "SEMI", false, "elitex.gun.origin_fire_staff.name"));
                            entries.add(createCustomNBTItem(GunItem, "elitex:origin_ice_staff", 10, "SEMI", false, "elitex.gun.origin_ice_staff.name"));
                            entries.add(createCustomNBTItem(GunItem, "elitex:origin_wind_staff", 10, "SEMI", false, "elitex.gun.origin_wind_staff.name"));

                            // Shotguns
                            entries.add(createCustomNBTItem(GunItem, "elitex:benelli_super", 8, "SEMI", true, "elitex.gun.benelli_super.name"));
                            entries.add(createCustomNBTItem(GunItem, "elitex:spaz12", 8, "SEMI", true, "elitex.gun.spaz12.name"));
                            entries.add(createCustomNBTItem(GunItem, "elitex:eb800", 8, "SEMI", true, "elitex.gun.eb800.name"));

                            // Bolt Action Rifles
                            entries.add(createCustomNBTItem(GunItem, "elitex:kar98k", 5, "SEMI", false, "elitex.gun.kar98k.name"));
                            entries.add(createCustomNBTItem(GunItem, "elitex:m1_garand", 8, "SEMI", false, "elitex.gun.m1_garand.name"));
                            entries.add(createCustomNBTItem(GunItem, "elitex:lee", 10, "SEMI", false, "elitex.gun.lee.name"));
                            entries.add(createCustomNBTItem(GunItem, "elitex:sp_m1903", 5, "SEMI", false, "elitex.gun.sp_m1903.name"));

                            // Snipers
                            entries.add(createCustomNBTItem(GunItem, "elitex:inter_m200", 7, "SEMI", true, "elitex.gun.inter_m200.name"));
                            entries.add(createCustomNBTItem(GunItem, "elitex:psg1", 5, "SEMI", true, "elitex.gun.psg1.name"));

                            // LMGs
                            entries.add(createCustomNBTItem(GunItem, "elitex:m249x", 150, "AUTO", false, "elitex.gun.m249x.name"));
                            entries.add(createCustomNBTItem(GunItem, "elitex:mgr338", 200, "AUTO", true, "elitex.gun.mgr338.name"));
                        })
                        .build()
        );
    }

    /**
     * Builds a gun stack with the same NBT layout the Forge version writes.
     */
    private static ItemStack createCustomNBTItem(Item baseItem, String gunId, int ammoCount, String fireMode,
                                                 boolean hasBulletInBarrel, String displayName) {
        ItemStack stack = new ItemStack(baseItem);
        NbtCompound nbt = stack.getOrCreateNbt();

        nbt.putString("GunId", gunId); // Unique gun identifier
        nbt.putInt("GunCurrentAmmoCount", ammoCount); // Current ammo count
        nbt.putString("GunFireMode", fireMode); // Fire mode (SEMI, AUTO, BURST)
        nbt.putBoolean("HasBulletInBarrel", hasBulletInBarrel); // 1/0 in NBT

        stack.setNbt(nbt);

        // Set the custom display name and disable italics
        stack.setCustomName(Text.translatable(displayName).styled(style -> style.withItalic(false)));

        return stack;
    }
}
