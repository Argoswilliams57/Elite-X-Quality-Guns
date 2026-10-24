#!/usr/bin/env bash
# Build both loader modules of Elite X Quality Guns 6.0 for Minecraft 1.20.1,
# then collect the release files in dist/.
#
# One Gradle build drives both modules: :forge (ForgeGradle) and :fabric (Loom).
# JDK 17 is required; set JAVA17_HOME to override autodetection.

set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$ROOT"

JDK17="${JAVA17_HOME:-}"
if [[ -z "$JDK17" ]]; then
    for candidate in /usr/lib/jvm/java-17-openjdk-amd64 /usr/lib/jvm/temurin-17-jdk-amd64 "$JAVA_HOME"; do
        if [[ -n "$candidate" && -x "$candidate/bin/java" ]] &&
           "$candidate/bin/java" -version 2>&1 | grep -q 'version "17'; then
            JDK17="$candidate"
            break
        fi
    done
fi

if [[ -z "$JDK17" ]]; then
    echo "A JDK 17 is required. Set JAVA17_HOME=/path/to/jdk17 and re-run." >&2
    exit 1
fi

export JAVA_HOME="$JDK17"

echo "==> Building common + forge + fabric"
./gradlew build

FORGE_JAR="$ROOT/forge/build/libs/Elite X Quality Guns Forge-6.0.jar"
FABRIC_JAR="$ROOT/fabric/build/libs/Elite X Quality Guns Fabric-6.0.jar"
PACK_DIR="$ROOT/common/src/main/resources/assets/elitex/custom/elitex_quality_guns"
DIST="$ROOT/dist"

for jar in "$FORGE_JAR" "$FABRIC_JAR"; do
    if [[ ! -f "$jar" ]]; then
        echo "Expected build output missing: $jar" >&2
        exit 1
    fi
done

mkdir -p "$DIST"
cp -f "$FORGE_JAR"  "$DIST/Elite X Quality Guns 6.0 - Forge 1.20.1.jar"
cp -f "$FABRIC_JAR" "$DIST/Elite X Quality Guns 6.0 - Fabric 1.20.1.jar"
rm -f "$DIST/Elite X Quality Guns 6.0 - Universal Content Pack 1.20.1.zip"
( cd "$PACK_DIR" && zip -r -q -X "$DIST/Elite X Quality Guns 6.0 - Universal Content Pack 1.20.1.zip" . )

echo "==> Merging Forge + Fabric into the universal jar"
python3 tools/build_universal_jar.py

echo
echo "==> dist/"
ls -1 "$DIST"
