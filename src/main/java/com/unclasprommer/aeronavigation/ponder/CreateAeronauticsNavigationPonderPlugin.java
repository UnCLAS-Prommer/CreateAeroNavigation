package com.unclasprommer.aeronavigation.ponder;

import com.unclasprommer.aeronavigation.CreateAeronauticsNavigation;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

public class CreateAeronauticsNavigationPonderPlugin implements PonderPlugin {
    @Override
    public String getModId() {
        return CreateAeronauticsNavigation.MOD_ID;
    }

    @Override
    public void registerScenes(final PonderSceneRegistrationHelper<ResourceLocation> helper) {
        ModPonderScenes.register(helper);
    }

    @Override
    public void registerTags(final PonderTagRegistrationHelper<ResourceLocation> helper) {
        ModPonderTags.register(helper);
    }
}
