package fr.velocity.mod.handler;

import fr.velocity.util.gui.GuiCustomOptions;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class GuiEventHandler {
    @SubscribeEvent
    public void onGuiInit(GuiScreenEvent.InitGuiEvent.Post event) {
        GuiScreen gui = event.getGui();
        if (gui instanceof GuiOptions) {
            event.getButtonList().add(new GuiButton(300, gui.width / 2 - 100, gui.height / 6 + 24 * 6, "Options Velocity Media"));
        }
    }

    @SubscribeEvent
    public void onActionPerformed(GuiScreenEvent.ActionPerformedEvent.Post event) {
        GuiScreen gui = event.getGui();
        GuiButton button = event.getButton();
        if (gui instanceof GuiOptions && button.id == 300) {
            gui.mc.displayGuiScreen(new GuiCustomOptions(gui));
        }
    }
}
