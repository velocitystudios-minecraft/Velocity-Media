package fr.velocity.music.client;

import fr.velocity.music.lavaplayer.api.queue.ITrackManager;
import fr.velocity.music.musicplayer.MusicPlayerManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Objects;

@SideOnly(Side.CLIENT)
public class MusicPause {
    public static void Pausemusic(String IsPause) {
        final ITrackManager manager = MusicPlayerManager.getPlayer().getTrackManager();
        if (Objects.equals(IsPause, "true")) {
            if (!manager.isPaused()) {
                manager.setPaused(true);
            }
        } else {
            if (manager.isPaused()) {
                manager.setPaused(false);
            }
        }
    }
}
