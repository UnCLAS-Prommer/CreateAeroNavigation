package com.unclasprommer.aeronavigation.block.entity;

import com.unclasprommer.aeronavigation.navigation.RouteWaypoint;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Locale;
import java.util.UUID;

public class VorDmeBeaconBlockEntity extends BlockEntity {
    private static final String STATION_ID_KEY = "StationId";
    private static final String STATION_NAME_KEY = "StationName";

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

    public RouteWaypoint createWaypoint(final Level level) {
        this.ensureIdentity();
        return new RouteWaypoint(this.stationName, GlobalPos.of(level.dimension(), this.worldPosition));
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
}
