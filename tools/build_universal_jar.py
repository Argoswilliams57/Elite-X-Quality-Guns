#!/usr/bin/env python3
"""Merge the reobfuscated Forge jar and the remapped Fabric jar into one universal jar.

Why this can work:
  * Forge classes are reobfuscated by ForgeGradle to SRG names and reference
    net.minecraftforge.*. They are only loaded by Forge.
  * Fabric classes are remapped by Loom to intermediary names and reference
    net.fabricmc.*. They are only loaded by Fabric.
  * Both class sets live in different packages, so they do not collide.
  * Both metadata files (META-INF/mods.toml and fabric.mod.json) coexist; each
    loader ignores the other loader's metadata.

The script refuses to build if the inputs disagree on any shared resource or if
a class path exists in both jars.

Usage: python3 tools/build_universal_jar.py
Output: dist/Elite X Quality Guns 6.0 - Universal 1.20.1.jar
"""

import re
import sys
import zipfile
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
DIST = ROOT / "dist"
OUTPUT = DIST / "Elite X Quality Guns 6.0 - Universal 1.20.1.jar"

FORGE_REQUIRED = "META-INF/mods.toml"
FABRIC_REQUIRED = "fabric.mod.json"
FORGE_CLASSES = (
    "com/argos_garcia/elitex/EliteXQualityGuns.class",
    "com/argos_garcia/registeration_center/EliteXCreativeTabRegistry.class",
    "com/argos_garcia/registeration_center/ElitexItemRegistry.class",
)
FABRIC_CLASSES = (
    "com/argos_garcia/elitex/fabric/EliteXFabric.class",
    "com/argos_garcia/elitex/fabric/registry/EliteXFabricCreativeTab.class",
    "com/argos_garcia/elitex/fabric/registry/EliteXFabricItems.class",
)


def find_jar(pattern, required_entry):
    candidates = sorted(ROOT.glob(pattern))
    matches = []
    for path in candidates:
        if path.suffix != ".jar" or "sources" in path.name:
            continue
        with zipfile.ZipFile(path) as jar:
            if required_entry in jar.namelist():
                matches.append(path)
    if len(matches) != 1:
        raise SystemExit(f"Expected one {pattern} containing {required_entry}, found {matches}")
    return matches[0]


def read_manifest(jar):
    text = jar.read("META-INF/MANIFEST.MF").decode("utf-8")
    result = {}
    current = None
    for line in text.splitlines():
        line = line.rstrip("\r")
        if line.startswith(" ") and current:
            result[current] += line[1:]
        elif ":" in line:
            key, value = line.split(":", 1)
            current = key.strip()
            result[current] = value.strip()
    return result


def build_manifest(fabric_manifest, forge_manifest):
    # Keep Fabric's attributes: Fabric-Mapping-Namespace tells Fabric Loader that
    # the classes are intermediary-remapped. Forge ignores the Fabric-* entries.
    merged = dict(fabric_manifest)
    # Forge 1.20.1 registers mixin configs through the JAR manifest "MixinConfigs"
    # attribute (Mixin's Forge platform agent reads it from each mod file), so
    # dropping it here would silently disable the Forge-side mixins in the universal jar.
    if forge_manifest.get("MixinConfigs"):
        merged["MixinConfigs"] = forge_manifest["MixinConfigs"]
    merged["Specification-Title"] = "Elite X Quality Guns"
    merged["Implementation-Title"] = "Elite X Quality Guns Universal"
    merged["Implementation-Vendor"] = "Argos Williams"
    merged["Universal-Jar"] = "forge+fabric"

    lines = ["Manifest-Version: 1.0"]
    for key, value in merged.items():
        if key != "Manifest-Version":
            lines.append(f"{key}: {value}")
    return ("\r\n".join(lines) + "\r\n\r\n").encode("utf-8")


