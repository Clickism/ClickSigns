### ClickSigns 2.0 Beta 1 Release
(Wall of text incoming)

Version 2.0 has been completely rewritten from scratch.

ClickSigns is now fully dynamic, with no limitations, allowing you to create
much more complex and realistic signs than ever before.

Alongside these big changes, the code base is much cleaner, with proper
Forge (and in the future, NeoForge) support. Which means the mod will be easier
to maintain and update in the future. _(For now, only 1.20.1 is supported, but future versions are planned)_

The mod now allows for the creation of fully custom signs, with **no limitations** whatsoever, allowing you to create any sign you want.

This version is released as a *beta*, meaning there may be some bugs, or certain signs
that you make might become invalid/corrupted before the release. Also, currently
there aren't as many textures/symbols available as I want to add before the full release (Though even with this, there are much more textures than the previous version). Feel free to suggest symbols/textures or even make them yourself and let me know!
However, the mod is close to being feature complete, and the full release will likely come out soon!

### Overview
- The classic sign menu is still there, where you can easily edit the text on the sign. But now, you can also click on the symbols to change them or open the symbol menu and pick from there!
- The real powerhouse of the mod is the new **editor**. Click on the "Edit" button to open the editor. Here, you can place arbitrary elements (symbols, text, plates) anywhere on the sign, move them around, delete them, or update their properties. You can also change the textures of the sign, or its size to make it bigger or smaller.
- The UI is powered by my new UI-library [ClickUI](https://github.com/Clickism/ClickUI).

### Features / Changes
- Symbols are no longer tied to textures, allowing you to place any symbol anywhere on any sign.
- Text elements are now fully dynamic as well, allowing you to place them anywhere, change their size, style, and
  color.
  - Text elements now also support multiple lines! With additional support for line alignment and line spacing.
  - Text elements now support custom background colors, outlines with custom colors and thicknesses, and customizable padding.
- Added **tilesets**! Meaning you can create a texture once, and resize it to any size you want, and the texture will be automatically generated.
- Back textures now automatically generate and match the shape of the front textures, meaning a single texture can be used for many shapes (square signs, circular signs, rounded corners, etc.)
- Symbols, texts, etc. automatically change their color to match the color of the sign they are placed on.
- Added **plate elements**! Plate elements are used to attach smaller signs to
 a sign, allowing you to create more complex signs.
- Templates are now fully optional, and are a way to save the signs that you make.
 There are now two types of templates:
    - **Local Templates**: These are saved to your local machine and can be used in any world/server. (Stored in
      .minecraft/sign_templates)
  - **Resource Templates**: These are loaded from a resource pack, and are given by the resource pack creator (or are built in to the mod).
- You can still add custom textures using resource packs, but now you need to create **wayyy** fewer textures, as the symbols/arrows are added dynamically, and size variations are generated using tilesets.
- **Texture Pipelines:** The mod internally uses a texture pipeline to generate textures for the sign, for example tiling a texture to a given size, or replacing a color in an arrow to contrast with the sign's background. These tools are also available to you via the **Texture Pipeline Editor**, giving you infinite power to create practially any texture you want. To use the texture editor, click on the "Edit" button under a texture button.
  - Currently available processors are: **Tiler**, **Replace Color**, **Flip** and **Rotate**.
- Added many new textures/symbols:
  - Some EU signs (more to be added)
  - Street, highway, and rounded textures (with different styles) with more realistic colors.
  - Many new arrows, including connectors, flat arrows, roundabouts, curved arrows, and more.

