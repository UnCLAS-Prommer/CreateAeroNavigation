package com.unclasprommer.aeronavigation.screen;

import com.mojang.blaze3d.platform.InputConstants;
import com.unclasprommer.aeronavigation.block.entity.VorDmeBeaconBlockEntity;
import com.unclasprommer.aeronavigation.network.RenameVorDmeBeaconPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

public class VorDmeBeaconScreen extends AbstractContainerScreen<VorDmeBeaconMenu> {
    private static final int PANEL_COLOR = 0xFF2F343B;
    private static final int CONTENT_COLOR = 0xFFF0F0F0;
    private static final int LABEL_COLOR = 0xFF30343A;

    private EditBox nameEdit;

    public VorDmeBeaconScreen(final VorDmeBeaconMenu menu, final Inventory playerInventory, final Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 220;
        this.imageHeight = 112;
    }

    @Override
    protected void init() {
        super.init();

        final int fieldX = this.leftPos + 12;
        final int fieldY = this.topPos + 40;
        this.nameEdit = new EditBox(
                this.font,
                fieldX,
                fieldY,
                this.imageWidth - 24,
                20,
                Component.translatable("screen.create_aeronautics_navigation.vordme_beacon.name")
        );
        this.nameEdit.setMaxLength(VorDmeBeaconBlockEntity.MAX_STATION_NAME_LENGTH);
        this.nameEdit.setValue(this.menu.getStationName());
        this.nameEdit.setFocused(true);
        this.addRenderableWidget(this.nameEdit);
        this.setInitialFocus(this.nameEdit);

        final int buttonY = this.topPos + 78;
        this.addRenderableWidget(Button.builder(
                        Component.translatable("screen.create_aeronautics_navigation.vordme_beacon.save"),
                        button -> this.saveAndClose()
                )
                .bounds(this.leftPos + 12, buttonY, 94, 20)
                .build());
        this.addRenderableWidget(Button.builder(
                        Component.translatable("screen.create_aeronautics_navigation.vordme_beacon.cancel"),
                        button -> this.closeWithoutSaving()
                )
                .bounds(this.leftPos + 114, buttonY, 94, 20)
                .build());
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
        graphics.drawString(
                this.font,
                Component.translatable("screen.create_aeronautics_navigation.vordme_beacon.name"),
                12,
                28,
                LABEL_COLOR,
                false
        );
    }

    @Override
    public boolean keyPressed(final int keyCode, final int scanCode, final int modifiers) {
        if (keyCode == InputConstants.KEY_RETURN || keyCode == InputConstants.KEY_NUMPADENTER) {
            this.saveAndClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void saveAndClose() {
        PacketDistributor.sendToServer(new RenameVorDmeBeaconPacket(this.menu.getPos(), this.nameEdit.getValue()));
        this.closeWithoutSaving();
    }

    private void closeWithoutSaving() {
        if (this.minecraft != null && this.minecraft.player != null) {
            this.minecraft.player.closeContainer();
        }
    }
}
