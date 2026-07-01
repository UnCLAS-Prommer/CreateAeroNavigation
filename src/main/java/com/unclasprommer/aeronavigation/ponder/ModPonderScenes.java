package com.unclasprommer.aeronavigation.ponder;

import com.unclasprommer.aeronavigation.block.ModBlocks;
import com.unclasprommer.aeronavigation.item.ModItems;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

public final class ModPonderScenes {
    private ModPonderScenes() {
    }

    public static void register(final PonderSceneRegistrationHelper<ResourceLocation> helper) {
        helper.addStoryBoard(
                ModBlocks.VORDME_BEACON.getId(),
                "navigation_base",
                NavigationPonderScenes::vordmeBeacon,
                ModPonderTags.NAVIGATION
        );

        helper.addStoryBoard(
                ModItems.ROUTE_CARD.getId(),
                "navigation_base",
                NavigationPonderScenes::routeCardRecording,
                ModPonderTags.NAVIGATION
        );

        helper.addStoryBoard(
                ModItems.ROUTE_CARD.getId(),
                "navigation_base",
                NavigationPonderScenes::routeCardNavigationTable,
                ModPonderTags.NAVIGATION
        );
    }
}
