# Spawner Shards - Development Guide

For what the mod is and how it plays, see [README.md](README.md).

## Installation

Install server-side alongside its declared dependencies (see `fabric.mod.json`); connecting clients need only Pandorical. Version targets live in `gradle.properties` (Minecraft, loader, Fabric API) and `fabric.mod.json` (Java).

## Art

`generate_icon.py` and `generate_textures.py` cut the mod's icon and item sprite out of the vanilla jar. Both are deterministic; re-run either after a Minecraft version bump.
