package com.unclasprommer.aeronavigation.mixin;

import com.unclasprommer.aeronavigation.integration.simulated.NavigationTableRedstone;
import dev.simulated_team.simulated.content.blocks.nav_table.NavTableBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NavTableBlock.class)
public class NavTableBlockMixin {
    @Inject(method = "getSignal", at = @At("HEAD"), cancellable = true)
    private void createAeronauticsNavigation$getSignal(
            final BlockState state,
            final BlockGetter level,
            final BlockPos pos,
            final Direction direction,
            final CallbackInfoReturnable<Integer> cir
    ) {
        if (direction == Direction.DOWN && NavigationTableRedstone.isRouteComplete(level, pos)) {
            cir.setReturnValue(15);
        }
    }

    @Inject(method = "getDirectSignal", at = @At("HEAD"), cancellable = true)
    private void createAeronauticsNavigation$getDirectSignal(
            final BlockState state,
            final BlockGetter level,
            final BlockPos pos,
            final Direction direction,
            final CallbackInfoReturnable<Integer> cir
    ) {
        if (direction == Direction.DOWN && NavigationTableRedstone.isRouteComplete(level, pos)) {
            cir.setReturnValue(15);
        }
    }
}
