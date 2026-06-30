package com.unclasprommer.aeronavigation.navigation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;

public record RouteWaypoint(String name, GlobalPos pos) {
    public static final Codec<RouteWaypoint> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("name").forGetter(RouteWaypoint::name),
            GlobalPos.CODEC.fieldOf("pos").forGetter(RouteWaypoint::pos)
    ).apply(instance, RouteWaypoint::new));

    public static final Codec<java.util.List<RouteWaypoint>> LIST_CODEC = CODEC.listOf();

    public static final StreamCodec<ByteBuf, RouteWaypoint> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            RouteWaypoint::name,
            GlobalPos.STREAM_CODEC,
            RouteWaypoint::pos,
            RouteWaypoint::new
    );

    public Vec3 center() {
        return Vec3.atCenterOf(this.pos.pos());
    }
}
