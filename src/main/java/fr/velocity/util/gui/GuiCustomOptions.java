package fr.velocity.util.gui;

import fr.velocity.music.lavaplayer.api.queue.ITrackManager;
import fr.velocity.music.musicplayer.CustomPlayer;
import fr.velocity.music.util.DebugRenderer;
import fr.velocity.util.gui.GuiCustomSlider;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.math.BlockPos;

import java.util.Map;
import java.util.Objects;

import static fr.velocity.music.musicplayer.MusicPlayerManager.playerCache;

public class GuiCustomOptions extends GuiScreen {
    private GuiScreen parentScreen;
    private static boolean debugMode = false;

    public GuiCustomOptions(GuiScreen parentScreen) {
        this.parentScreen = parentScreen;
    }


    @Override
    public void initGui() {
        this.buttonList.add(new GuiCustomSlider(100, this.width / 2 - 75, this.height / 6 + 24 * 6 - 12));
        this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 168, "Done"));
        this.buttonList.add(new GuiButton(101, this.width / 2 - 100, this.height / 6 + 24 * 2, "Debug : " + (debugMode ? "ON" : "OFF")));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 200) {
            this.mc.displayGuiScreen(this.parentScreen);
        }
        if (button.id == 101) {
            debugMode = !debugMode;
            button.displayString = "Debug : " + (debugMode ? "ON" : "OFF");

            if(debugMode == true) {
                System.out.println("Mode debug activé");
                DebugRenderer.INSTANCE.enableDebug();
            } else {
                DebugRenderer.INSTANCE.disableDebug();
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, "Options Velocity Media", this.width / 2, this.height / 6 + 15, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
