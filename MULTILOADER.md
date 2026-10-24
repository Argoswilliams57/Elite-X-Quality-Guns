# Elite X Quality Guns 6.1 — Multiloader (NeoForge + Fabric, Minecraft 1.21.1)

This is a true multiloader repository: **one shared content module**, **two loader
modules**, and **one Gradle build** that produces both jars plus a merged universal
distribution jar.

```
Elite x Quality guns 6.0 Universal 1.21.1 Multiloader/
├── settings.gradle                  includes :common, :neoforge, :fabric
├── build.gradle                     root orchestration + universalJar task
├── gradle.properties                shared versions + dependency file ids
├── common/                          SHARED, loader-independent resources
│   └── src/main/resources/
│       ├── assets/elitex/custom/elitex_quality_guns/   full TaCZ gun pack (single source of truth)
│       ├── assets/elitex/lang, models, textures/        tab names + icon
│       ├── icon.png                                mod icon
│       └── Credits.txt
├── neoforge/                        NEOFORGE 1.21.1 module (ModDevGradle, Mojang mappings)
│   ├── build.gradle
│   └── src/main/
│       ├── java/com/argos_garcia/...          NeoForge entry point, config, tab, icon item, mixins
│       └── resources/META-INF/neoforge.mods.toml, elitex.mixins.json, pack.mcmeta
├── fabric/                          FABRIC 1.21.1 module (Fabric Loom, Yarn)
│   ├── build.gradle
│   ├── libs/simplebedrockmodel-fabric-2.3.0.1+mc1.21.1.jar   dev-runtime library (exact JiJ copy TaCZ ships)
│   ├── mappings/tacz-render-fix.tiny  tiny-remapper fix for TaCZ: Refabricated 0.7.1 (1.21.1)
│   └── src/main/
│       ├── java/com/argos_garcia/elitex/fabric/   Fabric entry point, config, tab, items, mixins
│       └── resources/fabric.mod.json, elitex.fabric.mixins.json
├── gradle/universal/                wrapper metadata for the universal jar
└── dist/                            final release jars
```

Both loader modules add `common/src/main/resources` to their own source set, so the
pack exists **once on disk** and is embedded byte-identically in both jars.

## Build

Requirements: **JDK 21**, the shipped Gradle **9.7.1** wrapper, and internet access
on the first build (the NeoForge/Fabric toolchains and the TaCZ jars are downloaded).

> If a Gradle daemon from an older wrapper is still running, stop it once with
> `./gradlew --stop` after switching versions.

```bash
./gradlew build             # common + neoforge + fabric + universal jar
./gradlew :neoforge:build   # NeoForge jar only
./gradlew :fabric:build     # Fabric jar only
./gradlew universalJar      # universal wrapper only (depends on both builds)
./gradlew clean
```

Outputs:

| Artifact | Path |
|---|---|
| NeoForge | `neoforge/build/libs/Elite X Quality Guns NeoForge-6.1.jar` |
| Fabric | `fabric/build/libs/Elite X Quality Guns Fabric-6.1.jar` |
| Universal | `dist/Elite X Quality Guns - Universal 1.21.1.jar` |

Development runs (the game directory defaults to the Gradle run folders):

```bash
./gradlew :neoforge:runClient
./gradlew :neoforge:runServer
./gradlew :fabric:runClient
./gradlew :fabric:runServer
```

## Universal jar

`universalJar` does **not** merge class files. It builds a small wrapper jar whose
metadata points each loader at its own nested production jar:

* Fabric reads `META-INF/jars/elitex-fabric-1.21.1.jar` from the `jars` entry in
  `fabric.mod.json`.
* NeoForge reads `META-INF/jars/elitex-neoforge-1.21.1.jar` through
  `META-INF/jarjar/metadata.json`.

Each loader loads only its own nested mod and ignores the other loader's metadata,
so no class or resource is ever shared or duplicated on one classpath. This replaces
the old Python merge script; `./gradlew build` produces it automatically.

## What is shared and what is loader-specific

| Category | common | neoforge | fabric |
|---|---|---|---|
| Gun pack, meshes, textures, animations, sounds, Lua, recipes | yes | – | – |
| Language files, creative-icon model/texture, icon.png, Credits.txt | yes | – | – |
| Mod metadata | – | `neoforge.mods.toml` | `fabric.mod.json` |
| Entry point / registries | – | `EliteXQualityGuns`, `ElitexItemRegistry`, `EliteXCreativeTabRegistry` | `EliteXFabric`, `EliteXFabricItems`, `EliteXFabricCreativeTab` |
| Config (same TOML file and option names) | values only | `ModConfigSpec` (NeoForge) | `ModConfigSpec` via Forge Config API Port |
| Creative tab (47 entries, same gun data) | values only | `IGun` API | `IGun` API |

