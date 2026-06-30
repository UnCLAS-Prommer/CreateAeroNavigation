package com.unclasprommer.aeronavigation.network;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ModNetwork {
    private static final String NETWORK_VERSION = "1";

    public static void register(final IEventBus modEventBus) {
        modEventBus.addListener(ModNetwork::registerPayloads);
    }

    private static void registerPayloads(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(NETWORK_VERSION);
        registrar.playToServer(ChangeRouteIndexPacket.TYPE, ChangeRouteIndexPacket.STREAM_CODEC, ChangeRouteIndexPacket::handle);
        registrar.playToServer(EditRouteCardPacket.TYPE, EditRouteCardPacket.STREAM_CODEC, EditRouteCardPacket::handle);
        registrar.playToServer(RenameVorDmeBeaconPacket.TYPE, RenameVorDmeBeaconPacket.STREAM_CODEC, RenameVorDmeBeaconPacket::handle);
    }
}
