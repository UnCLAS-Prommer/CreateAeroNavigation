package com.unclasprommer.aeronavigation.block;

import com.mojang.serialization.MapCodec;
import com.unclasprommer.aeronavigation.block.entity.VorDmeBeaconBlockEntity;
import com.unclasprommer.aeronavigation.item.ModItems;
import com.unclasprommer.aeronavigation.item.RouteCardItem;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class VorDmeBeaconBlock extends BaseEntityBlock {
    public static final MapCodec<VorDmeBeaconBlock> CODEC = simpleCodec(VorDmeBeaconBlock::new);

    public VorDmeBeaconBlock(final Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
        return new VorDmeBeaconBlockEntity(pos, state);
    }

    @Override
    protected RenderShape getRenderShape(final BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public void setPlacedBy(final Level level, final BlockPos pos, final BlockState state, @Nullable final LivingEntity placer, final ItemStack stack) {
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof final VorDmeBeaconBlockEntity beacon) {
            beacon.ensureIdentity();
        }
    }

    @Override
    protected ItemInteractionResult useItemOn(final ItemStack stack, final BlockState state, final Level level, final BlockPos pos, final Player player, final InteractionHand hand, final BlockHitResult hitResult) {
        if (!stack.is(ModItems.ROUTE_CARD.get())) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (level.getBlockEntity(pos) instanceof final VorDmeBeaconBlockEntity beacon) {
            RouteCardItem.recordBeacon(stack, level, player, beacon);
            return level.isClientSide ? ItemInteractionResult.SUCCESS : ItemInteractionResult.CONSUME;
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected InteractionResult useWithoutItem(final BlockState state, final Level level, final BlockPos pos, final Player player, final BlockHitResult hitResult) {
        if (player.isShiftKeyDown()) {
            if (!level.isClientSide && level.getBlockEntity(pos) instanceof final VorDmeBeaconBlockEntity beacon) {
                player.displayClientMessage(Component.translatable(
                        "block.create_aeronautics_navigation.vordme_beacon.status",
                        beacon.getStationName(),
                        pos.getX(),
                        pos.getY(),
                        pos.getZ()
                ), true);
            }
            return level.isClientSide ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
        }

        if (!level.isClientSide && level.getBlockEntity(pos) instanceof final VorDmeBeaconBlockEntity beacon) {
            if (player instanceof final ServerPlayer serverPlayer) {
                serverPlayer.openMenu(beacon, beacon::writeMenuData);
            }
        }
        return level.isClientSide ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
    }
}