The 47-entry tab list is duplicated **on purpose**: the values are identical, but the
registration APIs differ completely between NeoForge and Fabric.

## 1.21.1 port notes

Minecraft 1.21.1 changed several APIs the 1.20.1 build used. What changed here:

* **`ItemStack` NBT was removed.** `CompoundTag` / `setTag()` no longer exist. The
  creative tab now calls TaCZ's own `IGun` API (`setGunId`, `setFireMode`,
  `setCurrentAmmoCount`, `setBulletInBarrel`), which writes the same four values into
  the item's data components. TaCZ itself builds its creative-tab stacks the same way.
  The custom name is set with `CUSTOM_NAME` on `DataComponents` / `DataComponentTypes`.
* **Config API.** Both loaders use `net.neoforged.neoforge.common.ModConfigSpec`.
  `ModConfig.save()` no longer exists; the screen saves through `SPEC.save()`.
  Fabric registers the identical spec with Forge Config API Port's
  `NeoForgeConfigRegistry`.
* **NeoForge entry point.** `IEventBus`/`ModContainer` constructor injection,
  `ModContainer#registerConfig`, `IConfigScreenFactory` for the config button, and
  `[[mixins]]` in `neoforge.mods.toml` (no manifest `MixinConfigs` needed).
* **Mixin level.** `compatibilityLevel` is `JAVA_21`. Every mixin targets a TaCZ
  class with `remap = false`, so no refmap is needed on either loader (NeoForge
  1.21.1 also runs on Mojang mappings). The AP-generated refmap was removed.
* **Camera animation fix (Fabric) is no longer needed.** The 1.20.1 build had to
  re-apply TaCZ's camera roll in `GameRenderer` because Refabricated did not apply
  it. The 1.21.1 SimpleBedrockModel 2.3.0.1 that TaCZ 0.7.1 embeds already patches
  `Camera#setRotation` and applies the `ViewportEvent.CAMERA` roll to the camera
  quaternion itself, so keeping the old bridge would apply the roll twice.
  `EliteXCameraAnimation` and `GameRendererMixin` were therefore dropped.
* **TaCZ renderer remap fix (Fabric).** TaCZ: Refabricated 0.7.1 ships renderer
  classes that both declare an own `render` method and override the vanilla one.
  Remapping both to Yarn's `render` would create a duplicate method, so the layered
  `fabric/mappings/tacz-render-fix.tiny` renames TaCZ's own method to `render$tacz`.
  Without it Loom fails with "Mapping target name conflicts detected".
* **Pack format.** The NeoForge `pack.mcmeta` uses format 34 (1.21.1).

## Dependencies

### NeoForge 1.21.1

| Mod | Version | Curse Maven |
|---|---|---|
| NeoForge | 21.1.251 | – |
| [UNOFFICIAL] TaCZ NeoForge Port | 1.1.8-hotfix-r7 | `curse.maven:tacz-1-21-1-1353462:9027337` |
| TacZ Mesh Loader | 0.1.8.1 NeoForge | `curse.maven:tacz-mesh-loader-1549522:9296540` |

### Fabric 1.21.1

| Mod | Version | Curse Maven / Maven | Required |
|---|---|---|---|
| Fabric Loader | 0.19.3 (min 0.17.2) | – | yes |
| Fabric API | 0.116.17+1.21.1 | – | yes |
| TaCZ: Refabricated | 0.7.0-forge1.1.8-hotfix | `curse.maven:tacz-refabricated-1334246:8153698` | yes |
| Forge Config API Port | 21.1.6 Fabric | `curse.maven:forge-config-api-port-fabric-547434:7213608` | yes (hard dependency of Refabricated) |
| TacZ Mesh Loader | 0.1.8 Fabric | `curse.maven:tacz-mesh-loader-1549522:9014986` | client, strongly recommended |
| Mod Menu | 11.0.3 | `com.terraformersmc:modmenu` | optional (config button) |

> **Do not use TaCZ: Refabricated 0.7.1 while TacZ Mesh Loader is installed.**
> 0.7.1 removed `cn.sh1rocu.tacz.api.event.RenderTickEvent`; mesh loader 0.1.8's
> `ShaderStateTracker` links against it and crashes during `onInitializeClient`
> with `NoClassDefFoundError: cn/sh1rocu/tacz/api/event/RenderTickEvent`.
> 0.7.0 (file 8153698) still ships the event and is API-identical for everything
> this mod uses, so the Fabric build is pinned to it. The NeoForge side is not
> affected: its mesh loader build does not use the Refabricated client events.

