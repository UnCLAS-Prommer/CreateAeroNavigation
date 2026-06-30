package com.unclasprommer.aeronavigation.screen;

import com.unclasprommer.aeronavigation.item.ModItems;
import com.unclasprommer.aeronavigation.navigation.RouteData;
import com.unclasprommer.aeronavigation.navigation.RouteWaypoint;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class RouteCardMenu extends AbstractContainerMenu {
    private final InteractionHand hand;
    private final List<RouteWaypoint> waypoints;
    private final int routeIndex;

    public static RouteCardMenu create(final int containerId, final Inventory playerInventory, final InteractionHand hand, final ItemStack stack) {
        return new RouteCardMenu(containerId, playerInventory, hand, RouteData.getWaypoints(stack), RouteData.getRouteIndex(stack));
    }

    public static void writeMenuData(final RegistryFriendlyByteBuf buffer, final InteractionHand hand, final ItemStack stack) {
        buffer.writeVarInt(hand.ordinal());
        buffer.writeVarInt(RouteData.getRouteIndex(stack));
        final List<RouteWaypoint> waypoints = RouteData.getWaypoints(stack);
        buffer.writeVarInt(waypoints.size());
        for (final RouteWaypoint waypoint : waypoints) {
            RouteWaypoint.STREAM_CODEC.encode(buffer, waypoint);
        }
    }

    public RouteCardMenu(final int containerId, final Inventory playerInventory, final RegistryFriendlyByteBuf buffer) {
        this(containerId, playerInventory, readData(buffer));
    }

    private RouteCardMenu(final int containerId, final Inventory playerInventory, final RouteCardMenuData data) {
        this(containerId, playerInventory, data.hand(), data.waypoints(), data.routeIndex());
    }

    private RouteCardMenu(final int containerId, final Inventory playerInventory, final InteractionHand hand, final List<RouteWaypoint> waypoints, final int routeIndex) {
        super(ModMenuTypes.ROUTE_CARD.get(), containerId);
        this.hand = hand;
        this.waypoints = List.copyOf(waypoints);
        this.routeIndex = Math.clamp(routeIndex, 0, Math.max(0, waypoints.size() - 1));
    }

    private static RouteCardMenuData readData(final RegistryFriendlyByteBuf buffer) {
        final InteractionHand hand = InteractionHand.values()[Math.clamp(buffer.readVarInt(), 0, InteractionHand.values().length - 1)];
        final int routeIndex = buffer.readVarInt();
        final int count = Math.max(0, buffer.readVarInt());
        final List<RouteWaypoint> waypoints = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            waypoints.add(RouteWaypoint.STREAM_CODEC.decode(buffer));
        }
        return new RouteCardMenuData(hand, routeIndex, waypoints);
    }

    public InteractionHand getHand() {
        return this.hand;
    }

    public List<RouteWaypoint> getWaypoints() {
        return this.waypoints;
    }

    public int getRouteIndex() {
        return this.routeIndex;
    }

    @Override
    public boolean stillValid(final Player player) {
        return player.getItemInHand(this.hand).is(ModItems.ROUTE_CARD.get());
    }

    @Override
    public ItemStack quickMoveStack(final Player player, final int index) {
        return ItemStack.EMPTY;
    }

    private record RouteCardMenuData(InteractionHand hand, int routeIndex, List<RouteWaypoint> waypoints) {
    }
}
