package com.unclasprommer.aeronavigation.navigation;

import com.unclasprommer.aeronavigation.component.ModDataComponents;
import dev.simulated_team.simulated.content.blocks.nav_table.NavTableBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class RouteData {
    public static final double ARRIVAL_RADIUS = 4.0D;

    private RouteData() {
    }

    public static List<RouteWaypoint> getWaypoints(final ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.ROUTE_WAYPOINTS.get(), List.of());
    }

    public static int getRouteIndex(final ItemStack stack) {
        final List<RouteWaypoint> waypoints = getWaypoints(stack);
        if (waypoints.isEmpty()) {
            return 0;
        }

        final int index = stack.getOrDefault(ModDataComponents.ROUTE_INDEX.get(), 0);
        return Math.clamp(index, 0, waypoints.size() - 1);
    }

    public static void setRouteIndex(final ItemStack stack, final int index) {
        final List<RouteWaypoint> waypoints = getWaypoints(stack);
        final int clamped = waypoints.isEmpty() ? 0 : Math.clamp(index, 0, waypoints.size() - 1);
        stack.set(ModDataComponents.ROUTE_INDEX.get(), clamped);
    }

    public static void addWaypoint(final ItemStack stack, final RouteWaypoint waypoint) {
        final List<RouteWaypoint> waypoints = new ArrayList<>(getWaypoints(stack));
        waypoints.add(waypoint);
        stack.set(ModDataComponents.ROUTE_WAYPOINTS.get(), List.copyOf(waypoints));
        setRouteIndex(stack, Math.min(getRouteIndex(stack), waypoints.size() - 1));
    }

    public static boolean removeWaypointAt(final ItemStack stack, final RouteWaypoint waypoint) {
        final List<RouteWaypoint> waypoints = new ArrayList<>(getWaypoints(stack));
        final boolean removed = waypoints.removeIf(existing -> existing.pos().equals(waypoint.pos()));
        if (removed) {
            stack.set(ModDataComponents.ROUTE_WAYPOINTS.get(), List.copyOf(waypoints));
            setRouteIndex(stack, getRouteIndex(stack));
        }
        return removed;
    }

    public static void clear(final ItemStack stack) {
        stack.set(ModDataComponents.ROUTE_WAYPOINTS.get(), List.of());
        stack.set(ModDataComponents.ROUTE_INDEX.get(), 0);
    }

    @Nullable
    public static RouteWaypoint getCurrentWaypoint(final ItemStack stack) {
        final List<RouteWaypoint> waypoints = getWaypoints(stack);
        if (waypoints.isEmpty()) {
            return null;
        }
        return waypoints.get(getRouteIndex(stack));
    }

    @Nullable
    public static Vec3 advanceAndGetTarget(final NavTableBlockEntity navBE, final ItemStack stack) {
        final List<RouteWaypoint> waypoints = getWaypoints(stack);
        if (waypoints.isEmpty()) {
            return null;
        }

        int index = getRouteIndex(stack);
        Vec3 target = targetFor(navBE, waypoints.get(index));
        if (target == null) {
            return null;
        }

        if (navBE.getProjectedSelfPos().distanceTo(target) <= ARRIVAL_RADIUS && index < waypoints.size() - 1) {
            index++;
            setRouteIndex(stack, index);
            target = targetFor(navBE, waypoints.get(index));
        }

        return target;
    }

    public static boolean isFinalWaypointReached(final NavTableBlockEntity navBE, final ItemStack stack) {
        final List<RouteWaypoint> waypoints = getWaypoints(stack);
        if (waypoints.isEmpty() || getRouteIndex(stack) != waypoints.size() - 1) {
            return false;
        }

        final Vec3 target = targetFor(navBE, waypoints.get(waypoints.size() - 1));
        return target != null && navBE.getProjectedSelfPos().distanceTo(target) <= ARRIVAL_RADIUS;
    }

    @Nullable
    private static Vec3 targetFor(final NavTableBlockEntity navBE, final RouteWaypoint waypoint) {
        if (!navBE.getLevel().dimension().equals(waypoint.pos().dimension())) {
            return null;
        }
        return waypoint.center();
    }
}