def main():
    forge_jar = find_jar("forge/build/libs/*.jar", FORGE_REQUIRED)
    fabric_jar = find_jar("fabric/build/libs/*.jar", FABRIC_REQUIRED)
    print("Universal jar inputs:")
    print(f"  Forge : {forge_jar.relative_to(ROOT)}")
    print(f"  Fabric: {fabric_jar.relative_to(ROOT)}")

    with zipfile.ZipFile(forge_jar) as forge, zipfile.ZipFile(fabric_jar) as fabric:
        forge_entries = {i.filename: i for i in forge.infolist() if not i.is_dir()}
        fabric_entries = {i.filename: i for i in fabric.infolist() if not i.is_dir()}
        forge_names = set(forge_entries)
        fabric_names = set(fabric_entries)

        for name in (FORGE_REQUIRED, FABRIC_REQUIRED, *FORGE_CLASSES, *FABRIC_CLASSES):
            if name not in forge_names and name not in fabric_names:
                raise SystemExit(f"Missing required entry: {name}")

        conflicts = []
        for name in sorted(forge_names & fabric_names):
            if name.upper() == "META-INF/MANIFEST.MF":
                continue
            a, b = forge_entries[name], fabric_entries[name]
            if (a.CRC, a.file_size) != (b.CRC, b.file_size):
                conflicts.append(name)
        if conflicts:
            raise SystemExit("Conflicting shared entries: " + ", ".join(conflicts))

        shared_classes = sorted(n for n in (forge_names & fabric_names) if n.endswith(".class"))
        if shared_classes:
            raise SystemExit("Class paths exist in both jars: " + ", ".join(shared_classes))

        forge_bytes = forge.read(FORGE_CLASSES[0])
        fabric_bytes = fabric.read(FABRIC_CLASSES[0])
        forge_all = b"".join(forge.read(name) for name in FORGE_CLASSES)
        fabric_all = b"".join(fabric.read(name) for name in FABRIC_CLASSES)
        if b"net/minecraftforge" not in forge_bytes or not re.search(rb"m_\d+_", forge_all):
            raise SystemExit("Forge entry point does not look SRG-reobfuscated")
        if b"class_" not in fabric_all or b"net/minecraftforge" in fabric_all:
            raise SystemExit("Fabric entry point does not look intermediary-remapped")

        metadata = fabric.read(FABRIC_REQUIRED)
        if b'"id": "elitex"' not in metadata and b'"id":"elitex"' not in metadata:
            raise SystemExit("fabric.mod.json does not declare mod id elitex")

        DIST.mkdir(parents=True, exist_ok=True)
        if OUTPUT.exists():
            OUTPUT.unlink()

        with zipfile.ZipFile(OUTPUT, "w", zipfile.ZIP_DEFLATED, compresslevel=9) as out:
            out.writestr("META-INF/MANIFEST.MF", build_manifest(read_manifest(fabric), read_manifest(forge)))

            # Fabric first: assets are byte-identical in both jars, so this stores
            # exactly one copy of every shared file.
            for info in fabric.infolist():
                if info.is_dir() or info.filename.upper() == "META-INF/MANIFEST.MF":
                    continue
                out.writestr(info, fabric.read(info.filename))

            # Forge-only: Forge classes, mods.toml, mixins config, pack.mcmeta.
            for info in forge.infolist():
                if info.is_dir() or info.filename.upper() == "META-INF/MANIFEST.MF":
                    continue
                if info.filename in fabric_entries:
                    continue
                out.writestr(info, forge.read(info.filename))

    # Validate the merged result.
    with zipfile.ZipFile(OUTPUT) as merged:
        names = set(merged.namelist())
        for name in (FORGE_REQUIRED, FABRIC_REQUIRED, *FORGE_CLASSES, *FABRIC_CLASSES):
            if name not in names:
                raise SystemExit(f"Merged jar is missing {name}")
        bad = merged.testzip()
        if bad:
            raise SystemExit(f"Merged jar failed integrity check at {bad}")
        mods_toml = merged.read(FORGE_REQUIRED).decode("utf-8")
        if 'modId="elitex"' not in mods_toml:
            raise SystemExit("mods.toml does not declare modId elitex")
        if b"net/minecraftforge" not in merged.read(FORGE_CLASSES[0]):
            raise SystemExit("Merged Forge class lost its SRG references")
        if not any(b"class_" in merged.read(name) for name in FABRIC_CLASSES):
            raise SystemExit("Merged Fabric class lost its intermediary references")
        merged_manifest = read_manifest(merged)
        for mixin_config in merged_manifest.get("MixinConfigs", "").split(","):
            mixin_config = mixin_config.strip()
            if mixin_config and mixin_config not in names:
                raise SystemExit(f"Merged jar references missing mixin config {mixin_config}")
        if "MixinConfigs" not in merged_manifest:
            raise SystemExit("Merged jar lost the Forge MixinConfigs manifest attribute")
        print(f"  Shared files (single copy): {len(forge_names & fabric_names)}")
        print(f"  Forge-only entries:         {len(forge_names - fabric_names)}")
        print(f"  Fabric-only entries:        {len(fabric_names - forge_names)}")
        print(f"  Total entries:              {len(names)}")

    size_mb = OUTPUT.stat().st_size / (1024 * 1024)
    print(f"  Output: {OUTPUT.relative_to(ROOT)} ({size_mb:.1f} MB)")
    print("RESULT: universal jar built and validated")
    return 0


if __name__ == "__main__":
    sys.exit(main())
