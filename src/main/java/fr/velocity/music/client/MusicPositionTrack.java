package fr.velocity.music.client;

import fr.velocity.mod.handler.ConfigHandler;
import fr.velocity.music.lavaplayer.api.IMusicPlayer;
import fr.velocity.music.lavaplayer.api.audio.IAudioTrack;
import fr.velocity.music.lavaplayer.api.audio.IPlayingTrack;
import fr.velocity.music.lavaplayer.api.queue.ITrackManager;
import fr.velocity.music.musicplayer.MusicPlayerManager;
import fr.velocity.music.musicplayer.playlist.LoadedTracks;
import fr.velocity.music.musicplayer.playlist.Playlist;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static fr.velocity.music.musicplayer.MusicPlayerManager.GetMaxVolumeFromTrackId;

@SideOnly(Side.CLIENT)
public class MusicPositionTrack {

    private static final Map<String, Thread> activeThreads = new ConcurrentHashMap<>();
    private static final Map<String, AtomicBoolean> trackControlFlags = new ConcurrentHashMap<>();

    public static void positionTrackmusic(int x, int y, int z, int radius, String url, int volume, String TrackId, String Option) {

        Playlist playlist = new Playlist();

        Thread musicthread = new Thread(() -> {
            IMusicPlayer NewPlayer = MusicPlayerManager.TestGenerate(TrackId, volume, "PositionTrack", x, y, z, radius, Option, Option, "None", 0, 0, 0, "None", 0);

            NewPlayer.getTrackSearch().getTracks(url, result -> {
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
                        final ITrackManager manager = NewPlayer.getTrackManager();
                        playlist.add(track);

                        Pair<LoadedTracks, IAudioTrack> pair = playlist.getFirstTrack();
                        playlist.setPlayable(pair.getLeft(), pair.getRight());

                        if (Option.contains("--noplayagain")) {
                            if(NewPlayer.getTrackManager().getCurrentTrack() != null) {
                                if(Objects.equals(result.getTrack().getInfo().getTitle(), NewPlayer.getTrackManager().getCurrentTrack().getInfo().getTitle())) {
                                    return;
                                }
                            }
                        }

                        if (Option.contains("--onlyplaying")) {
                            if(NewPlayer.getTrackManager().getCurrentTrack() == null) {
                                return;
                            }
                        }

                        manager.setTrackQueue(playlist);
                        manager.start();

                        int StartTime = 0;
                        if (Option.contains("--position")) {
                            Pattern pattern = Pattern.compile("--position(\\d+)");
                            Matcher matcher = pattern.matcher(Option);
                            if (matcher.find()) {
                                StartTime = Integer.parseInt(matcher.group(1));
                                System.out.println("[Velocity Media] --position trouvé : " + StartTime);
                                IPlayingTrack currentTrack = NewPlayer.getTrackManager().getCurrentTrack();
                                if(currentTrack!=null) {
                                    if(currentTrack.getDuration() < StartTime) {
                                        System.out.println("[Velocity Media] Duration indiqué excède la limite de " + currentTrack.getDuration());
                                    } else {
                                        while (1==1) {
                                            long CurrentPosition = NewPlayer.getTrackManager().getCurrentTrack().getPosition();
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

                        BlockPos position = new BlockPos(x, y, z);
                        stopThreadForTrackId(TrackId);
                        AtomicBoolean controlFlag = new AtomicBoolean(true);
                        trackControlFlags.put(TrackId, controlFlag);

                        Thread thread = new Thread(() -> playWithPosition(manager, NewPlayer, position, volume, radius, Option, controlFlag, TrackId));
                        activeThreads.put(TrackId, thread);
                        thread.start();
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

    public static void stopThreadForTrackId(String TrackId) {
        if (activeThreads.containsKey(TrackId)) {
            AtomicBoolean controlFlag = trackControlFlags.get(TrackId);
            if (controlFlag != null) {
                controlFlag.set(false);
            }

            Thread thread = activeThreads.get(TrackId);
            if (thread != null && thread.isAlive()) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            activeThreads.remove(TrackId);
            trackControlFlags.remove(TrackId);
        }
    }

    private static void playWithPosition(ITrackManager manager, IMusicPlayer player, BlockPos source, int maxVolume, int maxDistance, String option, AtomicBoolean controlFlag, String TrackId) {
        Boolean HasFade = Boolean.TRUE;
        if (option.contains("--nofade")) {
            HasFade = Boolean.FALSE;
        }
        while (controlFlag.get() && manager.getCurrentTrack() != null) {
            maxVolume = GetMaxVolumeFromTrackId(TrackId);
            EntityPlayer clientPlayer = net.minecraft.client.Minecraft.getMinecraft().player;
            if (clientPlayer != null) {
                double distance = clientPlayer.getPosition().distanceSq(source);
                double distanceLinear = Math.sqrt(distance);
                int volume = 0;
                if (distanceLinear < maxDistance) {
                    volume = (int) (maxVolume - (distanceLinear / maxDistance * maxVolume));
                    if (HasFade == Boolean.FALSE) {
                        volume = maxVolume;
                    }
                }

                int realvolume = (int) (ConfigHandler.VolumeGlobaux * volume);
                player.setVolume(realvolume);

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }
}
