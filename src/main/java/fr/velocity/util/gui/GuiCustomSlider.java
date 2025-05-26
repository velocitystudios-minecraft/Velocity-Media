package fr.velocity.util.gui;

import fr.velocity.mod.handler.ConfigHandler;
import fr.velocity.music.musicplayer.MusicPlayerManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;

public class GuiCustomSlider extends GuiButton {
    private float sliderValue = 1.0F; // Valeur par défaut = 100%
    private boolean dragging;

    public GuiCustomSlider(int buttonId, int x, int y) {
        super(buttonId, x, y, 150, 20, "");
        this.sliderValue = ConfigHandler.VolumeGlobaux;
        this.sliderValue = Math.max(0.0F, Math.min(2.0F, this.sliderValue)); // clamp au cas où
        this.displayString = "Volume Globaux : " + (int)(sliderValue * 100) + "%";
    }

    @Override
    protected int getHoverState(boolean mouseOver) {
        return 0;
    }

    @Override
    protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            if (this.dragging) {
                this.sliderValue = (float)(mouseX - (this.x + 4)) / (float)(this.width - 8) * 2.0F;
                this.sliderValue = Math.max(0.0F, Math.min(2.0F, this.sliderValue));
                this.displayString = "Volume Globaux : " + (int)(sliderValue * 100) + "%";
                ConfigHandler.VolumeGlobaux = this.sliderValue;
                ConfigHandler.saveConfig();
                MusicPlayerManager.UpdateVolume();
            }

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            int sliderX = this.x + (int)((this.sliderValue / 2.0F) * (float)(this.width - 8));
            this.drawTexturedModalRect(sliderX, this.y, 0, 66, 4, 20);
            this.drawTexturedModalRect(sliderX + 4, this.y, 196, 66, 4, 20);
        }
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        if (super.mousePressed(mc, mouseX, mouseY)) {
            this.sliderValue = (float)(mouseX - (this.x + 4)) / (float)(this.width - 8) * 2.0F;
            this.sliderValue = Math.max(0.0F, Math.min(2.0F, this.sliderValue));
            this.displayString = "Volume Globaux : " + (int)(sliderValue * 100) + "%";
            this.dragging = true;
            ConfigHandler.VolumeGlobaux = this.sliderValue;
            ConfigHandler.saveConfig();
            MusicPlayerManager.UpdateVolume();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY) {
        this.dragging = false;
    }
}
