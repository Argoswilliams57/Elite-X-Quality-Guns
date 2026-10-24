# Elite X Quality Guns 6.1 — Multiloader (NeoForge + Fabric, Minecraft 26.3)

This is a true multiloader repository: **one shared content module**, **two loader
modules**, and **one Gradle build** that produces both jars plus a merged universal
distribution jar. Minecraft **26.3** is the final target of this port.

```
Elite x Quality guns 6.0 Universal 26.3 Multiloader/
├── settings.gradle                  includes :common, :neoforge, :fabric
├── build.gradle                     root orchestration + universalJar task
├── gradle.properties                shared versions + dependency file ids
├── common/                          SHARED, loader-independent resources
│   └── src/main/resources/
│       ├── assets/elitex/custom/elitex_quality_guns/   full TaCZ gun pack (single source of truth)
│       ├── assets/elitex/lang, models, textures/        tab names + icon
│       ├── icon.png                                mod icon
│       └── Credits.txt
├── neoforge/                        NEOFORGE 26.3 module (ModDevGradle, Mojang names)
│   ├── build.gradle
│   └── src/main/
│       ├── java/com/argos_garcia/...          NeoForge entry point, config, tab, icon item, mixins
│       └── resources/META-INF/neoforge.mods.toml, elitex.mixins.json, pack.mcmeta
├── fabric/                          FABRIC 26.3 module (Fabric Loom, Mojang names)
│   ├── build.gradle
│   └── src/main/
│       ├── java/com/argos_garcia/elitex/fabric/   Fabric entry point, config, tab, items, mixins
│       └── resources/fabric.mod.json, elitex.fabric.mixins.json
├── gradle/universal/                wrapper metadata for the universal jar
└── dist/                            final release jars
```

Both loader modules add `common/src/main/resources` to their own source set, so the
pack exists **once on disk** and is embedded byte-identically in both jars.

Two files from the 1.21.1 build are intentionally kept but **no longer used**:
`fabric/mappings/tacz-render-fix.tiny` (26.3 needs no Yarn remapping) and
`fabric/libs/simplebedrockmodel-fabric-2.3.0.1+mc1.21.1.jar` (TaCZ 26.3 ships the
animation/mesh stack itself).

## Build

Requirements: **JDK 25**, the shipped Gradle **9.7.1** wrapper, and internet access
on the first build (the NeoForge/Fabric/Minecraft toolchains and the TaCZ jars are
downloaded).

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
| Universal | `dist/Elite X Quality Guns - Universal 26.3.jar` |

Development runs (the game directory defaults to the Gradle run folders):

```bash
./gradlew :neoforge:runClient
./gradlew :neoforge:runServer
./gradlew :fabric:runClient
./gradlew :fabric:runServer
```

> On a headless/virtual X display where EGL cannot create a context, the NeoForge
> early loading window aborts the client before mod loading. Set
> `earlyWindowControl = false` in `neoforge/run/config/fml.toml` (or run on a
> normal desktop session) to launch the client.

## Universal jar

`universalJar` does **not** merge class files. It builds a small wrapper jar whose
metadata points each loader at its own nested production jar:

* Fabric reads `META-INF/jars/elitex-fabric-26.3.jar` from the `jars` entry in
  `fabric.mod.json`.
* NeoForge reads `META-INF/jars/elitex-neoforge-26.3.jar` through
  `META-INF/jarjar/metadata.json`.

Each loader loads only its own nested mod and ignores the other loader's metadata,
so no class or resource is ever shared or duplicated on one classpath. `./gradlew build`
produces it automatically. Because 26.3 is unobfuscated, the wrapper nests the plain
Fabric `jar` task output (the 1.21.1 build nested `remapJar`, which no longer exists).

## What is shared and what is loader-specific

