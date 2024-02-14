# Helper GUI mod for Minecraft

This client-side mod was created for **Minecraft 1.20.1** + **Fabric API 0.91.1+1.20.1**.

## Purpose

Displays the most useful parts of the debug menu without having to completely fill your screen with text.

This includes data such as your current coordinates, direction, biome, in-game time, light level and FPS.

## Preview

![](https://i.imgur.com/cP7nwRt.png)

## Configuring

The mod generates a config file `helper-gui.properties` that lets you configure where the info appears.

Property | Possible values | Meaning
---|---|---|
renderOnLeftSide | true / false | Should this info be rendered on the top left or on the top right?
screenBorderPaddingX | an integer | Distance in px from the left side (or right side) of the screen
screenBorderPaddingY | an integer | Distance in px from the top side of the screen

## Compiling

Run **gradlew build** on a terminal. A .jar file will be generated and placed in build/libs/.
