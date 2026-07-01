package com.unclasprommer.aeronavigation;

import com.unclasprommer.aeronavigation.ponder.CreateAeronauticsNavigationPonderPlugin;
import com.unclasprommer.aeronavigation.screen.ModMenuTypes;
import com.unclasprommer.aeronavigation.screen.RouteCardScreen;
import com.unclasprommer.aeronavigation.screen.VorDmeBeaconScreen;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = CreateAeronauticsNavigation.MOD_ID, dist = Dist.CLIENT)
public class CreateAeronauticsNavigationClient {
    public CreateAeronauticsNavigationClient(final IEventBus modEventBus, final ModContainer container) {
        // Allows NeoForge to create a config screen for this mod's configs.
        // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
        // Do not forget to add translations for your config options to the en_us.json file.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        modEventBus.addListener(CreateAeronauticsNavigationClient::onClientSetup);
        modEventBus.addListener(CreateAeronauticsNavigationClient::registerMenuScreens);
    }

    static void onClientSetup(FMLClientSetupEvent event) {
        // Some client setup code
        CreateAeronauticsNavigation.LOGGER.info("HELLO FROM CLIENT SETUP");
        CreateAeronauticsNavigation.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        PonderIndex.addPlugin(new CreateAeronauticsNavigationPonderPlugin());
    }

    static void registerMenuScreens(final RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.VORDME_BEACON.get(), VorDmeBeaconScreen::new);
        event.register(ModMenuTypes.ROUTE_CARD.get(), RouteCardScreen::new);
    }
}