| Category | common | neoforge | fabric |
|---|---|---|---|
| Gun pack, meshes, textures, animations, sounds, Lua, recipes | yes | – | – |
| Language files, creative-icon model/texture, icon.png, Credits.txt | yes | – | – |
| Mod metadata | – | `neoforge.mods.toml` | `fabric.mod.json` |
| Entry point / registries | – | `EliteXQualityGuns`, `ElitexItemRegistry`, `EliteXCreativeTabRegistry` | `EliteXFabric`, `EliteXFabricItems`, `EliteXFabricCreativeTab` |
| Config (same TOML file and option names) | values only | `ModConfigSpec` (NeoForge) | `ForgeConfigSpec` via Forge Config API Port 26.3 |
| Creative tab (47 entries, same gun data) | values only | `IGun` API | `IGun` API |

The 47-entry tab list is duplicated **on purpose**: the values are identical, but the
registration APIs differ between NeoForge and Fabric.

## 26.3 port notes

Minecraft 26.1+ ships **unobfuscated** class names and runs on **Java 25**, which
removed the need for mappings and remapping on both loaders. What changed here:

* **Java / toolchain.** `java_version=25`, `options.release = 25`, mixin
  `compatibilityLevel` `JAVA_25`. No Yarn, no Parchment.
* **Fabric toolchain.** Loom 1.19 requires Gradle 9.8, so the project stays on the
  shipped Gradle 9.7.1 and uses Loom **1.18.4** through the no-remap plugin id
  `net.fabricmc.fabric-loom`. Dependencies use plain `implementation` (the official
  26.3 Fabric example mod does the same); the layered TaCZ renderer remap fix is no
  longer needed or applied.
* **Fabric sources use Mojang names** (`net.minecraft.world.item.ItemStack`,
  `net.minecraft.client.player.LocalPlayer`, `net.minecraft.resources.Identifier`, …)
  because 26.3 is not mapped to Yarn anymore. `ResourceLocation` itself was renamed
  to `Identifier`.
* **Items need their id.** `Item.Properties` now requires `setId(...)`; the Fabric
  icon item sets it explicitly and the NeoForge registry uses
  `DeferredRegister.Items#registerSimpleItem`.
* **Creative tabs.** Vanilla `CreativeModeTab.builder()` now takes a row/column.
  Fabric uses `FabricCreativeModeTab.builder()` (fabric-creative-tab-api-v1, the
  26.3 replacement for the old item-group API) and `output.accept(...)`.
  NeoForge keeps the patched no-arg `CreativeModeTab.builder()`.
* **Screen rendering.** `GuiGraphics`/`Screen#render` was replaced by
  `GuiGraphicsExtractor`/`Screen#extractRenderState`; text is drawn with
  `centeredText(...)`. `Minecraft#setScreen(Screen)` became
  `Minecraft#setScreenAndShow(Screen)`.
* **Configs.** NeoForge's `ModConfig.Type` enum is now
  `LOCAL/CLIENT/SYNCED/STARTUP`, so the old COMMON config is registered as
  `LOCAL` with the explicit file name `elitex-common` (file stays
  `config/elitex-common.toml`). On Fabric, Forge Config API Port 26.3 still exposes
  the Forge-style enum (`COMMON/CLIENT/SERVER`) plus the Forge-style
  `net.minecraftforge.common.ForgeConfigSpec` and the new
  `fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry` entry point.
* **FML environment.** `FMLEnvironment.dist` became `FMLEnvironment.getDist()`.
* **TaCZ API.** `LocalPlayerReload#doReload` was replaced by `reload()` +
  `reloadWithDisplay(...)`; the "aim during reload" cancel hook now injects into
  `triggerClientReloadAnimation(IGun, GunDisplayInstance, GunData, ItemStack)`,
  which still runs only after all reload preconditions passed. All other mixin
  targets (`ExplodeUtil#createExplosion`, `JsonDataManager#parseJson`,
  `CommonNetworkCache#parse`, `LivingEntityAim#aim`, `LivingEntityReload#reload`,
  `LocalPlayerAim#aim/tickAimingProgress`, `EntityKineticBullet#igniteBlock`) were
  re-checked against the 26.3 TaCZ jars and are unchanged.
