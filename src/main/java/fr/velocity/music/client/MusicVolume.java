package fr.velocity.music.client;

import fr.velocity.music.musicplayer.MusicPlayerManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MusicVolume {
    public static void Volumemusic(String TrackId, int volume) {
        MusicPlayerManager.ChangeVolume(TrackId, volume);
    }
}
