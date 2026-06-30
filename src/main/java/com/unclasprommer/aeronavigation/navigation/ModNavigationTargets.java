package com.unclasprommer.aeronavigation.navigation;

import com.unclasprommer.aeronavigation.CreateAeronauticsNavigation;
import dev.simulated_team.simulated.content.blocks.nav_table.navigation_target.NavigationTarget;
import dev.simulated_team.simulated.index.SimRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModNavigationTargets {
    public static final DeferredRegister<NavigationTarget> NAVIGATION_TARGETS =
            DeferredRegister.create(SimRegistries.Keys.NAVIGATION_TARGET, CreateAeronauticsNavigation.MOD_ID);

    public static final DeferredHolder<NavigationTarget, RouteNavigationTarget> ROUTE_CARD =
            NAVIGATION_TARGETS.register("route_card", RouteNavigationTarget::new);

    public static void register(final IEventBus eventBus) {
        NAVIGATION_TARGETS.register(eventBus);
    }
}
