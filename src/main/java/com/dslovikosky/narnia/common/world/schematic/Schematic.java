package com.dslovikosky.narnia.common.world.schematic;

import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

public record Schematic(ResourceLocation id, int width, int height, int length, BlockState[] blocks, ListTag blockEntities, ListTag entities) {
}
