package fr.velocity.music.client;

import fr.velocity.music.musicplayer.MusicPlayerManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static fr.velocity.music.util.DebugRenderer.zones;

public class ClientManager {
    public ClientManager() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        if (event.getWorld().isRemote) {
            MusicPlayerManager.stopAudio("ALL");
            zones.clear();
        }
    }
}
