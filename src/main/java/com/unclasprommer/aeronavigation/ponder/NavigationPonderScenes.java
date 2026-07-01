package com.unclasprommer.aeronavigation.ponder;

import com.unclasprommer.aeronavigation.block.ModBlocks;
import com.unclasprommer.aeronavigation.block.entity.VorDmeBeaconBlockEntity;
import com.unclasprommer.aeronavigation.item.ModItems;
import com.unclasprommer.aeronavigation.navigation.RouteData;
import com.unclasprommer.aeronavigation.navigation.RouteWaypoint;
import dev.simulated_team.simulated.content.blocks.nav_table.NavTableBlockEntity;
import dev.simulated_team.simulated.index.SimBlocks;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.RedstoneLampBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public final class NavigationPonderScenes {
    private static final String AERODROME = "AERODROME-A";
    private static final String RIDGE = "RIDGE-B";
    private static final String HARBOR = "HARBOR-C";

    private NavigationPonderScenes() {
    }

    public static void vordmeBeacon(final SceneBuilder scene, final SceneBuildingUtil util) {
        scene.title("vordme_beacon", "Marking VOR/DME Waypoints");
        scene.configureBasePlate(0, 0, 7);
        scene.scaleSceneView(0.85F);
        scene.showBasePlate();
        scene.idle(10);

        final BlockPos firstBeacon = util.grid().at(1, 1, 2);
        final BlockPos secondBeacon = util.grid().at(3, 1, 3);
        final BlockPos thirdBeacon = util.grid().at(5, 1, 4);
        final Selection beacons = util.select().position(firstBeacon)
                .add(util.select().position(secondBeacon))
                .add(util.select().position(thirdBeacon));

        revealBeacon(scene, util, firstBeacon, AERODROME);
        revealBeacon(scene, util, secondBeacon, RIDGE);
        revealBeacon(scene, util, thirdBeacon, HARBOR);
        scene.idle(10);

        scene.overlay().showText(70)
                .attachKeyFrame()
                .text("VOR/DME Beacons mark fixed navigation waypoints in the world")
                .placeNearTarget()
                .pointAt(util.vector().topOf(firstBeacon));
        scene.idle(80);

        scene.overlay().showControls(util.vector().blockSurface(firstBeacon, Direction.UP), Pointing.DOWN, 35)
                .rightClick();
        scene.overlay().showText(70)
                .text("Right-click a beacon with an empty hand to rename it")
                .placeNearTarget()
                .pointAt(util.vector().topOf(firstBeacon));
        scene.idle(80);

        scene.overlay().showOutline(PonderPalette.INPUT, "beacon_names", beacons, 70);
        scene.overlay().showText(70)
                .attachKeyFrame()
                .text("The beacon name is what Route Cards will record")
                .placeNearTarget()
                .pointAt(util.vector().topOf(secondBeacon));
        scene.idle(80);

        scene.overlay().showControls(util.vector().blockSurface(thirdBeacon, Direction.UP), Pointing.DOWN, 35)
                .rightClick()
                .whileSneaking();
        scene.overlay().showText(80)
                .text("Sneak-right-click the beacon with an empty hand to check its name and coordinates")
                .placeNearTarget()
                .pointAt(util.vector().topOf(thirdBeacon));
        scene.idle(90);
    }

    public static void routeCardRecording(final SceneBuilder scene, final SceneBuildingUtil util) {
        scene.title("route_card_recording", "Recording Route Cards");
        scene.configureBasePlate(0, 0, 7);
        scene.scaleSceneView(0.85F);
        scene.showBasePlate();
        scene.idle(10);

        final BlockPos firstBeacon = util.grid().at(1, 1, 2);
        final BlockPos secondBeacon = util.grid().at(3, 1, 3);
        final BlockPos thirdBeacon = util.grid().at(5, 1, 4);

        revealBeacon(scene, util, firstBeacon, AERODROME);
        revealBeacon(scene, util, secondBeacon, RIDGE);
        revealBeacon(scene, util, thirdBeacon, HARBOR);
        scene.idle(10);

        final ItemStack routeCard = new ItemStack(ModItems.ROUTE_CARD.get());
        scene.world().createItemEntity(util.vector().centerOf(3, 2, 1), Vec3.ZERO, routeCard);
        scene.overlay().showText(70)
                .attachKeyFrame()
                .text("Route Cards store an ordered list of waypoints")
                .independent(30);
        scene.idle(80);

        scene.overlay().showControls(util.vector().blockSurface(firstBeacon, Direction.UP), Pointing.DOWN, 35)
                .rightClick()
                .withItem(routeCard);
        scene.overlay().showText(70)
                .text("Right-click a VOR/DME Beacon with the card to append it to the end")
                .placeNearTarget()
                .pointAt(util.vector().topOf(firstBeacon));
        scene.idle(40);
        scene.overlay().showText(60)
                .text("1. AERODROME-A")
                .independent(55)
                .colored(PonderPalette.INPUT);
        scene.idle(55);

        addRouteWaypoint(scene, util, routeCard, secondBeacon, "1. AERODROME-A\n2. RIDGE-B");
        addRouteWaypoint(scene, util, routeCard, thirdBeacon, "1. AERODROME-A\n2. RIDGE-B\n3. HARBOR-C");

        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("Record several beacons to build a route")
                .independent(25);
        scene.idle(90);

        scene.overlay().showControls(util.vector().of(3.5, 2.2, 1.5), Pointing.DOWN, 35)
                .rightClick()
                .withItem(routeCard);
        scene.overlay().showText(75)
                .text("Right-click the air to open the editor")
                .independent(35)
                .colored(PonderPalette.BLUE);
        scene.idle(80);

        scene.overlay().showText(85)
                .attachKeyFrame()
                .text("The editor can reorder, delete, set targets, and add coordinate waypoints")
                .independent(35);
        scene.idle(95);

        scene.overlay().showControls(util.vector().of(3.5, 2.2, 1.5), Pointing.DOWN, 35)
                .rightClick()
                .whileSneaking()
                .withItem(routeCard);
        scene.overlay().showText(75)
                .text("Sneak-right-click the air to clear the entire route")
                .independent(35)
                .colored(PonderPalette.RED);
        scene.idle(85);

        scene.overlay().showControls(util.vector().blockSurface(secondBeacon, Direction.UP), Pointing.DOWN, 35)
                .rightClick()
                .whileSneaking()
                .withItem(routeCard);
        scene.overlay().showText(85)
                .attachKeyFrame()
                .text("Sneak-right-click a recorded beacon to remove only that beacon from the card")
                .placeNearTarget()
                .pointAt(util.vector().topOf(secondBeacon));
        scene.idle(95);
    }

    public static void routeCardNavigationTable(final SceneBuilder scene, final SceneBuildingUtil util) {
        scene.title("route_card_navigation_table", "Navigating Routes");
        scene.configureBasePlate(0, 0, 7);
        scene.scaleSceneView(0.82F);
        scene.showBasePlate();
        scene.idle(10);

        final BlockPos navTable = util.grid().at(3, 1, 3);
        final BlockPos outputLamp = navTable.above();
        final BlockPos firstBeacon = util.grid().at(1, 1, 1);
        final BlockPos secondBeacon = util.grid().at(5, 1, 2);
        final BlockPos thirdBeacon = util.grid().at(4, 1, 5);

        revealBlock(scene, util, navTable, SimBlocks.NAVIGATION_TABLE.get().defaultBlockState()
                .setValue(DirectionalBlock.FACING, Direction.UP));
        revealBlock(scene, util, outputLamp, Blocks.REDSTONE_LAMP.defaultBlockState());
        revealBeacon(scene, util, firstBeacon, AERODROME);
        revealBeacon(scene, util, secondBeacon, RIDGE);
        revealBeacon(scene, util, thirdBeacon, HARBOR);
        scene.idle(10);

        final ItemStack routeCard = createRouteCard(firstBeacon, secondBeacon, thirdBeacon);
        scene.overlay().showControls(util.vector().blockSurface(navTable, Direction.UP), Pointing.DOWN, 40)
                .withItem(routeCard);
        scene.overlay().showText(75)
                .attachKeyFrame()
                .text("Insert a Route Card into the Aeronautics Navigation Table")
                .placeNearTarget()
                .pointAt(util.vector().topOf(navTable));
        scene.idle(20);
        scene.world().modifyBlockEntityNBT(util.select().position(navTable), NavTableBlockEntity.class,
                tag -> tag.put("CurrentStack", routeCard.saveOptional(scene.world().getHolderLookupProvider())), true);
        scene.idle(70);

        showTargetLine(scene, util, navTable, firstBeacon, "The table targets the current waypoint on the card");
        showTargetLine(scene, util, navTable, secondBeacon, "When the craft reaches a waypoint, the card advances to the next one");
        showTargetLine(scene, util, navTable, thirdBeacon, "The final waypoint completes the route");

        scene.world().modifyBlock(outputLamp,
                state -> state.setValue(RedstoneLampBlock.LIT, true), false);
        scene.effects().indicateRedstone(outputLamp);
        scene.overlay().showText(85)
                .attachKeyFrame()
                .text("On completion, the Navigation Table emits redstone upward")
                .placeNearTarget()
                .pointAt(util.vector().topOf(outputLamp))
                .colored(PonderPalette.OUTPUT);
        scene.idle(95);

        scene.overlay().showControls(util.vector().of(3.5, 2.25, 3.5), Pointing.DOWN, 40)
                .rightClick()
                .withItem(routeCard);
        scene.overlay().showText(90)
                .text("Open the Route Card editor and press a row's target button to choose the current waypoint manually")
                .independent(35)
                .colored(PonderPalette.BLUE);
        scene.idle(100);
    }

    private static void revealBeacon(final SceneBuilder scene, final SceneBuildingUtil util, final BlockPos pos, final String name) {
        placeBeacon(scene, pos, name);
        revealPosition(scene, util, pos);
    }

    private static void placeBeacon(final SceneBuilder scene, final BlockPos pos, final String name) {
        scene.world().setBlock(pos, ModBlocks.VORDME_BEACON.get().defaultBlockState(), false);
        scene.world().modifyBlockEntity(pos, VorDmeBeaconBlockEntity.class, beacon -> beacon.setStationName(name));
    }

    private static void revealBlock(final SceneBuilder scene, final SceneBuildingUtil util, final BlockPos pos, final BlockState state) {
        scene.world().setBlock(pos, state, false);
        revealPosition(scene, util, pos);
    }

    private static void revealPosition(final SceneBuilder scene, final SceneBuildingUtil util, final BlockPos pos) {
        scene.world().showSection(util.select().position(pos), Direction.DOWN);
        scene.idle(8);
    }

    private static void addRouteWaypoint(
            final SceneBuilder scene,
            final SceneBuildingUtil util,
            final ItemStack routeCard,
            final BlockPos beacon,
            final String routeList
    ) {
        scene.overlay().showControls(util.vector().blockSurface(beacon, Direction.UP), Pointing.DOWN, 30)
                .rightClick()
                .withItem(routeCard);
        scene.idle(35);
        scene.overlay().showText(65)
                .text(routeList)
                .independent(55)
                .colored(PonderPalette.INPUT);
        scene.idle(50);
    }

    private static ItemStack createRouteCard(final BlockPos firstBeacon, final BlockPos secondBeacon, final BlockPos thirdBeacon) {
        final ItemStack routeCard = new ItemStack(ModItems.ROUTE_CARD.get());
        RouteData.addWaypoint(routeCard, new RouteWaypoint(AERODROME, GlobalPos.of(Level.OVERWORLD, firstBeacon)));
        RouteData.addWaypoint(routeCard, new RouteWaypoint(RIDGE, GlobalPos.of(Level.OVERWORLD, secondBeacon)));
        RouteData.addWaypoint(routeCard, new RouteWaypoint(HARBOR, GlobalPos.of(Level.OVERWORLD, thirdBeacon)));
        return routeCard;
    }

    private static void showTargetLine(
            final SceneBuilder scene,
            final SceneBuildingUtil util,
            final BlockPos navTable,
            final BlockPos beacon,
            final String text
    ) {
        scene.overlay().showBigLine(
                PonderPalette.INPUT,
                util.vector().topOf(navTable),
                util.vector().topOf(beacon),
                70
        );
        scene.effects().indicateSuccess(beacon);
        scene.overlay().showText(70)
                .attachKeyFrame()
                .text(text)
                .placeNearTarget()
                .pointAt(util.vector().topOf(beacon));
        scene.idle(80);
    }
}
