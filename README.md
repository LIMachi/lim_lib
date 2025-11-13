Library to facilitate the development of mods for Minecraft

Main selling points:
- annotation based: declare messages, register blocks, items, entities... using @Register*
- multy loadder: built on top of Architectury so you can make mods for Fabric and NeoForge, also has a few helpers to lessen loader specific code
- custom config system: inspired by Forge's, simply annotate a public static field to make it a parameter of a config file that can be loadded on startup or reloaded
- and to power all of this, a custom annotation processor that works the same between loadders

Used in Analog Redstone Suite (set of redstone components to improve analog signal manipulations) and Dimensional Bags (WIP mod that adds custom transportable pocket dimensions)
