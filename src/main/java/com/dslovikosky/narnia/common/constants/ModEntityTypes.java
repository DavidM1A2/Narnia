package com.dslovikosky.narnia.common.constants;

import com.dslovikosky.narnia.common.entity.human_child.HumanChildEntity;
import com.dslovikosky.narnia.common.entity.lion.LionEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, Constants.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<HumanChildEntity>> DIGORY = ENTITY_TYPES.register("digory", id -> EntityType.Builder.of(HumanChildEntity::new, MobCategory.CREATURE)
            .sized(0.4F, 1.1F)
            .eyeHeight(0.9F)
            .ridingOffset(-0.7F)
            .clientTrackingRange(8)
            .build(id.getPath()));
    public static final DeferredHolder<EntityType<?>, EntityType<HumanChildEntity>> POLLY = ENTITY_TYPES.register("polly", id -> EntityType.Builder.of(HumanChildEntity::new, MobCategory.CREATURE)
            .sized(0.4F, 1.1F)
            .eyeHeight(0.9F)
            .ridingOffset(-0.7F)
            .clientTrackingRange(8)
            .build(id.getPath()));
    public static final DeferredHolder<EntityType<?>, EntityType<LionEntity>> LION = ENTITY_TYPES.register("lion", id -> EntityType.Builder.of(LionEntity::new, MobCategory.CREATURE)
            .sized(1.0F, 2.0F)
            .eyeHeight(1.5F)
            .ridingOffset(-0.7F)
            .clientTrackingRange(8)
            .build(id.getPath()));
}
