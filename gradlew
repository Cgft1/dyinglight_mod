#!/usr/bin/env sh
set -eu
APP_HOME=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
GRADLE_VERSION=8.6
GRADLE_USER_HOME=${GRADLE_USER_HOME:-"$HOME/.gradle"}
DIST_DIR="$GRADLE_USER_HOME/manual-dists/gradle-$GRADLE_VERSION"
GRADLE_BIN="$DIST_DIR/gradle-$GRADLE_VERSION/bin/gradle"
if [ ! -x "$GRADLE_BIN" ]; then
  mkdir -p "$GRADLE_USER_HOME/manual-dists"
  TMP_ZIP="$GRADLE_USER_HOME/manual-dists/gradle-$GRADLE_VERSION.zip"
  curl -fL --retry 3 "https://services.gradle.org/distributions/gradle-$GRADLE_VERSION-bin.zip" -o "$TMP_ZIP"
  unzip -q -o "$TMP_ZIP" -d "$GRADLE_USER_HOME/manual-dists"
fi
exec "$GRADLE_BIN" -p "$APP_HOME" "$@"
