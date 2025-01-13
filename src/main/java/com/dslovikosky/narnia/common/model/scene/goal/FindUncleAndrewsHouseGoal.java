package com.dslovikosky.narnia.common.model.scene.goal;

import com.dslovikosky.narnia.common.constants.ModDataComponentTypes;
import com.dslovikosky.narnia.common.constants.ModStructureTypes;
import com.dslovikosky.narnia.common.model.scene.GoalTickResult;
import com.dslovikosky.narnia.common.model.scene.Scene;
import com.dslovikosky.narnia.common.model.scene.goal.base.ChapterGoal;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;

import java.util.List;

public class FindUncleAndrewsHouseGoal extends ChapterGoal {
    private static final ResourceKey<Structure> UNCLE_ANDREWS_HOUSE = ResourceKey.create(Registries.STRUCTURE, ModStructureTypes.UNCLE_ANDREWS_HOUSE.getId());

    @Override
    public boolean start(final Scene scene, final Level level) {
        if (level.isClientSide()) {
            return false;
        }

        if (level.dimension() != Level.OVERWORLD) {
            return false;
        }

        final ServerLevel overworld = (ServerLevel) level;
        final ChunkGenerator generator = overworld.getChunkSource().getGenerator();
        final Holder.Reference<Structure> uncleAndrewsHouseReference = overworld.registryAccess()
                .registryOrThrow(Registries.STRUCTURE)
                .getHolderOrThrow(UNCLE_ANDREWS_HOUSE);

        final Pair<BlockPos, Holder<Structure>> nearestHouse = generator.findNearestMapStructure(
                overworld, HolderSet.direct(uncleAndrewsHouseReference), BlockPos.ZERO, 100, false);
        if (nearestHouse == null) {
            return false;
        }

        final ChunkAccess chunk = level.getChunk(nearestHouse.getFirst());
        final StructureStart houseStart = chunk.getStartForStructure(nearestHouse.getSecond().value());

        if (houseStart == null || houseStart.getPieces().isEmpty()) {
            return false;
        }

        final Direction direction = houseStart.getPieces().getFirst().getOrientation();
        scene.set(ModDataComponentTypes.UNCLE_ANDREWS_HOUSE_BB, houseStart.getBoundingBox());
        scene.set(ModDataComponentTypes.UNCLE_ANDREWS_HOUSE_DIRECTION, direction);
        return true;
    }

    @Override
    public GoalTickResult tick(final Scene scene, final Level level) {
        final List<Player> players = scene.getChapter().getPlayers(scene, level);

        if (players.isEmpty()) {
            return GoalTickResult.CONTINUE;
        }

        final BoundingBox boundingBox = scene.getOrDefault(ModDataComponentTypes.UNCLE_ANDREWS_HOUSE_BB, BoundingBox.infinite());
        if (players.stream().allMatch(playerEntity -> boundingBox.isInside(playerEntity.blockPosition()))) {
            return GoalTickResult.COMPLETED;
        }

        return GoalTickResult.CONTINUE;
    }

    @Override
    public void registerComponents(DataComponentMap.Builder builder) {
        builder.set(ModDataComponentTypes.UNCLE_ANDREWS_HOUSE_BB, BoundingBox.infinite());
        builder.set(ModDataComponentTypes.UNCLE_ANDREWS_HOUSE_DIRECTION, Direction.NORTH);
    }

    @Override
    public Component getDescription(Scene scene, Level level) {
        final BoundingBox boundingBox = scene.getOrDefault(ModDataComponentTypes.UNCLE_ANDREWS_HOUSE_BB, BoundingBox.infinite());
        final BlockPos blockPos = boundingBox.getCenter();
        return Component.literal(String.format("Walk to Uncle Andrew's House at x = %s, z = %s", blockPos.getX(), blockPos.getZ()));
    }
}
