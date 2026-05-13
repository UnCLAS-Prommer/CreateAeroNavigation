package com.unclasprommer.aeronavigation.item;

import com.unclasprommer.aeronavigation.CreateAeronauticsNavigation;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CreateAeronauticsNavigation.MOD_ID);
    public static final Supplier<CreativeModeTab> NAVIGATION_TAB =
            CREATIVE_MODE_TABS.register("navigation_tab", () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.VOR_BEACON.get()))
                    .title(Component.translatable("itemGoup.navigation_tab"))
                    .displayItems((parameters, output) -> {
                        output.accept((ModItems.VOR_BEACON));
                    }).build());

    public static void register(IEventBus eventBus){
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
