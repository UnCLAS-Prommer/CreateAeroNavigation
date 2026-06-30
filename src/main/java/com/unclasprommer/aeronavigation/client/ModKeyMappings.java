package com.unclasprommer.aeronavigation.client;

import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class ModKeyMappings {
    public static final KeyMapping ROUTE_CARD_SELECT = new KeyMapping(
            "key.create_aeronautics_navigation.route_card_select",
            GLFW.GLFW_KEY_L,
            "key.categories.create_aeronautics_navigation"
    );
}
