package com.helpergui;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelperguiRenderer implements HudRenderCallback {

    public static final Logger LOGGER = LoggerFactory.getLogger("helper-gui");

    // false = right side
    private boolean renderOnLeftSide = false;

    // distance from left side (or right side) of screen
    private int screenBorderPaddingX = 6;

    // distance from top of screen
    private int screenBorderPaddingY = 6;

    private boolean toggled = true;

    private Boolean previousShowDebugValue;

    public HelperguiRenderer() {
        this.loadConfigs();
    }

    private void loadConfigs() {
        try {
            final Path propertiesPath = FabricLoader.getInstance().getConfigDir().resolve("helper-gui.properties");

            if (Files.exists(propertiesPath)) {
                LOGGER.info("Helper-gui: loading properties from config file.");
                final Properties properties = new Properties();

                try (final InputStream is = Files.newInputStream(propertiesPath)) {
                    properties.load(is);
                }

                this.renderOnLeftSide = Boolean.parseBoolean(properties.getProperty("renderOnLeftSide", "false"));
                this.screenBorderPaddingX = Integer.parseInt(properties.getProperty("screenBorderPaddingX", "6"));
                this.screenBorderPaddingY = Integer.parseInt(properties.getProperty("screenBorderPaddingY", "6"));
            } else {
                LOGGER.info("Helper-gui: there is no config file. Using default values.");
                this.saveConfigs();
            }
        } catch (final IOException | NumberFormatException e) {
            LOGGER.error("Failed to load configs for helper-gui:", e);
        }
    }

    private void saveConfigs() {
        try {
            final Path propertiesPath = FabricLoader.getInstance().getConfigDir().resolve("helper-gui.properties");

            LOGGER.info("Helper-gui: saving properties to config file.");
            final Properties properties = new Properties();
            properties.setProperty("renderOnLeftSide", String.valueOf(this.renderOnLeftSide));
            properties.setProperty("screenBorderPaddingX", String.valueOf(this.screenBorderPaddingX));
            properties.setProperty("screenBorderPaddingY", String.valueOf(this.screenBorderPaddingY));

            try (final OutputStream os = Files.newOutputStream(propertiesPath)) {
                properties.store(os, "Configurations for the helper-gui mod.");
            }
        } catch (final IOException e) {
            LOGGER.error("Failed to save configs for helper-gui:", e);
        }
    }

    @Override
    public void onHudRender(final DrawContext drawContext, final float tickDelta) {
        final var client = MinecraftClient.getInstance();

        // toggle mod with F3
        if (Boolean.TRUE.equals(this.previousShowDebugValue) && !client.options.debugEnabled) {
            this.toggled = !this.toggled;
            LOGGER.info("Helper gui toggled to '{}'", this.toggled);
        }
        this.previousShowDebugValue = client.options.debugEnabled;

        if (client.options.hudHidden || client.options.debugEnabled || !this.toggled) {
            // this disables the mod entirely
            return;
        }

        this.drawText(drawContext, GuiData.getPlayerPosition(), 0, false);
        this.drawText(drawContext, GuiData.getPlayerDirection(), 1, false);
        this.drawText(drawContext, GuiData.getBiome(), 2, false);
        this.drawText(drawContext, GuiData.getIngameTime(), 3, false);
        this.drawText(drawContext, GuiData.getLight(), 4, false);
        this.drawText(drawContext, GuiData.getFps(), 5, true);
    }

    /**
     * Draws a single line of text.
     */
    private void drawText(final DrawContext drawContext, final Text text, final int row, final boolean isFinalRow) {
        if (text == null) {
            return;
        }

        final var renderer = MinecraftClient.getInstance().textRenderer;

        final int textPadding = 2;
        final int textHeight = 9;
        final int textWidth = renderer.getWidth(text);

        final int bgStartX, bgEndX;
        if (this.renderOnLeftSide) {
            bgStartX = this.screenBorderPaddingX;
            bgEndX = bgStartX + textWidth + textPadding * 2;
        } else {
            bgEndX = drawContext.getScaledWindowWidth() - this.screenBorderPaddingX;
            bgStartX = bgEndX - textWidth - textPadding * 2;
        }

        final int bgStartY = this.screenBorderPaddingY + row * (textHeight + textPadding);
        final int bgEndY = bgStartY + textHeight + textPadding + (isFinalRow ? 1 : 0);
        final int textStartX = bgStartX + textPadding;
        final int textStartY = bgStartY + textPadding;

        drawContext.fill(bgStartX, bgStartY, bgEndX, bgEndY, -1873784752);
        drawContext.drawTextWithShadow(renderer, text, textStartX, textStartY, Formatting.WHITE.getColorValue());
    }

}
