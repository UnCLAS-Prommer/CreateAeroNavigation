package com.unclasprommer.aeronavigation;

import com.unclasprommer.aeronavigation.client.ModKeyMappings;
import com.unclasprommer.aeronavigation.client.RouteCardScrollHandler;
import com.unclasprommer.aeronavigation.screen.ModMenuTypes;
import com.unclasprommer.aeronavigation.screen.VorDmeBeaconScreen;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = CreateAeronauticsNavigation.MOD_ID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = CreateAeronauticsNavigation.MOD_ID, value = Dist.CLIENT)
public class CreateAeronauticsNavigationClient {
    public CreateAeronauticsNavigationClient(final IEventBus modEventBus, final ModContainer container) {
        // Allows NeoForge to create a config screen for this mod's configs.
        // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
        // Do not forget to add translations for your config options to the en_us.json file.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        modEventBus.addListener(CreateAeronauticsNavigationClient::registerKeyMappings);
        modEventBus.addListener(CreateAeronauticsNavigationClient::registerMenuScreens);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        // Some client setup code
        CreateAeronauticsNavigation.LOGGER.info("HELLO FROM CLIENT SETUP");
        CreateAeronauticsNavigation.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    }

    @SubscribeEvent
    static void onMouseScroll(final InputEvent.MouseScrollingEvent event) {
        if (RouteCardScrollHandler.onMouseScroll(event.getScrollDeltaY())) {
            event.setCanceled(true);
        }
    }

    static void registerKeyMappings(final RegisterKeyMappingsEvent event) {
        event.register(ModKeyMappings.ROUTE_CARD_SELECT);
    }

    static void registerMenuScreens(final RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.VORDME_BEACON.get(), VorDmeBeaconScreen::new);
    }
}
