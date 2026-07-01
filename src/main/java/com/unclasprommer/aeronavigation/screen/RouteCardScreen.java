package com.unclasprommer.aeronavigation.screen;

import com.mojang.blaze3d.platform.InputConstants;
import com.unclasprommer.aeronavigation.navigation.RouteData;
import com.unclasprommer.aeronavigation.navigation.RouteWaypoint;
import com.unclasprommer.aeronavigation.network.EditRouteCardPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RouteCardScreen extends AbstractContainerScreen<RouteCardMenu> {
    private static final int PANEL_COLOR = 0xFF2F343B;
    private static final int CONTENT_COLOR = 0xFFF0F0F0;
    private static final int LABEL_COLOR = 0xFF30343A;
    private static final int ERROR_COLOR = 0xFFB02020;
    private static final int ACTIVE_COLOR = 0xFF126E82;
    private static final int ROWS = 7;
    private static final int ROW_HEIGHT = 20;

    private final List<RouteWaypoint> waypoints;
    private int routeIndex;
    private int scrollOffset;
    private boolean addingWaypoint;
    private Component errorMessage = Component.empty();

    private EditBox xEdit;
    private EditBox yEdit;
    private EditBox zEdit;

    public RouteCardScreen(final RouteCardMenu menu, final Inventory playerInventory, final Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 410;
        this.imageHeight = 228;
        this.waypoints = new ArrayList<>(menu.getWaypoints());
        this.routeIndex = menu.getRouteIndex();
    }

    @Override
    protected void init() {
        super.init();
        this.rebuildRouteCardWidgets();
    }

    private void rebuildRouteCardWidgets() {
        this.clearWidgets();
        if (this.addingWaypoint) {
            this.initAddWidgets();
        } else {
            this.initListWidgets();
        }
    }

    private void initListWidgets() {
        final int rowStartY = this.topPos + 38;
        final int visibleRows = Math.min(ROWS, Math.max(0, this.waypoints.size() - this.scrollOffset));
        for (int row = 0; row < visibleRows; row++) {
            final int index = this.scrollOffset + row;
            final int y = rowStartY + row * ROW_HEIGHT;
            final Button target = Button.builder(Component.translatable("screen.create_aeronautics_navigation.route_card.target"), button -> this.setTargetWaypoint(index))
                    .bounds(this.leftPos + 214, y, 50, 18)
                    .build();
            target.active = index != this.routeIndex;
            this.addRenderableWidget(target);

            final Button up = Button.builder(Component.translatable("screen.create_aeronautics_navigation.route_card.move_up"), button -> this.moveWaypoint(index, -1))
                    .bounds(this.leftPos + 268, y, 36, 18)
                    .build();
            up.active = index > 0;
            this.addRenderableWidget(up);

            final Button down = Button.builder(Component.translatable("screen.create_aeronautics_navigation.route_card.move_down"), button -> this.moveWaypoint(index, 1))
                    .bounds(this.leftPos + 308, y, 42, 18)
                    .build();
            down.active = index < this.waypoints.size() - 1;
            this.addRenderableWidget(down);

            this.addRenderableWidget(Button.builder(Component.translatable("screen.create_aeronautics_navigation.route_card.delete"), button -> this.removeWaypoint(index))
                    .bounds(this.leftPos + 354, y, 44, 18)
                    .build());
        }

        final int bottomY = this.topPos + this.imageHeight - 28;
        this.addRenderableWidget(Button.builder(Component.translatable("screen.create_aeronautics_navigation.route_card.add_coordinate"), button -> this.openAddForm())
                .bounds(this.leftPos + 12, bottomY, 92, 20)
                .build());

        final Button previous = Button.builder(Component.translatable("screen.create_aeronautics_navigation.route_card.previous"), button -> {
                    this.scrollOffset = Math.max(0, this.scrollOffset - ROWS);
                    this.rebuildRouteCardWidgets();
                })
                .bounds(this.leftPos + 114, bottomY, 70, 20)
                .build();
        previous.active = this.scrollOffset > 0;
        this.addRenderableWidget(previous);

        final Button next = Button.builder(Component.translatable("screen.create_aeronautics_navigation.route_card.next"), button -> {
                    this.scrollOffset = Math.min(this.maxScrollOffset(), this.scrollOffset + ROWS);
                    this.rebuildRouteCardWidgets();
                })
                .bounds(this.leftPos + 190, bottomY, 70, 20)
                .build();
        next.active = this.scrollOffset < this.maxScrollOffset();
        this.addRenderableWidget(next);

        this.addRenderableWidget(Button.builder(Component.translatable("screen.create_aeronautics_navigation.route_card.close"), button -> this.closeScreen())
                .bounds(this.leftPos + this.imageWidth - 104, bottomY, 92, 20)
                .build());
    }

    private void initAddWidgets() {
        final BlockPos defaultPos = this.minecraft != null && this.minecraft.player != null
                ? this.minecraft.player.blockPosition()
                : BlockPos.ZERO;
        final int fieldX = this.leftPos + 78;
        final int fieldWidth = 110;
        this.xEdit = this.coordinateEditBox(fieldX, this.topPos + 52, defaultPos.getX());
        this.yEdit = this.coordinateEditBox(fieldX, this.topPos + 82, defaultPos.getY());
        this.zEdit = this.coordinateEditBox(fieldX, this.topPos + 112, defaultPos.getZ());
        this.addRenderableWidget(this.xEdit);
        this.addRenderableWidget(this.yEdit);
        this.addRenderableWidget(this.zEdit);

        final int buttonY = this.topPos + this.imageHeight - 48;
        this.addRenderableWidget(Button.builder(Component.translatable("screen.create_aeronautics_navigation.route_card.confirm"), button -> this.confirmAdd())
                .bounds(this.leftPos + 78, buttonY, fieldWidth, 20)
                .build());
        this.addRenderableWidget(Button.builder(Component.translatable("screen.create_aeronautics_navigation.route_card.cancel"), button -> {
                    this.addingWaypoint = false;
                    this.errorMessage = Component.empty();
                    this.rebuildRouteCardWidgets();
                })
                .bounds(this.leftPos + 198, buttonY, fieldWidth, 20)
                .build());

        this.setInitialFocus(this.xEdit);
        this.setFocused(this.xEdit);
        this.xEdit.setFocused(true);
        this.xEdit.moveCursorToEnd(false);
        this.xEdit.setHighlightPos(0);
    }

    private EditBox coordinateEditBox(final int x, final int y, final int value) {
        final EditBox editBox = new EditBox(this.font, x, y, 110, 20, Component.empty());
        editBox.setMaxLength(12);
        editBox.setValue(Integer.toString(value));
        return editBox;
    }

    private void moveWaypoint(final int index, final int delta) {
        if (this.waypoints.size() < 2 || index < 0 || index >= this.waypoints.size()) {
            return;
        }

        final int to = Math.clamp(index + delta, 0, this.waypoints.size() - 1);
        if (to == index) {
            return;
        }

        Collections.swap(this.waypoints, index, to);
        if (this.routeIndex == index) {
            this.routeIndex = to;
        } else if (this.routeIndex == to) {
            this.routeIndex = index;
        }
        this.scrollOffset = Math.clamp(this.scrollOffset, 0, this.maxScrollOffset());
        PacketDistributor.sendToServer(EditRouteCardPacket.move(this.menu.getHand(), index, delta));
        this.rebuildRouteCardWidgets();
    }

    private void removeWaypoint(final int index) {
        if (index < 0 || index >= this.waypoints.size()) {
            return;
        }

        this.waypoints.remove(index);
        if (this.waypoints.isEmpty()) {
            this.routeIndex = 0;
        } else if (this.routeIndex > index) {
            this.routeIndex--;
        } else {
            this.routeIndex = Math.min(this.routeIndex, this.waypoints.size() - 1);
        }
        this.scrollOffset = Math.clamp(this.scrollOffset, 0, this.maxScrollOffset());
        PacketDistributor.sendToServer(EditRouteCardPacket.remove(this.menu.getHand(), index));
        this.rebuildRouteCardWidgets();
    }

    private void setTargetWaypoint(final int index) {
        if (index < 0 || index >= this.waypoints.size() || index == this.routeIndex) {
            return;
        }

        this.routeIndex = index;
        PacketDistributor.sendToServer(EditRouteCardPacket.setTarget(this.menu.getHand(), index));
        this.rebuildRouteCardWidgets();
    }

    private void openAddForm() {
        this.addingWaypoint = true;
        this.errorMessage = Component.empty();
        this.rebuildRouteCardWidgets();
    }

    private void confirmAdd() {
        final BlockPos pos;
        try {
            pos = new BlockPos(this.parseCoordinate(this.xEdit), this.parseCoordinate(this.yEdit), this.parseCoordinate(this.zEdit));
        } catch (final NumberFormatException ignored) {
            this.errorMessage = Component.translatable("screen.create_aeronautics_navigation.route_card.invalid_coordinate");
            return;
        }

        if (this.minecraft != null && this.minecraft.player != null) {
            this.waypoints.add(new RouteWaypoint(RouteData.createCoordinateWaypointName(pos), GlobalPos.of(this.minecraft.player.level().dimension(), pos)));
        }
        if (this.waypoints.size() == 1) {
            this.routeIndex = 0;
        }
        this.scrollOffset = this.maxScrollOffset();
        PacketDistributor.sendToServer(EditRouteCardPacket.add(this.menu.getHand(), pos));
        this.addingWaypoint = false;
        this.errorMessage = Component.empty();
        this.rebuildRouteCardWidgets();
    }

    private int parseCoordinate(final EditBox editBox) {
        return Integer.parseInt(editBox.getValue().trim());
    }

    private int maxScrollOffset() {
        return Math.max(0, this.waypoints.size() - ROWS);
    }

    @Override
    public boolean keyPressed(final int keyCode, final int scanCode, final int modifiers) {
        if (this.addingWaypoint) {
            if (keyCode == InputConstants.KEY_RETURN || keyCode == InputConstants.KEY_NUMPADENTER) {
                this.confirmAdd();
                return true;
            }
            if (keyCode == InputConstants.KEY_ESCAPE) {
                this.addingWaypoint = false;
                this.errorMessage = Component.empty();
                this.rebuildRouteCardWidgets();
                return true;
            }
            if (this.handleFocusedCoordinateEditBoxKeyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(final char codePoint, final int modifiers) {
        if (this.addingWaypoint) {
            final EditBox focusedEditBox = this.getFocusedCoordinateEditBox();
            if (focusedEditBox != null) {
                return focusedEditBox.charTyped(codePoint, modifiers);
            }
        }
        return super.charTyped(codePoint, modifiers);
    }

    private boolean handleFocusedCoordinateEditBoxKeyPressed(final int keyCode, final int scanCode, final int modifiers) {
        final EditBox focusedEditBox = this.getFocusedCoordinateEditBox();
        if (focusedEditBox == null) {
            return false;
        }

        focusedEditBox.keyPressed(keyCode, scanCode, modifiers);
        return true;
    }

    private EditBox getFocusedCoordinateEditBox() {
        if (this.xEdit != null && this.xEdit.isFocused()) {
            return this.xEdit;
        }
        if (this.yEdit != null && this.yEdit.isFocused()) {
            return this.yEdit;
        }
        if (this.zEdit != null && this.zEdit.isFocused()) {
            return this.zEdit;
        }
        return null;
    }

    @Override
    public void render(final GuiGraphics graphics, final int mouseX, final int mouseY, final float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(final GuiGraphics graphics, final float partialTick, final int mouseX, final int mouseY) {
        graphics.fill(this.leftPos, this.topPos, this.leftPos + this.imageWidth, this.topPos + this.imageHeight, PANEL_COLOR);
        graphics.fill(this.leftPos + 1, this.topPos + 1, this.leftPos + this.imageWidth - 1, this.topPos + this.imageHeight - 1, CONTENT_COLOR);
    }

    @Override
    protected void renderLabels(final GuiGraphics graphics, final int mouseX, final int mouseY) {
        graphics.drawString(this.font, this.title, 12, 10, LABEL_COLOR, false);
        if (this.addingWaypoint) {
            this.renderAddLabels(graphics);
        } else {
            this.renderListLabels(graphics);
        }
    }

    private void renderListLabels(final GuiGraphics graphics) {
        graphics.drawString(this.font, Component.translatable("screen.create_aeronautics_navigation.route_card.summary", this.waypoints.size()), 12, 24, LABEL_COLOR, false);
        if (this.waypoints.isEmpty()) {
            graphics.drawString(this.font, Component.translatable("item.create_aeronautics_navigation.route_card.empty"), 12, 56, LABEL_COLOR, false);
            return;
        }

        final int visibleRows = Math.min(ROWS, Math.max(0, this.waypoints.size() - this.scrollOffset));
        for (int row = 0; row < visibleRows; row++) {
            final int index = this.scrollOffset + row;
            final RouteWaypoint waypoint = this.waypoints.get(index);
            final BlockPos pos = waypoint.pos().pos();
            final String prefix = index == this.routeIndex ? "> " : "  ";
            final String text = prefix + "#" + (index + 1) + " " + waypoint.name() + " (" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ")";
            final int color = index == this.routeIndex ? ACTIVE_COLOR : LABEL_COLOR;
            graphics.drawString(this.font, this.font.plainSubstrByWidth(text, 198), 12, 43 + row * ROW_HEIGHT, color, false);
        }
    }

    private void renderAddLabels(final GuiGraphics graphics) {
        graphics.drawString(this.font, Component.translatable("screen.create_aeronautics_navigation.route_card.add_coordinate"), 12, 28, LABEL_COLOR, false);
        graphics.drawString(this.font, Component.translatable("screen.create_aeronautics_navigation.route_card.x"), 38, 58, LABEL_COLOR, false);
        graphics.drawString(this.font, Component.translatable("screen.create_aeronautics_navigation.route_card.y"), 38, 88, LABEL_COLOR, false);
        graphics.drawString(this.font, Component.translatable("screen.create_aeronautics_navigation.route_card.z"), 38, 118, LABEL_COLOR, false);
        if (!this.errorMessage.getString().isEmpty()) {
            graphics.drawString(this.font, this.errorMessage, 78, 144, ERROR_COLOR, false);
        }
    }

    private void closeScreen() {
        if (this.minecraft != null && this.minecraft.player != null) {
            this.minecraft.player.closeContainer();
        }
    }
}
