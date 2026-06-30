package com.unclasprommer.aeronavigation.component;

import com.unclasprommer.aeronavigation.CreateAeronauticsNavigation;
import com.unclasprommer.aeronavigation.navigation.RouteWaypoint;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

public class ModDataComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, CreateAeronauticsNavigation.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<RouteWaypoint>>> ROUTE_WAYPOINTS =
            DATA_COMPONENT_TYPES.register("route_waypoints", () -> DataComponentType.<List<RouteWaypoint>>builder()
                    .persistent(RouteWaypoint.LIST_CODEC)
                    .networkSynchronized(ByteBufCodecs.fromCodecTrusted(RouteWaypoint.LIST_CODEC))
                    .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> ROUTE_INDEX =
            DATA_COMPONENT_TYPES.register("route_index", () -> DataComponentType.<Integer>builder()
                    .persistent(com.mojang.serialization.Codec.INT)
                    .networkSynchronized(ByteBufCodecs.VAR_INT)
                    .build());

    public static void register(final IEventBus eventBus) {
        DATA_COMPONENT_TYPES.register(eventBus);
    }
}
