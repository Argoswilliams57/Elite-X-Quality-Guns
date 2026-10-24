package com.argos_garcia.elitex.fabric.client;

import com.argos_garcia.elitex.fabric.config.EliteXConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

/**
 * Minimal vanilla-style configuration screen for Elite X Quality Guns (Fabric).
 *
 * <p>Edits the existing COMMON config only: the toggle is applied on <em>Save</em> through
 * {@link EliteXConfig#setAllowAimingDuringReload(boolean)}, which writes the real
 * {@code ForgeConfigSpec} value (Forge Config API Port) and saves {@code config/elitex-common.toml}.
 * Cancel (and closing the screen) leaves the config untouched.</p>
 */
public class EliteXConfigScreen extends Screen {
    private static final Text DESCRIPTION =
            Text.literal("Default ADS behaviour while reloading, for guns without \"allow_aim_during_reload\".");

    private final Screen parent;
    private boolean pendingValue;

    public EliteXConfigScreen(Screen parent) {
        super(Text.literal("Elite X Quality Guns"));
        this.parent = parent;
        this.pendingValue = EliteXConfig.allowAimingDuringReload();
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int toggleY = this.height / 4 + 48;

        this.addDrawableChild(ButtonWidget.builder(toggleLabel(), button -> {
            this.pendingValue = !this.pendingValue;
            button.setMessage(toggleLabel());
        }).dimensions(centerX - 100, toggleY, 200, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Save"), button -> {
            EliteXConfig.setAllowAimingDuringReload(this.pendingValue);
            this.close();
        }).dimensions(centerX - 102, toggleY + 40, 100, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Cancel"), button -> this.close())
                .dimensions(centerX + 2, toggleY + 40, 100, 20).build());
    }

    private Text toggleLabel() {
        return Text.literal("Allow Aiming During Reload: " + (this.pendingValue ? "ON" : "OFF"));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        context.drawCenteredTextWithShadow(this.textRenderer, DESCRIPTION, this.width / 2, this.height / 4 + 20, 0xA0A0A0);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        if (this.client != null) {
            this.client.setScreen(this.parent);
        }
    }
}