* **Block support check.** `BlockState#isSolidRender(level, pos)` was split: the
  render flag now takes no arguments. The explosion fire pass uses
  `isCollisionShapeFullBlock(level, pos)`, which is the equivalent full-support test.
* **Pack metadata.** `pack.mcmeta` now uses `min_format`/`max_format`
  (resource 97 … data 121) instead of `pack_format`; 26.3 rejects the old field for
  formats above 81. `neoforge.mods.toml` uses `iconFile` instead of the deprecated
  `logoFile`.
* **Mesh loader.** Both 26.3 TaCZ builds integrate the mesh loader (they provide the
  `taczmeshloader` mod id and ship `cn/sh1rocu/tacz/compat/meshloader`). The
  standalone TacZ Mesh Loader jar from 1.21.1 is no longer a dependency, and EliteX's
  poly meshes are parsed by TaCZ's built-in loader (`[TacZMeshLoader] ... elitex:
  geo_models/...`).
* **No datagen run.** ModDevGradle 2.0.147 exposes the run types
  `client`, `clientData`, `serverData`, `gameTestServer`, `server` and `junit`;
  the old `data()` run type is gone. EliteX has no `DataProvider`/`GatherDataEvent`
  implementation (its recipes, tags and lang files are hand-written resources in
  `common/src/main/resources`), so the obsolete `data { data() }` run was removed
  instead of being renamed to `clientData`/`serverData`.
* **Poly-mesh shading.** TaCZ 26.3 renders `poly_mesh` geometry through GPU-baked
  `RenderPipeline`s that are built with the `NO_CARDINAL_LIGHTING` shader define and
  without the `Lighting` bind group. `core/entity` then reduces to
  `vertexColor = Color`, i.e. the baked flat face normals are ignored and weapons are
  lit only by texture + lightmap, which makes faceted surfaces and cylinders look
  smoothly shaded. (The 1.21.1 mesh loader drew through
  `RenderTypes.entityCutout(...)`, the vanilla entity shader **with** cardinal
  lighting.) `PolyMeshLightingMixin` (both loaders) drops that define and adds
  `BindGroupLayouts.LIGHTING` while TaCZ builds its pipelines, restoring per-face
  flat shading without disabling `MeshGpuBaking`. See
  "Mesh shading / lighting verification" below for the measured before/after and the
  remaining limitation.
* **Poly-mesh texel filtering.** The same GPU pass also binds `Sampler0` with
  `SamplerCache.getClampToEdge(FilterMode.LINEAR)`, i.e. bilinear min *and* mag.
  Vanilla entity render types instead bind the texture's own sampler, which for a
  texture without `.mcmeta` is `NEAREST/NEAREST`
  (`ReloadableTexture`: `contents.blur() ? LINEAR : NEAREST`). The 1.21.1 loader used
  those render types, so texels stayed sharp; with the GPU path the 256x256-512x512
  gun textures are magnified on screen and blurred, which reads as "smooth/rounded".
  `PolyMeshSamplerMixin` (both loaders) changes only the `FilterMode` argument of the
  `Sampler0` lookup inside `PolyMeshGpuRenderer#drawList` to `NEAREST`; lightmap,
  scope mask, UVs, mipmaps and the baking path are untouched.
* **Poly-mesh lightmap level.** `PolyMeshGpuRenderer#quantizeLight` rounds the packed
  block and sky light *down* to a multiple of `LIGHT_GRID = 4`, and that rounded value is
  both written into the baked vertices (`PolyMesh#writeRaw -> setLight`) and used as the
  bake cache key. Because the lightmap response is non-linear
  (`get_brightness(level) = level / (4 - 3 * level)` in `core/lightmap.fsh`), level 15
  maps to 1.0 but level 12 to 0.5, so a gun in open daylight (block 4 / sky 15) was baked
  as `(4, 12)` and every `poly_mesh` surface rendered at roughly half the brightness of
  the cube parts of the same gun. The 1.21.1 mesh loader baked the exact packed light
  (`PolyMesh#ensureUploaded -> setLight(packedLight)`, 8-entry per-light VBO cache) and
  TaCZ's own config text describes that reference behaviour ("Upstream TML uses 8
  unquantized levels; we quantize light to a coarse grid first"), while the collector
  path that 26.3 still uses for translucent bones writes the exact light as well.
  `PolyMeshLightingMixin` now also neutralises `quantizeLight` (it returns its argument), so the
  bake and its cache key carry the real packed light. GPU baking stays enabled; only the rebake
  frequency follows the real light again, exactly like upstream.
