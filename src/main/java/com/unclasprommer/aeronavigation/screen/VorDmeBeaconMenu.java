package com.unclasprommer.aeronavigation.screen;

import com.unclasprommer.aeronavigation.block.entity.VorDmeBeaconBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class VorDmeBeaconMenu extends AbstractContainerMenu {
    private static final double MAX_USE_DISTANCE_SQUARED = 64.0D;

    private final BlockPos pos;
    private final String stationName;

    public static VorDmeBeaconMenu create(final int containerId, final Inventory playerInventory, final VorDmeBeaconBlockEntity beacon) {
        return new VorDmeBeaconMenu(containerId, playerInventory, beacon.getBlockPos(), beacon.getStationName());
    }

    public VorDmeBeaconMenu(final int containerId, final Inventory playerInventory, final RegistryFriendlyByteBuf buffer) {
        this(containerId, playerInventory, buffer.readBlockPos(), buffer.readUtf());
    }

    private VorDmeBeaconMenu(final int containerId, final Inventory playerInventory, final BlockPos pos, final String stationName) {
        super(ModMenuTypes.VORDME_BEACON.get(), containerId);
        this.pos = pos;
        this.stationName = stationName;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public String getStationName() {
        return this.stationName;
    }

    @Override
    public boolean stillValid(final Player player) {
        if (!(player.level().getBlockEntity(this.pos) instanceof VorDmeBeaconBlockEntity)) {
            return false;
        }

        final double x = this.pos.getX() + 0.5D;
        final double y = this.pos.getY() + 0.5D;
        final double z = this.pos.getZ() + 0.5D;
        return player.distanceToSqr(x, y, z) <= MAX_USE_DISTANCE_SQUARED;
    }

    @Override
    public ItemStack quickMoveStack(final Player player, final int index) {
        return ItemStack.EMPTY;
    }
}
