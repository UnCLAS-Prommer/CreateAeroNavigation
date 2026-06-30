package com.unclasprommer.aeronavigation.block.entity;

import com.unclasprommer.aeronavigation.CreateAeronauticsNavigation;
import com.unclasprommer.aeronavigation.block.ModBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntityTypes {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, CreateAeronauticsNavigation.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<VorDmeBeaconBlockEntity>> VORDME_BEACON =
            BLOCK_ENTITY_TYPES.register("vordme_beacon", () -> BlockEntityType.Builder.of(
                    VorDmeBeaconBlockEntity::new,
                    ModBlocks.VORDME_BEACON.get()
            ).build(null));

    public static void register(final IEventBus eventBus) {
        BLOCK_ENTITY_TYPES.register(eventBus);
    }
}
