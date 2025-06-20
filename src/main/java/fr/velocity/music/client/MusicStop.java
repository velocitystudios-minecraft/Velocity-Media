package fr.velocity.music.client;

import fr.velocity.music.musicplayer.MusicPlayerManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MusicStop {
    public static void Stopmusic(String TrackId) {
        MusicPlayerManager.StopAudio(TrackId);
    }
}
