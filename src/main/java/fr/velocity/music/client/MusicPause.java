package fr.velocity.music.client;

import fr.velocity.music.musicplayer.MusicPlayerManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Objects;

@SideOnly(Side.CLIENT)
public class MusicPause {
    public static void Pausemusic(String TrackId, String IsPause) {
        if (Objects.equals(IsPause, "true")) {
            MusicPlayerManager.Pause(TrackId, true);
        } else {
            MusicPlayerManager.Pause(TrackId, false);
        }
    }
}
