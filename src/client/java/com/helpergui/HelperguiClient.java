package com.helpergui;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelperguiClient implements ClientModInitializer {

	public static final Logger LOGGER = LoggerFactory.getLogger("helper-gui");

	@Override
	public void onInitializeClient() {
		HudRenderCallback.EVENT.register(new HudRenderCallback() {

			private final int screenBorderPadding = 6;

			private final int textHorizontalPadding = 2;

			private final int textVerticalPadding = 2;

			private boolean toggled = true;

			private Boolean previousShowDebugValue;

			@Override
			public void onHudRender(final DrawContext drawContext, final float tickDelta) {
				final var client = MinecraftClient.getInstance();

				// toggle mod with F3
				if (Boolean.TRUE.equals(this.previousShowDebugValue) && !client.options.debugEnabled) {
					this.toggled = !this.toggled;
					LOGGER.info("Helper gui toggled to '{}'", this.toggled);
				}
				this.previousShowDebugValue = client.options.debugEnabled;

				if (client.options.hudHidden || client.options.debugEnabled || !toggled) {
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

			private void drawText(final DrawContext drawContext, final Text text, final int row, final boolean isFinalRow) {
				if (text == null) {
					return;
				}

				final var renderer = MinecraftClient.getInstance().textRenderer;

				final int textHeight = 9;
				final int textWidth = renderer.getWidth(text);

				final int bgStartX = this.screenBorderPadding;
				final int bgEndX = bgStartX + textWidth + this.textHorizontalPadding * 2;
				final int bgStartY = this.screenBorderPadding + row * (textHeight + this.textVerticalPadding);
				final int bgEndY = bgStartY + textHeight + this.textVerticalPadding + (isFinalRow ? 1 : 0);

				final int textStartX = bgStartX + this.textHorizontalPadding;
				final int textStartY = bgStartY + this.textVerticalPadding;

				drawContext.fill(bgStartX, bgStartY, bgEndX, bgEndY, -1873784752);
				drawContext.drawTextWithShadow(renderer, text, textStartX, textStartY, Formatting.WHITE.getColorValue());
			}

		});
	}
}