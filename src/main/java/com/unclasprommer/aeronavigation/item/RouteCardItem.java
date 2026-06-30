package com.unclasprommer.aeronavigation.item;

import com.unclasprommer.aeronavigation.block.entity.VorDmeBeaconBlockEntity;
import com.unclasprommer.aeronavigation.navigation.RouteData;
import com.unclasprommer.aeronavigation.navigation.RouteWaypoint;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;

public class RouteCardItem extends Item {
    private static final int MAX_TOOLTIP_WAYPOINTS = 6;

    public RouteCardItem(final Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(final UseOnContext context) {
        final Level level = context.getLevel();
        final BlockEntity blockEntity = level.getBlockEntity(context.getClickedPos());
        if (!(blockEntity instanceof final VorDmeBeaconBlockEntity beacon)) {
            return super.useOn(context);
        }

        final Player player = context.getPlayer();
        final ItemStack stack = context.getItemInHand();
        recordBeacon(stack, level, player, beacon);

        return level.isClientSide ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
    }

    public static void recordBeacon(final ItemStack stack, final Level level, final Player player, final VorDmeBeaconBlockEntity beacon) {
        if (level.isClientSide) {
            return;
        }

        final RouteWaypoint waypoint = beacon.createWaypoint(level);
        if (player != null && player.isShiftKeyDown()) {
            final boolean removed = RouteData.removeWaypointAt(stack, waypoint);
            player.displayClientMessage(Component.translatable(
                    removed
                            ? "item.create_aeronautics_navigation.route_card.remove"
                            : "item.create_aeronautics_navigation.route_card.remove_missing",
                    waypoint.name()
            ), true);
        } else {
            RouteData.addWaypoint(stack, waypoint);
            if (player != null) {
                player.displayClientMessage(Component.translatable(
                        "item.create_aeronautics_navigation.route_card.add",
                        waypoint.name(),
                        RouteData.getWaypoints(stack).size()
                ), true);
            }
        }

        if (player != null) {
            player.getInventory().setChanged();
            player.inventoryMenu.broadcastChanges();
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(final Level level, final Player player, final InteractionHand usedHand) {
        final ItemStack stack = player.getItemInHand(usedHand);
        if (player.isShiftKeyDown() && !RouteData.getWaypoints(stack).isEmpty()) {
            if (!level.isClientSide) {
                RouteData.clear(stack);
                player.displayClientMessage(Component.translatable("item.create_aeronautics_navigation.route_card.clear"), true);
            }
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
        }
        return super.use(level, player, usedHand);
    }

    @Override
    public void appendHoverText(final ItemStack stack, final TooltipContext context, final List<Component> tooltipComponents, final TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        final List<RouteWaypoint> waypoints = RouteData.getWaypoints(stack);
        if (waypoints.isEmpty()) {
            tooltipComponents.add(Component.translatable("item.create_aeronautics_navigation.route_card.empty")
                    .withStyle(ChatFormatting.DARK_GRAY));
            return;
        }

        tooltipComponents.add(Component.translatable(
                "item.create_aeronautics_navigation.route_card.progress",
                RouteData.getRouteIndex(stack) + 1,
                waypoints.size()
        ).withStyle(ChatFormatting.GOLD));

        final int shown = Math.min(MAX_TOOLTIP_WAYPOINTS, waypoints.size());
        for (int i = 0; i < shown; i++) {
            final RouteWaypoint waypoint = waypoints.get(i);
            final BlockPos pos = waypoint.pos().pos();
            tooltipComponents.add(Component.translatable(
                    "item.create_aeronautics_navigation.route_card.waypoint",
                    i + 1,
                    waypoint.name(),
                    pos.getX(),
                    pos.getY(),
                    pos.getZ()
            ).withStyle(i == RouteData.getRouteIndex(stack) ? ChatFormatting.AQUA : ChatFormatting.GRAY));
        }

        if (waypoints.size() > shown) {
            tooltipComponents.add(Component.translatable(
                    "item.create_aeronautics_navigation.route_card.more",
                    waypoints.size() - shown
            ).withStyle(ChatFormatting.DARK_GRAY));
        }
    }
}
