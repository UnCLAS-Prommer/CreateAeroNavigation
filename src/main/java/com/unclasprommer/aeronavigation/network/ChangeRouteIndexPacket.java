package com.unclasprommer.aeronavigation.network;

import com.unclasprommer.aeronavigation.CreateAeronauticsNavigation;
import com.unclasprommer.aeronavigation.item.ModItems;
import com.unclasprommer.aeronavigation.navigation.RouteData;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ChangeRouteIndexPacket(InteractionHand hand, int delta) implements CustomPacketPayload {
    public static final Type<ChangeRouteIndexPacket> TYPE = new Type<>(CreateAeronauticsNavigation.path("change_route_index"));

    public static final StreamCodec<ByteBuf, ChangeRouteIndexPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            packet -> packet.hand.ordinal(),
            ByteBufCodecs.VAR_INT,
            ChangeRouteIndexPacket::delta,
            ChangeRouteIndexPacket::new
    );

    private ChangeRouteIndexPacket(final int handIndex, final int delta) {
        this(InteractionHand.values()[Math.clamp(handIndex, 0, InteractionHand.values().length - 1)], Math.clamp(delta, -1, 1));
    }

    public static void handle(final ChangeRouteIndexPacket packet, final IPayloadContext context) {
        if (!(context.player() instanceof final ServerPlayer player)) {
            return;
        }

        final ItemStack stack = player.getItemInHand(packet.hand());
        if (!stack.is(ModItems.ROUTE_CARD.get())) {
            return;
        }

        RouteData.changeRouteIndex(stack, packet.delta());
        player.getInventory().setChanged();
        player.inventoryMenu.broadcastChanges();
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
