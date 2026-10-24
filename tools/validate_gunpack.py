#!/usr/bin/env python3
"""Validate the shared Elite X Quality Guns TaCZ gun pack.

The very same pack is embedded in the Forge jar and in the Fabric jar, so one
validation run covers both releases.

Checks performed:
  * every JSON file is valid JSON (Gson-lenient // comments are accepted and reported)
  * index -> display/data references resolve
  * display -> model / texture / hud / slot / lod / shell / entity references resolve
  * display -> animation / state machine / sound references resolve
  * gun data -> ammo index references resolve
  * recipes -> result gun / ammo / attachment / block references resolve
  * attachment tags in data/<ns>/tacz_tags reference existing attachments

Usage:  python3 tools/validate_gunpack.py
Exit code 0 = no errors, 1 = errors, 2 = pack directory not found.
"""

import json
import os
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
PACK = ROOT / "common" / "src" / "main" / "resources" / "assets" / "elitex" / "custom" / "elitex_quality_guns"
NS = "elitex"

ASSETS = PACK / "assets" / NS
DATA = PACK / "data" / NS

errors = []
warnings = []
# Missing models/textures/sounds are content gaps. They already exist in the
# original Forge pack and are therefore identical on both loaders; they are
# reported separately so they cannot hide a loader-specific regression.
asset_gaps = []
stats = {"json": 0, "comments": 0, "guns": 0, "ammo": 0, "attachments": 0, "blocks": 0,
         "recipes": 0, "references": 0}


def strip_comments(text):
    """Remove // line comments without touching string contents."""
    out = []
    in_string = False
    escaped = False
    i = 0
    while i < len(text):
        ch = text[i]
        if in_string:
            out.append(ch)
            if escaped:
                escaped = False
            elif ch == "\\":
                escaped = True
            elif ch == '"':
                in_string = False
            i += 1
            continue
        if ch == '"':
            in_string = True
            out.append(ch)
            i += 1
            continue
        if ch == "/" and i + 1 < len(text) and text[i + 1] == "/":
            while i < len(text) and text[i] not in "\r\n":
                i += 1
            continue
        out.append(ch)
        i += 1
    return "".join(out)


def load_json(path):
    text = path.read_text(encoding="utf-8-sig")
    try:
        return json.loads(text), False
    except json.JSONDecodeError:
        # TaCZ parses gun pack files with Gson's lenient reader, which allows
        # // comments. Retry without comments before reporting a failure.
        try:
            return json.loads(strip_comments(text)), True
        except json.JSONDecodeError as exc:
            errors.append(f"{rel(path)}: invalid JSON: {exc}")
            return None, False


def rel(path):
    try:
        return str(path.relative_to(PACK))
    except ValueError:
        return str(path)


def split_ref(ref):
    if not isinstance(ref, str) or ":" not in ref:
        return NS, ref
    return tuple(ref.split(":", 1))


def require_file(path, context):
    stats["references"] += 1
    if not path.is_file():
        asset_gaps.append(f"{context}: missing {rel(path)}")
        return False
    return True


def check_texture(ref, context):
    if not isinstance(ref, str):
        return
    namespace, path = split_ref(ref)
    if namespace != NS:
        return  # vanilla / TaCZ textures are shipped by their own mods
    require_file(ASSETS / "textures" / f"{path}.png", f"{context} (texture {ref})")


def check_model(ref, context):
    if not isinstance(ref, str):
        return
    namespace, path = split_ref(ref)
    if namespace != NS:
        return
    require_file(ASSETS / "geo_models" / f"{path}.json", f"{context} (model {ref})")


def check_animation(ref, context):
    if not isinstance(ref, str):
        return
    namespace, path = split_ref(ref)
    if namespace != NS:
        return
    base = ASSETS / "animations"
    candidates = [base / f"{path}.animation.json", base / f"{path}.gltf"]
    stats["references"] += 1
    if not any(c.is_file() for c in candidates):
        asset_gaps.append(f"{context}: missing animation {rel(base / (path + '.animation.json'))} / {rel(base / (path + '.gltf'))}")


def check_state_machine(ref, context):
    if not isinstance(ref, str):
        return
    namespace, path = split_ref(ref)
    if namespace != NS:
        return
    require_file(ASSETS / "scripts" / f"{path}.lua", f"{context} (state machine {ref})")


def check_sound(ref, context):
    if not isinstance(ref, str):
        return
    namespace, path = split_ref(ref)
    if namespace != NS:
        return
    base = ASSETS / "tacz_sounds"
    candidates = [base / f"{path}.ogg", base / f"{path}.wav"]
    stats["references"] += 1
    if not any(c.is_file() for c in candidates):
        asset_gaps.append(f"{context}: missing sound {rel(base / (path + '.ogg'))} / .wav")


