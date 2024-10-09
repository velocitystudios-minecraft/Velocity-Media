package fr.velocity.music.client;

import fr.velocity.music.lavaplayer.api.audio.IAudioTrack;
import fr.velocity.music.lavaplayer.api.queue.ITrackManager;
import fr.velocity.music.musicplayer.MusicPlayerManager;
import fr.velocity.music.musicplayer.playlist.LoadedTracks;
import fr.velocity.music.musicplayer.playlist.Playlist;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;

@SideOnly(Side.CLIENT)
public class MusicPlay {
    public static void Playmusic(String url, int volume) {
        Playlist playlist = new Playlist();
        MusicPlayerManager.getPlayer().getTrackSearch().getTracks(url, result -> {
            if (result.hasError()) {
                System.out.println(new TextComponentString(result.getErrorMessage()));
            } else {
                final IAudioTrack track = result.getTrack();

                final Runnable runnable = () -> {
                    final ITrackManager manager = MusicPlayerManager.getPlayer().getTrackManager();
                    playlist.add(track);
                    System.out.println(track);
                    Pair<LoadedTracks, IAudioTrack> pair = playlist.getFirstTrack();
                    playlist.setPlayable(pair.getLeft(), pair.getRight());
                    manager.setTrackQueue(playlist);
                    manager.start();
                    MusicPlayerManager.getPlayer().setVolume(volume);
                };

                if (!playlist.isLoaded()) {
                    playlist.load(runnable);
                } else {
                    runnable.run();
                }
            }
        });
    }
}
