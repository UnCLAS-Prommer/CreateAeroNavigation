package com.unclasprommer.aeronavigation.ponder;

import com.unclasprommer.aeronavigation.CreateAeronauticsNavigation;
import com.unclasprommer.aeronavigation.block.ModBlocks;
import com.unclasprommer.aeronavigation.item.ModItems;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

public final class ModPonderTags {
    public static final ResourceLocation NAVIGATION = CreateAeronauticsNavigation.path("navigation");

    private ModPonderTags() {
    }

    public static void register(final PonderTagRegistrationHelper<ResourceLocation> helper) {
        helper.registerTag(NAVIGATION)
                .addToIndex()
                .item(ModItems.ROUTE_CARD.get(), true, true)
                .title("Aeronautics Navigation")
                .description("Waypoints and route cards for Create: Aeronautics navigation tables")
                .register();

        helper.addToTag(NAVIGATION)
                .add(ModItems.ROUTE_CARD.getId())
                .add(ModBlocks.VORDME_BEACON.getId());
    }
}
