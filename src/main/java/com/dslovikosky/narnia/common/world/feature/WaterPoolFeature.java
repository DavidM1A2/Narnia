package com.dslovikosky.narnia.common.world.feature;

import com.dslovikosky.narnia.common.utils.EllipsoidUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class WaterPoolFeature extends Feature<WaterPoolFeature.Configuration> {
    public WaterPoolFeature(Codec<WaterPoolFeature.Configuration> codec) {
        super(codec);
    }

    @Override
    public boolean place(final FeaturePlaceContext<Configuration> context) {
        final BlockPos blockPos = context.origin();
        final BlockPos waterLevelPos = blockPos.below();
        final WorldGenLevel level = context.level();
        final RandomSource random = context.random();
        final WaterPoolFeature.Configuration configuration = context.config();

        final BlockStateProvider fluid = configuration.fluid();
        final int width = configuration.width().sample(random);
        final int length = configuration.length().sample(random);
        final int depth = configuration.depth().sample(random);

        if (waterLevelPos.getY() <= level.getMinBuildHeight() + depth + 1) {
            return false;
        }

        final int widthBoundingBox = width / 2 + 2;
        final int lengthBoundingBox = length / 2 + 2;
        for (int x = -widthBoundingBox; x <= widthBoundingBox; x++) {
            for (int z = -lengthBoundingBox; z <= lengthBoundingBox; z++) {
                for (int y = -depth; y < 0; y++) {
                    final BlockPos currentBlockPos = waterLevelPos.offset(x, y, z);

                    if (!EllipsoidUtils.isInsideEllipsoid(0.0, 0.0, 0.0, width / 2.0, depth, length / 2.0, x, y, z)) {
                        continue;
                    }

                    final BlockState newBlockState = fluid.getState(random, currentBlockPos);
                    final BlockState oldBlockState = level.getBlockState(currentBlockPos);
                    if (!oldBlockState.is(BlockTags.FEATURES_CANNOT_REPLACE) && !oldBlockState.isAir()) {
                        level.setBlock(currentBlockPos, newBlockState, 2);
                    }
                }
                final BlockPos currentBlockPos = waterLevelPos.offset(x, 0, z);

                if (!EllipsoidUtils.isInsideEllipsoid(0.0, 0.0, 0.0, widthBoundingBox, depth, lengthBoundingBox, x, 0, z)) {
                    continue;
                }

                final BlockState oldBlockState = level.getBlockState(currentBlockPos);
                if (!oldBlockState.is(BlockTags.FEATURES_CANNOT_REPLACE) && !oldBlockState.isAir()) {
                    level.setBlock(currentBlockPos, Blocks.AIR.defaultBlockState(), 2);
                    if (level.getBlockState(currentBlockPos.below()).is(Blocks.DIRT)) {
                        level.setBlock(currentBlockPos.below(), Blocks.GRASS_BLOCK.defaultBlockState(), 2);
                    }
                }
            }
        }

        return false;
    }

    public record Configuration(BlockStateProvider fluid, IntProvider width, IntProvider length, IntProvider depth) implements FeatureConfiguration {
        public static final Codec<WaterPoolFeature.Configuration> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                                BlockStateProvider.CODEC.fieldOf("fluid").forGetter(WaterPoolFeature.Configuration::fluid),
                                IntProvider.CODEC.fieldOf("width").forGetter(WaterPoolFeature.Configuration::width),
                                IntProvider.CODEC.fieldOf("length").forGetter(WaterPoolFeature.Configuration::length),
                                IntProvider.CODEC.fieldOf("depth").forGetter(WaterPoolFeature.Configuration::depth)
                        )
                        .apply(instance, WaterPoolFeature.Configuration::new)
        );
    }
}
