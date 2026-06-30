package com.unclasprommer.aeronavigation.network;

import com.unclasprommer.aeronavigation.CreateAeronauticsNavigation;
import com.unclasprommer.aeronavigation.item.ModItems;
import com.unclasprommer.aeronavigation.navigation.RouteData;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record EditRouteCardPacket(InteractionHand hand, int action, int index, int delta, BlockPos pos) implements CustomPacketPayload {
    private static final int ACTION_MOVE = 0;
    private static final int ACTION_REMOVE = 1;
    private static final int ACTION_ADD = 2;

    public static final Type<EditRouteCardPacket> TYPE = new Type<>(CreateAeronauticsNavigation.path("edit_route_card"));

    public static final StreamCodec<ByteBuf, EditRouteCardPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            packet -> packet.hand.ordinal(),
            ByteBufCodecs.VAR_INT,
            EditRouteCardPacket::action,
            ByteBufCodecs.VAR_INT,
            EditRouteCardPacket::index,
            ByteBufCodecs.VAR_INT,
            EditRouteCardPacket::delta,
            BlockPos.STREAM_CODEC,
            EditRouteCardPacket::pos,
            EditRouteCardPacket::new
    );

    private EditRouteCardPacket(final int handIndex, final int action, final int index, final int delta, final BlockPos pos) {
        this(InteractionHand.values()[Math.clamp(handIndex, 0, InteractionHand.values().length - 1)], Math.clamp(action, ACTION_MOVE, ACTION_ADD), index, Math.clamp(delta, -1, 1), pos);
    }

    public static EditRouteCardPacket move(final InteractionHand hand, final int index, final int delta) {
        return new EditRouteCardPacket(hand, ACTION_MOVE, index, delta, BlockPos.ZERO);
    }

    public static EditRouteCardPacket remove(final InteractionHand hand, final int index) {
        return new EditRouteCardPacket(hand, ACTION_REMOVE, index, 0, BlockPos.ZERO);
    }

    public static EditRouteCardPacket add(final InteractionHand hand, final BlockPos pos) {
        return new EditRouteCardPacket(hand, ACTION_ADD, 0, 0, pos);
    }

    public static void handle(final EditRouteCardPacket packet, final IPayloadContext context) {
        if (!(context.player() instanceof final ServerPlayer player)) {
            return;
        }

        final ItemStack stack = player.getItemInHand(packet.hand());
        if (!stack.is(ModItems.ROUTE_CARD.get())) {
            return;
        }

        final boolean changed = switch (packet.action()) {
            case ACTION_MOVE -> RouteData.moveWaypoint(stack, packet.index(), packet.delta());
            case ACTION_REMOVE -> RouteData.removeWaypointAtIndex(stack, packet.index());
            case ACTION_ADD -> {
                RouteData.addCoordinateWaypoint(stack, player.level(), packet.pos());
                yield true;
            }
            default -> false;
        };

        if (!changed) {
            return;
        }

        player.getInventory().setChanged();
        player.inventoryMenu.broadcastChanges();
        player.containerMenu.broadcastChanges();
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