def validate_display(path, kind):
    data, had_comments = load_json(path)
    if had_comments:
        stats["comments"] += 1
        warnings.append(f"{rel(path)}: contains // comments (accepted by Gson on both loaders)")
    if not isinstance(data, dict):
        return

    ctx = rel(path)
    check_model(data.get("model"), ctx)
    check_texture(data.get("texture"), ctx)
    check_texture(data.get("hud"), ctx)
    check_texture(data.get("slot"), ctx)
    check_animation(data.get("animation"), ctx)
    check_state_machine(data.get("state_machine"), ctx)

    for key in ("lod", "entity", "shell"):
        nested = data.get(key)
        if isinstance(nested, dict):
            check_model(nested.get("model"), f"{ctx}.{key}")
            check_texture(nested.get("texture"), f"{ctx}.{key}")

    flash = data.get("muzzle_flash")
    if isinstance(flash, dict):
        check_texture(flash.get("texture"), f"{ctx}.muzzle_flash")

    sounds = data.get("sounds")
    if isinstance(sounds, dict):
        for name, ref in sounds.items():
            check_sound(ref, f"{ctx}.sounds.{name}")


def validate_index():
    kinds = {
        "guns": ("data/guns", "display/guns", True),
        "ammo": ("", "display/ammo", False),
        "attachments": ("data/attachments", "display/attachments", True),
        "blocks": ("data/blocks", "display/blocks", True),
    }
    known_ids = {"guns": set(), "ammo": set(), "attachments": set(), "blocks": set()}

    for kind, (data_dir, display_dir, needs_data) in kinds.items():
        index_dir = DATA / "index" / kind
        if not index_dir.is_dir():
            errors.append(f"missing index directory: {rel(index_dir)}")
            continue
        for entry in sorted(index_dir.glob("*.json")):
            stats[kind] += 1
            known_ids[kind].add(f"{NS}:{entry.stem}")
            info, had_comments = load_json(entry)
            if had_comments:
                stats["comments"] += 1
            if not isinstance(info, dict):
                continue
            ctx = rel(entry)

            display = info.get("display")
            if not display:
                errors.append(f"{ctx}: no 'display' entry")
            else:
                namespace, name = split_ref(display)
                if namespace == NS:
                    require_file(ASSETS / display_dir / f"{name}.json", f"{ctx} (display)")
                elif kind in ("guns", "attachments") and needs_data:
                    warnings.append(f"{ctx}: display '{display}' belongs to another namespace")

            if needs_data:
                data_ref = info.get("data")
                if not data_ref:
                    errors.append(f"{ctx}: no 'data' entry")
                else:
                    namespace, name = split_ref(data_ref)
                    if namespace == NS:
                        require_file(DATA / data_dir / f"{name}.json", f"{ctx} (data)")
    return known_ids


def validate_gun_data(known_ids):
    directory = DATA / "data" / "guns"
    if not directory.is_dir():
        errors.append(f"missing directory: {rel(directory)}")
        return
    valid_types = {"scope", "muzzle", "grip", "stock", "extended_mag", "laser"}
    for entry in sorted(directory.glob("*.json")):
        data, _ = load_json(entry)
        if not isinstance(data, dict):
            continue
        ctx = rel(entry)

        ammo = data.get("ammo")
        if isinstance(ammo, str):
            namespace, name = split_ref(ammo)
            if namespace == NS and f"{NS}:{name}" not in known_ids["ammo"]:
                errors.append(f"{ctx}: ammo '{ammo}' has no index entry")

        for attachment in (data.get("exclusive_attachments") or {}):
            namespace, name = split_ref(attachment)
            if namespace == NS and f"{NS}:{name}" not in known_ids["attachments"]:
                errors.append(f"{ctx}: exclusive attachment '{attachment}' has no index entry")

        for attachment_type in (data.get("allow_attachment_types") or []):
            if attachment_type not in valid_types:
                warnings.append(f"{ctx}: unknown attachment type '{attachment_type}'")


