<div align="center">

# MinecraftForge
Modifications to the Minecraft base files to assist in compatibility between mods.
</div>

## What does this repository do?
This repository holds the code to the Forge Mod Loader (FML) and utilities to help make mods.

## How do I set it up?

### Prerequisites
- You must have a JDK installed, for this project preferably JDK 8.

Once you have those it's quite simple, clone this project in your IDE of choice. We recommend Intellij IDEA but Eclipse is fine.

- Import the project into Gradle
- Run the gradle `setup` task from the `forgegradle` tasks group
- Add the `projects` folder as another project in your IDE of choice. In Intellij IDEA this is through the plus sign in the Gradle tab
- Once you have completed all of these steps you're done! Happy modding :)

### How do I edit the game source and PR it?
Well since Forge uses patches it is a different process, but it is very easy!
Just run the `genForgePatches` task from the `forgegradle` task group.

## Notes
- When contributing, make sure to read the [Contribution Guide](.github/CONTRIBUTING.md)