package com.unclasprommer.aeronavigation.block.entity;

import com.unclasprommer.aeronavigation.navigation.RouteWaypoint;
import com.unclasprommer.aeronavigation.screen.VorDmeBeaconMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.UUID;

public class VorDmeBeaconBlockEntity extends BlockEntity implements MenuProvider {
    private static final String STATION_ID_KEY = "StationId";
    private static final String STATION_NAME_KEY = "StationName";
    public static final int MAX_STATION_NAME_LENGTH = 64;

    private UUID stationId;
    private String stationName = "";

    public VorDmeBeaconBlockEntity(final BlockPos pos, final BlockState blockState) {
        super(ModBlockEntityTypes.VORDME_BEACON.get(), pos, blockState);
    }

    public void ensureIdentity() {
        boolean changed = false;
        if (this.stationId == null) {
            this.stationId = UUID.randomUUID();
            changed = true;
        }
        if (this.stationName == null || this.stationName.isBlank()) {
            this.stationName = "VORDME-" + this.stationId.toString().substring(0, 8).toUpperCase(Locale.ROOT);
            changed = true;
        }
        if (changed) {
            this.setChanged();
        }
    }

    public String getStationName() {
        this.ensureIdentity();
        return this.stationName;
    }

    public void setStationName(final String stationName) {
        this.ensureIdentity();
        final String normalized = normalizeStationName(stationName, this.stationId);
        if (normalized.equals(this.stationName)) {
            return;
        }

        this.stationName = normalized;
        this.setChanged();
        if (this.level != null) {
            final BlockState state = this.getBlockState();
            this.level.sendBlockUpdated(this.worldPosition, state, state, 3);
        }
    }

    public RouteWaypoint createWaypoint(final Level level) {
        this.ensureIdentity();
        return new RouteWaypoint(this.stationName, GlobalPos.of(level.dimension(), this.worldPosition));
    }

    public void writeMenuData(final RegistryFriendlyByteBuf buffer) {
        buffer.writeBlockPos(this.worldPosition);
        buffer.writeUtf(this.getStationName());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(final int containerId, final Inventory playerInventory, final Player player) {
        return VorDmeBeaconMenu.create(containerId, playerInventory, this);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("screen.create_aeronautics_navigation.vordme_beacon");
    }

    @Override
    protected void saveAdditional(final CompoundTag tag, final HolderLookup.Provider registries) {
        this.ensureIdentity();
        tag.putUUID(STATION_ID_KEY, this.stationId);
        tag.putString(STATION_NAME_KEY, this.stationName);
        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(final CompoundTag tag, final HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains(STATION_ID_KEY)) {
            this.stationId = tag.getUUID(STATION_ID_KEY);
        }
        if (tag.contains(STATION_NAME_KEY)) {
            this.stationName = tag.getString(STATION_NAME_KEY);
        }
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(final HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

    public static String normalizeStationName(final String input, final UUID fallbackId) {
        final String cleaned = input == null ? "" : input.codePoints()
                .filter(codePoint -> codePoint == ' ' || !Character.isISOControl(codePoint))
                .limit(MAX_STATION_NAME_LENGTH)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString()
                .strip();

        if (!cleaned.isBlank()) {
            return cleaned;
        }

        return "VORDME-" + fallbackId.toString().substring(0, 8).toUpperCase(Locale.ROOT);
    }
}
