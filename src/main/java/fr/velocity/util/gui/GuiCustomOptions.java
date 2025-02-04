package fr.velocity.util.gui;

import fr.velocity.util.gui.GuiCustomSlider;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class GuiCustomOptions extends GuiScreen {
    private GuiScreen parentScreen;

    public GuiCustomOptions(GuiScreen parentScreen) {
        this.parentScreen = parentScreen;
    }

    @Override
    public void initGui() {
        this.buttonList.add(new GuiCustomSlider(100, this.width / 2 - 75, this.height / 6 + 24 * 6 - 12));
        this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 168, "Done"));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 200) {
            this.mc.displayGuiScreen(this.parentScreen);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, "Options Velocity Media", this.width / 2, this.height / 6 + 15, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
