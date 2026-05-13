package com.unclasprommer.aeronavigation.item;

import com.unclasprommer.aeronavigation.CreateAeronauticsNavigation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(CreateAeronauticsNavigation.MOD_ID);

    public static final DeferredItem<Item> VOR_BEACON =
            ITEMS.register("vor_beacon", () -> new Item(new Item.Properties()));

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
