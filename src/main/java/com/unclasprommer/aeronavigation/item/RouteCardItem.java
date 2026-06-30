package com.unclasprommer.aeronavigation.item;

import com.unclasprommer.aeronavigation.block.entity.VorDmeBeaconBlockEntity;
import com.unclasprommer.aeronavigation.navigation.RouteData;
import com.unclasprommer.aeronavigation.navigation.RouteWaypoint;
import com.unclasprommer.aeronavigation.screen.RouteCardMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
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
        if (player.isShiftKeyDown()) {
            if (!level.isClientSide && !RouteData.getWaypoints(stack).isEmpty()) {
                RouteData.clear(stack);
                player.displayClientMessage(Component.translatable("item.create_aeronautics_navigation.route_card.clear"), true);
            }
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
        }

        openRouteCardMenu(level, player, usedHand, stack);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    private static void openRouteCardMenu(final Level level, final Player player, final InteractionHand hand, final ItemStack stack) {
        if (level.isClientSide || !(player instanceof final ServerPlayer serverPlayer)) {
            return;
        }

        serverPlayer.openMenu(new SimpleMenuProvider(
                        (containerId, inventory, menuPlayer) -> RouteCardMenu.create(containerId, inventory, hand, stack),
                        Component.translatable("screen.create_aeronautics_navigation.route_card")
                ),
                buffer -> RouteCardMenu.writeMenuData(buffer, hand, stack));
    }

    @Override
    public InteractionResult useOn(final UseOnContext context) {
        final Level level = context.getLevel();
        final BlockEntity blockEntity = level.getBlockEntity(context.getClickedPos());
        if (blockEntity instanceof final VorDmeBeaconBlockEntity beacon) {
            final Player player = context.getPlayer();
            final ItemStack stack = context.getItemInHand();
            recordBeacon(stack, level, player, beacon);

            return level.isClientSide ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
        }

        final Player player = context.getPlayer();
        if (player != null && !player.isShiftKeyDown()) {
            openRouteCardMenu(level, player, context.getHand(), context.getItemInHand());
            return level.isClientSide ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
        }
        return super.useOn(context);
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
