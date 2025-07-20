package fr.velocity.music.client;


import fr.velocity.mod.handler.ConfigHandler;
import fr.velocity.music.lavaplayer.api.IMusicPlayer;
import fr.velocity.music.lavaplayer.api.audio.IAudioTrack;
import fr.velocity.music.lavaplayer.api.audio.IPlayingTrack;
import fr.velocity.music.lavaplayer.api.queue.ITrackManager;
import fr.velocity.music.musicplayer.CustomPlayer;
import fr.velocity.music.musicplayer.MusicPlayerManager;
import fr.velocity.music.musicplayer.playlist.LoadedTracks;
import fr.velocity.music.musicplayer.playlist.Playlist;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SideOnly(Side.CLIENT)
public class MusicTrack {
    public static void trackMusic(String url, int volume, String TrackId, String Option) {
        Playlist playlist = new Playlist();

        Thread musicthread = new Thread(() -> {
            CustomPlayer NewPlayer = MusicPlayerManager.getCustomPlayer(TrackId, volume, "Track", 0, 0, 0, 0, Option, "None", "None", 0, 0, 0, "None", 0);
            NewPlayer.getPlayer().getTrackSearch().getTracks(url, result -> {
                if (result.hasError()) {
                    System.out.println(new TextComponentString(result.getErrorMessage()));
                } else {
                    final IAudioTrack track = result.getTrack();

                    if (Option.contains("--repeat")) {
                        playlist.RepeatMode = "true";
                    } else {
                        playlist.RepeatMode = "false";
                    }

                    final Runnable runnable = () -> {
                        final ITrackManager manager = NewPlayer.getPlayer().getTrackManager();

                        playlist.add(track);
                        Pair<LoadedTracks, IAudioTrack> pair = playlist.getFirstTrack();
                        playlist.setPlayable(pair.getLeft(), pair.getRight());

                        if (Option.contains("--noplayagain")) {
                            if(NewPlayer.getPlayer().getTrackManager().getCurrentTrack() != null) {
                                if(Objects.equals(result.getTrack().getInfo().getTitle(), NewPlayer.getPlayer().getTrackManager().getCurrentTrack().getInfo().getTitle())) {
                                    return;
                                }
                            }
                        }

                        if (Option.contains("--onlyplaying")) {
                            if(NewPlayer.getPlayer().getTrackManager().getCurrentTrack() == null) {
                                return;
                            }
                        }

                        manager.setTrackQueue(playlist);
                        manager.start();

                        int realvolume = (int) (ConfigHandler.VolumeGlobaux * volume);
                        NewPlayer.getPlayer().setVolume(realvolume);

                        long StartTime = 0;
                        if (Option.contains("--position")) {
                            Pattern pattern = Pattern.compile("--position(\\d+)");
                            Matcher matcher = pattern.matcher(Option);
                            if (matcher.find()) {
                                StartTime = Integer.parseInt(matcher.group(1));
                                System.out.println("[Velocity Media] --position trouvé : " + StartTime);
                                IPlayingTrack currentTrack = NewPlayer.getPlayer().getTrackManager().getCurrentTrack();
                                if(currentTrack!=null) {
                                    if(currentTrack.getDuration() < StartTime) {
                                        System.out.println("[Velocity Media] Duration indiqué excède la limite de " + currentTrack.getDuration());
                                    } else {
                                        while (1==1) {
                                            long CurrentPosition = NewPlayer.getPlayer().getTrackManager().getCurrentTrack().getPosition();
                                            if (CurrentPosition > 0) {
                                                System.out.println("[Velocity Media] Position mis avec succès avec un temps max de " + currentTrack.getDuration());
                                                currentTrack.setPosition(StartTime);
                                                break;
                                            }
                                            try {
                                                Thread.sleep(2);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                } else {
                                    System.out.println("[Velocity Media] CurrentTrack actuellement invalide");
                                }
                            } else {
                                System.out.println("[Velocity Media] --position invalide");
                            }
                        }
                    };

                    if (!playlist.isLoaded()) {
                        playlist.load(runnable);
                    } else {
                        runnable.run();
                    }
                }
            });
        });
        musicthread.start();
    }
}