The dependency file ids live in `gradle.properties`, so bumping a dependency only
requires editing that one line.

### Dev runtime notes (Fabric)

A Fabric **dev** launch does not unpack Jar-in-Jar libraries, so `fabric/build.gradle`
adds the same libraries TaCZ: Refabricated and Forge Config API Port ship in
`META-INF/jars/`:

* TaCZ: LuaJ (`luaj-core`/`luaj-jse`, which pulls bcel), `commons-math3`,
  Cardinal Components 6.1.2, SimpleBedrockModel + `mae`.
* Forge Config API Port: NightConfig `core`/`toml` 3.8.0. Without these two, startup
  fails with `NoClassDefFoundError: com/electronwill/nightconfig/core/ConfigFormat`
  from both TaCZ's `PreLoadConfig` and this mod's `ModConfigSpec`.

None of them are embedded in the released jar; production keeps using the copies the
other mods already ship.

## Installation

### NeoForge

Put in `mods/`:

* `Elite X Quality Guns NeoForge-6.1.jar` (or the universal jar)
* TaCZ NeoForge Port 1.1.8-hotfix-r7 (1353462)
* TacZ Mesh Loader 0.1.8.1 NeoForge (1549522, client only)

The mod extracts the pack to `.minecraft/tacz/elitex_quality_guns/` automatically.

### Fabric

Put in `mods/`:

* `Elite X Quality Guns Fabric-6.1.jar` (or the universal jar)
* Fabric API 0.116.17+1.21.1 (or newer for 1.21.1)
* TaCZ: Refabricated 0.7.0-forge1.1.8-hotfix (8153698)
* Forge Config API Port 21.1.6 Fabric (7213608)
* TacZ Mesh Loader 0.1.8 Fabric (9014986, client only)
* Mod Menu 11.0.3 (optional)

The pack is extracted automatically to `.minecraft/tacz/elitex_quality_guns/`.

### Universal jar

`Elite X Quality Guns - Universal 1.21.1.jar` can be used **instead of** either
loader-specific jar: install it with the NeoForge dependency set on a NeoForge
instance, or with the Fabric dependency set on a Fabric instance. Never install it
together with the loader-specific jar (same mod id), and never install the content
pack ZIP together with any mod jar (same pack namespace).

### Optional standalone pack (either loader)

Drop a ZIP that contains only the pack (`gunpack.meta.json` at ZIP root) into
`.minecraft/tacz/`.

## Configuration

`config/elitex-common.toml` (same file, same option name on both loaders):

```toml
[gameplay]
    # Default for aiming (ADS) while a gun is reloading.
    # Only used by guns whose gun JSON has no "allow_aim_during_reload" entry.
    allowAimingDuringReload = true
```

A gun JSON value (`allow_aim_during_reload`, legacy alias
`allow_aiming_during_reload`) always overrides the global option. The option can be
changed in-game: NeoForge adds a button on the Mods screen, Fabric through Mod Menu.
The screen writes the spec and saves the TOML file immediately.

## Behavioural features ported unchanged

* `allow_aim_during_reload` per gun (captured while TaCZ parses gun data, plus the
  network cache path for dedicated servers).
* Client-side and server-side enforcement, including cancelling an ADS that was
  already active when the reload starts.
* Explosion fire restoration for ammo with `ignite.block` (respects TaCZ's
  `IGNITE_BLOCK` / `EXPLOSIVE_AMMO_FIRE` options).

Camera roll animations now come from TaCZ's bundled SimpleBedrockModel (it applies
the `ViewportEvent.CAMERA` roll in `Camera#setRotation`), so the mod does not
intercept the renderer.

All TaCZ mixin targets were re-checked against the 1.21.1 TaCZ builds
(`EntityKineticBullet#igniteBlock`, `ExplodeUtil#createExplosion`,
`JsonDataManager#parseJson`, `CommonNetworkCache#parse`, `LivingEntityAim#aim`,
`LivingEntityReload#reload`, `LocalPlayerAim#aim/tickAimingProgress`,
`LocalPlayerReload#doReload`) and are unchanged.

## Validation status

`./gradlew build` compiles both modules and produces the three jars listed above.
The jars have **not** been launched in a running 1.21.1 client/server as part of this
port, so the first in-game check should cover: pack extraction, the creative tab's 47
entries, ADS-during-reload behaviour and the explosion fire pass.

