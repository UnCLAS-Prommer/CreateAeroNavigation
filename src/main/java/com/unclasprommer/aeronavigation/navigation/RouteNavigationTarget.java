package com.unclasprommer.aeronavigation.navigation;

import dev.simulated_team.simulated.content.blocks.nav_table.NavTableBlockEntity;
import dev.simulated_team.simulated.content.blocks.nav_table.navigation_target.NavigationTarget;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class RouteNavigationTarget implements NavigationTarget {
    @Nullable
    @Override
    public Vec3 getTarget(final NavTableBlockEntity navBE, final ItemStack self) {
        return RouteData.advanceAndGetTarget(navBE, self);
    }

    @Override
    public float getDeadzone() {
        return (float) RouteData.ARRIVAL_RADIUS;
    }

    @Override
    public int getRedstoneStrength(final NavTableBlockEntity navBE, final Direction direction, final ItemStack self) {
        if (direction == Direction.UP) {
            return RouteData.isFinalWaypointReached(navBE, self) ? 15 : 0;
        }
        return NavigationTarget.super.getRedstoneStrength(navBE, direction, self);
    }

    @Override
    public void onInsert(final ItemStack itemStack, final NavTableBlockEntity be, @Nullable final Player player) {
        RouteData.setRouteIndex(itemStack, RouteData.getRouteIndex(itemStack));
    }
}
