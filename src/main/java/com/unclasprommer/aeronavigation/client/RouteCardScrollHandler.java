package com.unclasprommer.aeronavigation.client;

import com.unclasprommer.aeronavigation.item.ModItems;
import com.unclasprommer.aeronavigation.navigation.RouteData;
import com.unclasprommer.aeronavigation.navigation.RouteWaypoint;
import com.unclasprommer.aeronavigation.network.ChangeRouteIndexPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

public class RouteCardScrollHandler {
    public static boolean onMouseScroll(final double scrollDeltaY) {
        if (!ModKeyMappings.ROUTE_CARD_SELECT.isDown() || scrollDeltaY == 0) {
            return false;
        }

        final Player player = Minecraft.getInstance().player;
        if (player == null) {
            return false;
        }

        final InteractionHand hand = getRouteCardHand(player);
        if (hand == null) {
            return false;
        }

        final ItemStack stack = player.getItemInHand(hand);
        final List<RouteWaypoint> waypoints = RouteData.getWaypoints(stack);
        if (waypoints.isEmpty()) {
            return false;
        }

        final int delta = scrollDeltaY < 0 ? 1 : -1;
        final int index = RouteData.changeRouteIndex(stack, delta);
        final RouteWaypoint waypoint = waypoints.get(index);

        player.displayClientMessage(Component.translatable(
                "item.create_aeronautics_navigation.route_card.selected",
                index + 1,
                waypoints.size(),
                waypoint.name()
        ), true);
        PacketDistributor.sendToServer(new ChangeRouteIndexPacket(hand, delta));
        return true;
    }

    private static InteractionHand getRouteCardHand(final Player player) {
        if (player.getMainHandItem().is(ModItems.ROUTE_CARD.get())) {
            return InteractionHand.MAIN_HAND;
        }
        if (player.getOffhandItem().is(ModItems.ROUTE_CARD.get())) {
            return InteractionHand.OFF_HAND;
        }
        return null;
    }
}