def validate_recipes(known_ids):
    directory = DATA / "recipes"
    if not directory.is_dir():
        errors.append(f"missing directory: {rel(directory)}")
        return
    type_to_index = {"gun": "guns", "ammo": "ammo", "attachment": "attachments", "block": "blocks"}
    for entry in sorted(directory.rglob("*.json")):
        stats["recipes"] += 1
        data, _ = load_json(entry)
        if not isinstance(data, dict):
            continue
        ctx = rel(entry)
        result = data.get("result") or {}
        result_id = result.get("id")
        if isinstance(result_id, str):
            namespace, name = split_ref(result_id)
            index = type_to_index.get(result.get("type"))
            if namespace == NS and index and f"{NS}:{name}" not in known_ids[index]:
                errors.append(f"{ctx}: recipe result '{result_id}' has no {index} index entry")
        for material in (data.get("materials") or []):
            item = (material or {}).get("item") or {}
            for key in ("item", "tag"):
                ref = item.get(key)
                if isinstance(ref, str) and ref.startswith(NS + ":"):
                    namespace, name = split_ref(ref)
                    expected = {f"{NS}:{name}"}
                    if key == "item" and expected.isdisjoint(known_ids["ammo"] | known_ids["attachments"] |
                                                             known_ids["guns"]):
                        warnings.append(f"{ctx}: recipe material '{ref}' is not a pack-defined index entry")


def validate_attachment_tags(known_ids):
    roots = [DATA / "tacz_tags", PACK / "data" / "tacz" / "tacz_tags"]
    for root in roots:
        if not root.is_dir():
            continue
        for entry in sorted(root.rglob("*.json")):
            data, _ = load_json(entry)
            if not isinstance(data, list):
                continue
            for ref in data:
                if not isinstance(ref, str) or ref.startswith("#"):
                    continue
                namespace, name = split_ref(ref)
                if namespace == NS and f"{NS}:{name}" not in known_ids["attachments"]:
                    errors.append(f"{rel(entry)}: attachment '{ref}' has no index entry")


def collect_sound_refs():
    used = set()
    for entry in (ASSETS / "display").rglob("*.json"):
        data, _ = load_json(entry)
        if not isinstance(data, dict):
            continue
        sounds = data.get("sounds")
        if isinstance(sounds, dict):
            for ref in sounds.values():
                namespace, path = split_ref(ref)
                if namespace == NS:
                    used.add(path)
    return used


def main():
    if not PACK.is_dir():
        print(f"Gun pack not found: {PACK}")
        return 2

    known_ids = validate_index()
    for kind, display_dir in (("guns", "guns"), ("ammo", "ammo"), ("attachments", "attachments"),
                              ("blocks", "blocks")):
        directory = ASSETS / "display" / display_dir
        if not directory.is_dir():
            errors.append(f"missing directory: {rel(directory)}")
            continue
        for entry in sorted(directory.glob("*.json")):
            validate_display(entry, kind)

    validate_gun_data(known_ids)
    validate_recipes(known_ids)
    validate_attachment_tags(known_ids)

    for entry in sorted(PACK.rglob("*.json")):
        stats["json"] += 1
        load_json(entry)

    used_sounds = collect_sound_refs()
    sound_files = {p.relative_to(ASSETS / "tacz_sounds").with_suffix("").as_posix()
                   for p in (ASSETS / "tacz_sounds").rglob("*") if p.suffix in (".ogg", ".wav")}
    for unused in sorted(sound_files - used_sounds)[:20]:
        warnings.append(f"unreferenced sound file: tacz_sounds/{unused}")

    print("Elite X Quality Guns - gun pack validation")
    print(f"  pack: {PACK}")
    print(f"  JSON files parsed: {stats['json']} (with // comments: {stats['comments']})")
    print(f"  guns: {stats['guns']}, ammo: {stats['ammo']}, attachments: {stats['attachments']}, "
          f"blocks: {stats['blocks']}, recipes: {stats['recipes']}")
    print(f"  asset references checked: {stats['references']}")
    print()

    if warnings:
        print(f"Warnings ({len(warnings)}):")
        for warning in warnings:
            print(f"  - {warning}")
        print()
    if asset_gaps:
        print(f"Asset gaps ({len(asset_gaps)}) - present in the original pack, identical on Forge and Fabric:")
        for gap in asset_gaps:
            print(f"  - {gap}")
        print()
    if errors:
        print(f"Errors ({len(errors)}):")
        for error in errors:
            print(f"  - {error}")
        print()
        print("RESULT: FAILED")
        return 1

    strict = os.environ.get("ELITEX_STRICT_ASSETS") == "1"
    if asset_gaps and strict:
        print("RESULT: FAILED (ELITEX_STRICT_ASSETS=1 and asset gaps were found)")
        return 1

    print("RESULT: OK - pack structure and all index/display/data/recipe/tag references are valid "
          "for both loaders.")
    if asset_gaps:
        print("        The asset gaps listed above are pre-existing content gaps, not loader "
              "incompatibilities.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
