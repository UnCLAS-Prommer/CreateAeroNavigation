package com.unclasprommer.aeronavigation.screen;

import com.unclasprommer.aeronavigation.CreateAeronauticsNavigation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(BuiltInRegistries.MENU, CreateAeronauticsNavigation.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<VorDmeBeaconMenu>> VORDME_BEACON =
            MENU_TYPES.register("vordme_beacon", () -> IMenuTypeExtension.create(VorDmeBeaconMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<RouteCardMenu>> ROUTE_CARD =
            MENU_TYPES.register("route_card", () -> IMenuTypeExtension.create(RouteCardMenu::new));

    public static void register(final IEventBus eventBus) {
        MENU_TYPES.register(eventBus);
    }
}
