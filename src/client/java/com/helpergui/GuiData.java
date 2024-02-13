package com.helpergui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.biome.Biome;

public final class GuiData {


    private static final Formatting titleColor = Formatting.GRAY;

    private static final Formatting normalColor = Formatting.WHITE;

    private static final long fullDayIngameTicks = 24000;

    private GuiData() {}

    public static Text getPlayerPosition() {
        final var client = MinecraftClient.getInstance();
        if (client.getCameraEntity() == null) {
            return null;
        }

        final Entity camera = client.getCameraEntity();
        final String output = "%sxyz: %s%.0f / %.0f / %.0f".formatted(
                titleColor,
                normalColor,
                camera.getX(),
                camera.getY(),
                camera.getZ()
        );
        return output != null ? Text.literal(output) : null;
    }

    public static Text getPlayerDirection() {
        final var client = MinecraftClient.getInstance();
        if (client.getCameraEntity() == null) {
            return null;
        }

        final Direction direction = client.getCameraEntity().getMovementDirection();
        final String output = "%sFacing: %s%s (%s)".formatted(
                titleColor,
                normalColor,
                direction,
                convertToAxis(direction)
        );
        return output != null ? Text.literal(output) : null;
    }

    public static Text getBiome() {
        final var client = MinecraftClient.getInstance();
        if (client.getCameraEntity() == null || client.world == null) {
            return null;
        }

        final BlockPos pos = client.getCameraEntity().getBlockPos();
        final String output = "%sBiome: %s%s".formatted(
                titleColor,
                normalColor,
                convertToReadableName(client.world.getBiome(pos))
        );
        return output != null ? Text.literal(output) : null;
    }

    public static Text getIngameTime() {
        final var client = MinecraftClient.getInstance();
        if (client.world == null) {
            return null;
        }

        final long dayIngameTicks = client.world.getTimeOfDay() % fullDayIngameTicks;
        final String output = "%sTime: %s%s (%s)".formatted(
                titleColor,
                normalColor,
                convertToReadableTime(dayIngameTicks),
                convertToDayOrNight(dayIngameTicks)
        );
        return output != null ? Text.literal(output) : null;
    }

    public static Text getLight() {
        final var client = MinecraftClient.getInstance();
        if (client.getCameraEntity() == null || client.world == null) {
            return null;
        }

        final BlockPos pos = client.getCameraEntity().getBlockPos();
        final String output = "%sLight: %s%d".formatted(
                titleColor,
                normalColor,
                client.world.getChunkManager().getLightingProvider().getLight(pos, 0)
        );
        return output != null ? Text.literal(output) : null;
    }

    public static Text getFps() {
        final var client = MinecraftClient.getInstance();

        final String output = "%sFps: %s%d".formatted(
                titleColor,
                normalColor,
                client.getCurrentFps()
        );
        return output != null ? Text.literal(output) : null;
    }

    private static String convertToAxis(final Direction direction) {
        return switch (direction) {
            case NORTH -> "-z";
            case SOUTH -> "+z";
            case WEST -> "-x";
            case EAST -> "+x";
            case UP -> "+y";
            case DOWN -> "-y";
        };
    }

    private static String convertToReadableName(final RegistryEntry<Biome> biome) {
        return biome.getKeyOrValue().map(biomeKey -> {
                final String[] location = biomeKey.getValue().toString().split(":");
                return location.length > 1 ? location[location.length - 1] : location[0];
            },
            unknownBiome -> "unknown?"
        );
    }

    // ticks are 0-23999. tick 0 is 06:00 AM.
    public static String convertToReadableTime(final long dayIngameTicks) {
        final long hour = (dayIngameTicks / 1000 + 6) % 24; // 23912 -> hour 23 -> add 6 and wrap around -> hour 5
        final double minutes = Math.floor((dayIngameTicks % 1000) * 0.06); // 23912 -> remainder 912 -> 91.2% of an hour -> minute 54
        return "%02d:%02.0f".formatted(hour, minutes);
    }

    public static String convertToDayOrNight(final long dayIngameTicks) {
        final boolean isNight = dayIngameTicks >= 13000 && dayIngameTicks <= 23000;
        return isNight ? "night" : "day";
    }

}