* **Poly-mesh attachment body type.** `BedrockAttachmentModel#submit` receives a render type that
  TaCZ already resolved for the attachment body - in first person
  `ScopeBodyRenderTypes.clipForViewmodel(entityCutout(texture), texture, firstPerson)`, i.e.
  `tacz:core/scope_body` + `SCOPE_MASK` while an aimed scope's ocular mask is active. That type is
  what keeps the scope housing out of the ocular aperture so the picture-in-picture world view
  shows through. 26.3's mesh loader ignored the argument for `poly_mesh` geometry and hardcoded
  `RenderTypes.entityCutout`, so every poly-bodied scope painted its housing over the lens and the
  PIP composite (the reticle still drew, because TaCZ clips the reticle itself). PSG-1 is the only
  EliteX scope whose geo has no `poly_mesh` at all (`attachment poly_mesh stats for
  elitex:geo_models/attachments/scopes/psg1_scope.json: 0 bones, 0 vertices` in the dev log), which
  is exactly why it alone looked right. The 1.21.1 loader handled this with
  `renderPolyMeshThroughStencilHole` (stencil `GL_NOTEQUAL 0`), i.e. clipping the poly body through
  the ocular hole; `PolyMeshAttachmentMixin` (both loaders) restores that through the 26.3 mask API
  by resolving the same type for the poly snapshot (`isScope()` attachments only, so sights keep
  the vanilla sight-channel behaviour).
* **Poly-mesh attachment normals.** `PolyMesh#compile` (the collector path used by every poly
  attachment) applies `Pose#normal()` by hand and never normalises, while vanilla 26.3
  `PoseStack.Pose#transformNormal` returns `trustedNormals ? result : result.normalize()` and
  `ModelPart.Cube` goes through it, and TaCZ's own GPU path writes unit baked normals. The 26.3
  `core/entity` shader does not renormalise (`minecraft_compute_light` is a raw `dot`, then
  `(dot0 + dot1) * 0.6 + 0.4` is clamped to [0,1]), so any scale in the pose chain acts as a
  brightness gain: 0.6 third-person/ground display scale = x1.67 normals (saturated highlights),
  1.2 fixed scale = x0.83 (dimmed), plus animation bone scale. `PolyMeshNormalMixin` (both loaders)
  normalises that one `setNormal` call; `PolyMesh#writeRaw` (GPU bake) is untouched and unscaled
  poses stay bit-identical.

## Dependencies

### NeoForge 26.3

| Mod | Version | Curse Maven |
|---|---|---|
| NeoForge | 26.3.0.95 | – |
| [UNOFFICIAL] TaCZ Renovated | 1.1.8+neoforge.26.3.R1 | `curse.maven:unofficial-tacz-renovated-1663324:9089853` |

TaCZ Renovated requires NeoForge `[26.3.0.51-beta,)`; EliteX declares the same range.

### Fabric 26.3

| Mod | Version | Curse Maven / Maven | Required |
|---|---|---|---|
| Fabric Loader | 0.19.5 | – | yes |
| Fabric API | 0.163.0+26.3 | – | yes |
| TaCZ: Refabricated | 1.1.8+fabric.26.3.R1-hotfix2 | `curse.maven:unofficial-tacz-refabricated-1627909:9282484` | yes |
| Forge Config API Port | 26.3.1 Fabric | `curse.maven:forge-config-api-port-fabric-547434:8938808` | yes (hard dependency of Refabricated) |
| Mod Menu | 21.0.0 | `com.terraformersmc:modmenu` | optional (config button) |

