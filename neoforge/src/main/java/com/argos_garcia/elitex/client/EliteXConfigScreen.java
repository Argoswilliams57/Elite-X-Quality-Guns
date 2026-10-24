package com.argos_garcia.elitex.client;

import com.argos_garcia.elitex.config.EliteXConfig;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;


public class EliteXConfigScreen extends Screen {
    private static final Component TITLE = Component.literal("Elite X Quality Guns");
    private static final Component DESCRIPTION =
            Component.literal("Default ADS behaviour while reloading, for guns without \"allow_aim_during_reload\".");

    private final Screen parent;
    private boolean pendingValue;

    public EliteXConfigScreen(Screen parent) {
        super(TITLE);
        this.parent = parent;
        this.pendingValue = EliteXConfig.allowAimingDuringReload();
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int toggleY = this.height / 4 + 48;

        this.addRenderableWidget(Button.builder(toggleLabel(), button -> {
            this.pendingValue = !this.pendingValue;
            button.setMessage(toggleLabel());
        }).bounds(centerX - 100, toggleY, 200, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("Save"), button -> {
            EliteXConfig.setAllowAimingDuringReload(this.pendingValue);
            this.onClose();
        }).bounds(centerX - 102, toggleY + 40, 100, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("Cancel"), button -> this.onClose())
                .bounds(centerX + 2, toggleY + 40, 100, 20).build());
    }

    private Component toggleLabel() {
        return Component.literal("Allow Aiming During Reload: " + (this.pendingValue ? "ON" : "OFF"));
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        graphics.centeredText(this.font, this.title, this.width / 2, 20, 0xFFFFFF);
        graphics.centeredText(this.font, DESCRIPTION, this.width / 2, this.height / 4 + 20, 0xA0A0A0);
    }

    @Override
    public void onClose() {
        if (this.minecraft != null) {
            // 26.3 moved the active screen to Minecraft#gui and replaced setScreen(Screen)
            // with setScreenAndShow(Screen).
            this.minecraft.setScreenAndShow(this.parent);
        }
    }
}
