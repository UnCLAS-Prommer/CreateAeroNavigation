package com.unclasprommer.aeronavigation.integration.simulated;

import com.unclasprommer.aeronavigation.item.ModItems;
import com.unclasprommer.aeronavigation.navigation.RouteData;
import dev.simulated_team.simulated.content.blocks.nav_table.NavTableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;

public final class NavigationTableRedstone {
    private NavigationTableRedstone() {
    }

    public static boolean isRouteComplete(final BlockGetter level, final BlockPos pos) {
        final BlockEntity blockEntity = level.getBlockEntity(pos);
        return blockEntity instanceof final NavTableBlockEntity navTable && isRouteComplete(navTable);
    }

    public static boolean isRouteComplete(final NavTableBlockEntity navTable) {
        final ItemStack stack = navTable.getHeldItem();
        return stack.is(ModItems.ROUTE_CARD.get()) && RouteData.isFinalWaypointReached(navTable, stack);
    }
}
