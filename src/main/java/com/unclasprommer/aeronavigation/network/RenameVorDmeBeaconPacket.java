package com.unclasprommer.aeronavigation.network;

import com.unclasprommer.aeronavigation.CreateAeronauticsNavigation;
import com.unclasprommer.aeronavigation.block.entity.VorDmeBeaconBlockEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record RenameVorDmeBeaconPacket(BlockPos pos, String stationName) implements CustomPacketPayload {
    private static final double MAX_USE_DISTANCE_SQUARED = 64.0D;

    public static final Type<RenameVorDmeBeaconPacket> TYPE = new Type<>(CreateAeronauticsNavigation.path("rename_vordme_beacon"));

    public static final StreamCodec<ByteBuf, RenameVorDmeBeaconPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            RenameVorDmeBeaconPacket::pos,
            ByteBufCodecs.STRING_UTF8,
            RenameVorDmeBeaconPacket::stationName,
            RenameVorDmeBeaconPacket::new
    );

    public static void handle(final RenameVorDmeBeaconPacket packet, final IPayloadContext context) {
        if (!(context.player() instanceof final ServerPlayer player)) {
            return;
        }

        final double x = packet.pos().getX() + 0.5D;
        final double y = packet.pos().getY() + 0.5D;
        final double z = packet.pos().getZ() + 0.5D;
        if (player.distanceToSqr(x, y, z) > MAX_USE_DISTANCE_SQUARED) {
            return;
        }

        if (player.level().getBlockEntity(packet.pos()) instanceof final VorDmeBeaconBlockEntity beacon) {
            beacon.setStationName(packet.stationName());
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