Both TaCZ 26.3 builds already contain the TacZ Mesh Loader; do **not** install the
old standalone mesh loader next to them.

The dependency file ids live in `gradle.properties`, so bumping a dependency only
requires editing that one line.

### Dev runtime notes (Fabric)

A Fabric **dev** launch does not unpack Jar-in-Jar libraries, so `fabric/build.gradle`
adds the same libraries TaCZ: Refabricated and Forge Config API Port ship in
`META-INF/jars/`:

* TaCZ: LuaJ (`luaj-core`/`luaj-jse`, which pulls bcel), `commons-math3`, `mae`.
* Forge Config API Port: NightConfig `core`/`toml` 3.8.4 (`compileOnly` + `runtimeOnly`;
  `ForgeConfigSpec` extends NightConfig's `UnmodifiableConfigWrapper`).

None of them are embedded in the released jar; production keeps using the copies the
other mods already ship.

## Installation

### NeoForge

Put in `mods/`:

* `Elite X Quality Guns NeoForge-6.1.jar` (or the universal jar)
* [UNOFFICIAL] TaCZ Renovated 1.1.8+neoforge.26.3.R1 (1663324 / 9089853)

The mod extracts the pack to `.minecraft/tacz/elitex_quality_guns/` automatically.

### Fabric

Put in `mods/`:

* `Elite X Quality Guns Fabric-6.1.jar` (or the universal jar)
* Fabric API 0.163.0+26.3 (or newer for 26.3)
* TaCZ: Refabricated 1.1.8+fabric.26.3.R1-hotfix2 (9282484)
* Forge Config API Port 26.3.1 Fabric (8938808)
* Mod Menu 21.0.0 (optional)

The pack is extracted automatically to `.minecraft/tacz/elitex_quality_guns/`.

### Universal jar

`Elite X Quality Guns - Universal 26.3.jar` can be used **instead of** either
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

Camera roll animations come from TaCZ's bundled animation/mesh stack, so the mod does
not intercept the renderer.

## Mesh shading / lighting verification

Measured with Minecraft 26.3 (Fabric), a fixed camera and `elitex:m4a1_classic` in
first person:

* Runtime values read from inside `PolyMesh.<init>`: `MeshPolyPreferPackNormals=false`,
  `MeshPolyInvertNormals=false`, `MeshPolyMirrorReverseWinding=false`,
  `MeshGpuBaking=true`, `MeshGpuWorld=true`. The baked normals are the expected flat
  per-face normals (four identical axis-aligned vectors per quad), so the pack's
  smooth `normals` arrays are **not** in use. The fabric config file was regenerated
  by the 26.3 TaCZ build and also contains `MeshPolyPreferPackNormals = false`.
* A model-wide check of all 48 EliteX geo files (198,385 quads, 797 poly meshes) found
  only 556 degenerate quads, so TaCZ's fallback to pack normals is negligible; 97.7 %
  of quads carry differing (i.e. smooth) pack normals that are ignored while the option
  is false.
* With the default GPU path (`MeshGpuBaking=true`) the gun renders smoothly/uniformly;
  with `MeshGpuBaking=false` (CPU collector + `RenderTypes.entityCutout`) the same
  frame shows the old faceted, per-face flat shading. Pixel comparison of a fixed
  crop: gun-region mean absolute difference ≈ 11/255 between the two paths while the
  background differs by ≈ 2.7/255.
* With `PolyMeshLightingMixin` active and `MeshGpuBaking=true`, the GPU path produces
  the faceted appearance again (screenshot compared against the collector render).
* Runtime probe of the pipelines built by `PolyMeshGpuRenderer.<clinit>` (temporary
  client entrypoint in a Fabric dev run): `LIT_PIPELINE` flags are `[NO_OVERLAY]`
  (no `NO_CARDINAL_LIGHTING`), `EMISSIVE_PIPELINE` `[EMISSIVE, NO_OVERLAY]`,
  `LIT_CLIPPED_PIPELINE` `[NO_OVERLAY, SCOPE_MASK]`; each pipeline lists a `Lighting`
  bind group, and `RenderSystem.getShaderLights()` was non-null when the probe ran.
  `drawListInto` sets the pipeline before `RenderSystem.bindDefaultUniforms`, so the
  `Lighting` block is bound. The same run logged
  `GPU mesh pass drew 9 bones (14730 indices) on the hand pass, lit=true`.
* Texel filtering: the GPU pass used `getClampToEdge(FilterMode.LINEAR)`. Vanilla
  `RenderSetup#prepareTextures` binds `AbstractTexture#getSampler()`, and
  `ReloadableTexture` derives `NEAREST` for textures whose metadata does not request
  blur; the pack ships no `.mcmeta` files at all (`find ... -name '*.mcmeta'` is
  empty), so the 1.21.1/collector appearance is NEAREST. A 2x zoom of the same frame
  showed hard texel edges through the collector path and interpolated, blurred
  detail through the GPU path; a 3x3 local-variance check of the difference image
  (2.58 vs. an overall difference std. dev. of 19.9) confirms the remaining
  difference is piecewise-constant per face, i.e. lighting, not texture noise.
* Lightmap level: `javap -c` of `PolyMeshGpuRenderer#quantizeLight` in both TaCZ 26.3 jars
  (Fabric `9282484`, NeoForge `9089853`) shows identical code - `block = min(15, max(0,
  packed >> 4 & 15))`, `sky = min(15, max(0, packed >>> 20 & 15))`, then `qb = block / 4 *
  4`, `qs = sky / 4 * 4`, `return LightCoordsUtil.pack(qb, qs)` - and
  `TaczPolyMeshGunModel` calls it twice (hand bake, world bake). The dev client logged
  `GPU-baked 13 bones (19608 vertices) for elitex:textures/gun/uv/spaz12.png at quantized
  light c00040`: sky 15 was baked as 12, which `core/lightmap.fsh` maps to 0.5 instead of
  1.0, i.e. the mesh was baked at half the sky light of the cube geometry in the same
  frame. The 1.21.1 reference has no such step (`PolyMesh#ensureUploaded` writes
  `setLight(packedLight)` verbatim; `PolyMeshModel#ensureAllUploaded` keys the VBO cache
  by that same exact value, 8 levels deep - the "8 unquantized levels" TaCZ's config
  comment refers to, and the identical 8-entry LRU that 26.3 uses for
  `MeshGpuLightCacheSize`). 26.3's own collector path (`PolyMeshSnapshot#writeCutout`)
  also writes the exact light, so translucent bones and opaque GPU bones of the same gun
  disagreed. `quantizeLight` is now intercepted, which is verified on the transformed
  class (see "Validation status").
* Attachment body type: a dev run logged `attachment poly_mesh stats` for every scope model and
  showed `psg1_scope.json: 0 bones, 0 vertices` against `hybrid_sight 1 bone / 6400 vertices`,
  `2x_green_dot_sight 1 / 11440`, `kobra_sight 2 / 2904`, `inter_m200_scope_geo 2 / 3536` (and
  `sassy_dmr`/`g11` are the same shape). PSG-1 is therefore the only EliteX optic that never takes
  the poly attachment path, which matches the reported "PSG-1 is the one that works". The same run
  drew `Ocular mask drawn: 192 indices from 8 batches`, so the mask itself and TaCZ's clipping of
  the cube path were healthy; only the poly housing was outside it. The transformed
  `TaczPolyMeshAttachmentModel#submit` (exported with `-Dmixin.debug.export=true`) now calls
  `redirect$bej000$elitex$polyAttachmentBodyType(Identifier)` where the `RenderTypes.entityCutout`
  call used to be, and that handler is `entityCutout -> ScopeBodyRenderTypes.clipForViewmodel(...)`.
* Attachment normals: `PolyMesh#compile` in both TaCZ jars transforms with `Pose#normal()` and calls
  `setNormal` with the raw result, whereas vanilla 26.3 `PoseStack.Pose#transformNormal`
  (`trustedNormals ? result : result.normalize()`) is what `ModelPart.Cube` uses; `core/entity`'s
  `minecraft_compute_light` has no `normalize()`. `PolyMesh#writeRaw` (GPU bake) already stores unit
  normals, so the weapon body and the attachment were being lit with different-length normals
  whenever the pose carries scale. The exported `PolyMesh` shows the wrapper bound on the
  `compile` call site only (`redirect$bek000$elitex$normalizeShadingNormal` at the `compile`
  `setNormal`, while `writeRaw`'s `setNormal` stays direct).

Remaining limitation (verified **not** to be a regression): TaCZ's GPU path stores normals
in bone-local space and does not rotate them with the bone pose or with the model-view
transform. The 1.21.1 reference behaves the same way in the path it actually used for the
hand: `TaczPolyMeshGunModel#render` sets `useVBO = !isGuiLike`, `PolyMesh#ensureUploaded`
bakes `.setNormal(bakedNX, bakedNY, bakedNZ)` and `drawVBO` only supplies
`modelView * bonePose` as `ModelViewMat`, so positions are posed but normals stay
bone-local (only the rarely used collector fallback, `PolyMesh#compileConsumer`, applies
`pose.normal()` - and it negates the normal as well). Because 26.3 writes the same
`(faceNx, -faceNy, faceNz)` values with the same sign and the same `Lighting` entry
(`Entry.LEVEL`, the only entry the 26.3 level render installs), the faceted light/dark
pattern of the GPU path matches 1.21.1 face for face. The difference that remains is
against vanilla **cube** geometry (`BedrockCubeBox#compile` multiplies each face normal by
`pose.normal()`), whose shading therefore follows the camera while poly meshes keep
theirs glued to the model - also true in 1.21.1. A fresh model-wide count of the 82
EliteX geo files that contain `poly_mesh` data (816 meshes, 839,188 quad vertices) puts
27.0 % of vertices (226,848) on bone chains with a non-zero rest rotation, and animated
bones deviate from the baked rest pose while moving; that share is lit as if unposed.
Making those normals view-space needs a vertex shader for the mesh pipelines that
transforms `Normal` by `ModelViewMat` (TaCZ's `DynamicTransforms` block has no normal
matrix and the baked buffers are static), i.e. a behavioural change away from the 1.21.1
look, so it was not applied here. `MeshGpuBaking=false` (CPU collector) remains the fully
posed reference if that exactness is ever required.

## Validation status

Verified during this port:

* `./gradlew :fabric:build` and `./gradlew :neoforge:build` succeed, as does
  `universalJar`; the three jars listed above are produced. `clean build` also
  succeeds after the 6.1 datagen/shading fixes, and both the loader jars and the
  embedded jars inside the universal jar contain `PolyMeshLightingMixin` and
  `PolyMeshSamplerMixin`.
* `:neoforge:tasks --all` now lists only `prepareClientRun`/`prepareServerRun`; the
  previously failing `prepareDataRun` task no longer exists.
* **Fabric client**: reaches the resource reload, sound engine and an in-game world;
  TaCZ's integrated mesh loader parses EliteX's models (`elitex:geo_models/...`).
  A verbose-mixin run confirmed that every EliteX mixin which became loadable was
  applied: `EntityKineticBulletAccessor`, `JsonDataManagerMixin`,
  `CommonNetworkCacheMixin`, `LivingEntityAimMixin`, `LivingEntityReloadMixin`,
  `LocalPlayerAimMixin`, the retargeted `LocalPlayerReloadMixin`,
  `PolyMeshLightingMixin` and `PolyMeshSamplerMixin`.
* **NeoForge client**: initialises, reloads resources, starts the sound engine,
  registers the mesh model type and applies `PolyMeshLightingMixin` (used Vulkan on
  this machine because EGL was unavailable; see the note under "Build").
  `PolyMeshSamplerMixin` targets `drawList`, which contains exactly one
  `SamplerCache.getClampToEdge(FilterMode)` call in the NeoForge TaCZ jar as well
  (checked with `javap`); it is the same injector that was runtime-verified on Fabric,
  but it has not been observed in a NeoForge session yet.
* **Shading**: see "Mesh shading / lighting verification" above - the GPU path with
  the mixins matches the faceted, crisp collector render.
* **Lightmap fix, runtime**: a Fabric dev run launched with
  `-Dmixin.debug.export=true` exported the transformed
  `cn/sh1rocu/tacz/compat/meshloader/render/PolyMeshGpuRenderer` to
  `fabric/run/.mixin.out/class`. `javap -c` on that class shows all four EliteX handlers
  bound: `redirect$zhf000$elitex$keepCardinalLighting` (9 references in `<clinit>`),
  `redirect$zhf000$elitex$addLightingLayout` (inserted before `SAMPLER0`/`SAMPLER2`/mask
  layouts), `modify$zhg000$elitex$keepTexelFiltering`, and
  `handler$zhf000$elitex$keepExactBakedLight`, which is now the first thing
  `quantizeLight` executes (`iload_0; aload cir; invokestatic handler$...; isCancelled ->
  ireturn`), so the rounding code at offset 32 is unreachable and the exact packed light
  reaches `bakeBone`. The exported tree was removed afterwards; no client was left
  running.
* **Attachment mixins, runtime**: the same export run also contains
  `cn/sh1rocu/tacz/compat/meshloader/model/TaczPolyMeshAttachmentModel.class` and
  `cn/sh1rocu/tacz/compat/meshloader/core/PolyMesh.class`, i.e. both classes were loaded and
  transformed at resource reload without an `InvalidInjectionException`
  (`defaultRequire: 1` would have failed loudly). `javap -c` shows the two redirects wired at the
  intended call sites (see "Mesh shading / lighting verification"); the run also reached an
  in-game world, drew the ocular mask and baked a poly weapon, so the attachment and weapon paths
  both executed. `PolyMeshAttachmentMixin` and `PolyMeshNormalMixin` are listed in both
  `elitex.fabric.mixins.json` and `elitex.mixins.json` and are present in both loader jars and in
  both nested jars of the universal jar.
* **Fabric dedicated server**: starts to `Done`, EliteX initialises, the pack is
  exported to `tacz/elitex_quality_guns` and discovered by TaCZ, all EliteX mixins
  apply.
* **NeoForge dedicated server**: starts to `Done`, `elitex-common.toml` is loaded,
  EliteX mixins and the item/creative-tab registries initialise, the pack is exported
  and discovered.
* **Fabric client**: reaches the resource reload, sound engine and an in-game world;
  TaCZ's integrated mesh loader parses EliteX's models (`elitex:geo_models/...`).
  A verbose-mixin run confirmed that every EliteX mixin which became loadable was
  applied: `EntityKineticBulletAccessor`, `JsonDataManagerMixin`,
  `CommonNetworkCacheMixin`, `LivingEntityAimMixin`, `LivingEntityReloadMixin`,
  `LocalPlayerAimMixin` and the retargeted `LocalPlayerReloadMixin`.
* **NeoForge client**: initialises, reloads resources, starts the sound engine and
  registers the mesh model type (used Vulkan on this machine because EGL was
  unavailable; see the note under "Build").

Not verified interactively: opening the creative tab and firing a gun in-game (no UI
automation available in this environment), so `ExplodeUtilMixin` (explosion fire
restoration) was not exercised at runtime; its injection target and descriptor were
re-checked against the 26.3 TaCZ jar. The content pack also logs two pre-existing
hygiene warnings from TaCZ's script/resource scanners for leftover `*_backup` files
(`sp_m1903_reload_logic_backup.lua`, `m1_garand_state_machine_BACKUP2.lua`); the live
scripts load correctly and these files are untouched.

