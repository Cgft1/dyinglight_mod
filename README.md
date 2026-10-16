# Dying Light Mod — Minecraft 1.20.1 Fabric

This repository contains the GitHub Actions build workflow for the current source archive.

## First setup

1. Download `DyingLightMod-1.20.1-Fabric-GitHub-ready.zip` from the ChatGPT conversation where it was generated.
2. Upload that ZIP to the root of this repository with the exact filename:
   `DyingLightMod-1.20.1-Fabric-GitHub-ready.zip`
3. Open the **Actions** tab.
4. Choose **Build Dying Light Fabric Mod** and click **Run workflow**.
5. When the workflow completes successfully, open the run and download the artifact named `dyinglight-fabric-1.20.1`.

The workflow extracts the ZIP, runs Gradle, and uploads the resulting JAR files as a GitHub Actions artifact.

## Important

The current source is an early prototype, not a complete recreation of Dying Light. The JAR is not considered tested until the GitHub Actions build succeeds and the mod is checked in Minecraft 1.20.1 with Fabric Loader.