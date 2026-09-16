# Spawner Shards

A Fabric mod that makes a spawner a thing you can take with you, in pieces.

## Features

- **Spawners break into shards**: 1-2 with any pickaxe, 2-4 with Silk Touch, and Fortune adds up to its level on top of the plain roll (1-3, 1-4, 1-5). Nine shards build a spawner back, so careless mining loses ground.
- **Silk Touch and Fortune buy different things**: they can never share a pickaxe, so they are a real choice. Silk Touch raises the floor and never leaves you with one shard; Fortune leaves the floor alone and raises the ceiling.
- **Crafted spawners start blank**: a spawner built from shards spawns nothing until you tell it what to spawn.
- **A mob head sets it**: right-click a spawner holding a skeleton skull, wither skeleton skull, zombie head, creeper head, piglin head or dragon head and the spawner takes that mob. This runs the same path a spawn egg does, so it works wherever an egg does and costs the head the same way. Sneak to place the head as a block instead.
- **A dragon spawner fires once**: the spawner is spent and blows apart the moment it produces its dragon. A dragon head buys one dragon where and when you want it, not a dragon farm.
- **The player head is left out**: it names a person, not a mob, and there is nothing behind it to spawn.

## Crafting

Nine Spawner Shards, filling a crafting table, make one Spawner.

## Pandorical

Spawner Shards registers its item model through Pandorical's content sync, and tells Pandorical clients that a right-click on a spawner may be the server's to answer. **Pandorical is required on the server.**

**The Pandorical mod must be installed client-side** to see the shards rendered with their texture. Without it the mod still works, but a connecting client sees an untextured item.

## Development

Installing and the art pipeline are in [DEVELOPMENT.md](DEVELOPMENT.md).

## License

MIT, see [LICENSE](LICENSE).
