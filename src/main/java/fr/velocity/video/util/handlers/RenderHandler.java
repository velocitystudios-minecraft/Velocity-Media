package fr.velocity.video.util.handlers;

import fr.velocity.Main;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(value = Side.CLIENT, modid = Main.modid)
public class RenderHandler {

    @SubscribeEvent
    public static void registerEntityRenders(ModelRegistryEvent event) {

    }
}
