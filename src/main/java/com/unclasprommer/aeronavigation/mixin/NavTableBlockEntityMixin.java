package com.unclasprommer.aeronavigation.mixin;

import com.unclasprommer.aeronavigation.integration.simulated.NavigationTableRedstone;
import dev.simulated_team.simulated.content.blocks.nav_table.NavTableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NavTableBlockEntity.class)
public abstract class NavTableBlockEntityMixin extends BlockEntity {
    @Unique
    private boolean createAeronauticsNavigation$wasRouteComplete;

    protected NavTableBlockEntityMixin(final BlockEntityType<?> type, final BlockPos pos, final BlockState blockState) {
        super(type, pos, blockState);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void createAeronauticsNavigation$tickRouteCompletion(final CallbackInfo ci) {
        if (this.level == null || this.level.isClientSide) {
            return;
        }

        final boolean routeComplete = NavigationTableRedstone.isRouteComplete((NavTableBlockEntity) (Object) this);
        if (routeComplete == this.createAeronauticsNavigation$wasRouteComplete) {
            return;
        }

        this.createAeronauticsNavigation$wasRouteComplete = routeComplete;
        final BlockState state = this.getBlockState();
        this.level.updateNeighborsAt(this.worldPosition, state.getBlock());
        this.level.updateNeighborsAt(this.worldPosition.above(), state.getBlock());
        this.setChanged();
    }
}
