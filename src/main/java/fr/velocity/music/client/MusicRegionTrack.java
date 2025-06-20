package fr.velocity.music.client;

import fr.velocity.mod.handler.ConfigHandler;
import fr.velocity.music.lavaplayer.api.IMusicPlayer;
import fr.velocity.music.lavaplayer.api.audio.IAudioTrack;
import fr.velocity.music.lavaplayer.api.audio.IPlayingTrack;
import fr.velocity.music.lavaplayer.api.queue.ITrackManager;
import fr.velocity.music.musicplayer.MusicPlayerManager;
import fr.velocity.music.musicplayer.playlist.LoadedTracks;
import fr.velocity.music.musicplayer.playlist.Playlist;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
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
public class MusicRegionTrack {

    private static final Map<String, Thread> activeThreads = new ConcurrentHashMap<>();
    private static final Map<String, AtomicBoolean> trackControlFlags = new ConcurrentHashMap<>();

    public static void regionTrackmusic(int x1, int y1, int z1, int x2, int y2, int z2, String regionname, String world, int DimensionId, String url, int volume, String TrackId, String Option) {

        Playlist playlist = new Playlist();

        Thread musicthread = new Thread(() -> {
            IMusicPlayer NewPlayer = MusicPlayerManager.TestGenerate(TrackId, volume, "RegionTrack", x1, y1, z1, 0, Option, "None", regionname, x2, y2, z2, world, DimensionId);

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

                        stopThreadForTrackId(TrackId);
                        AtomicBoolean controlFlag = new AtomicBoolean(true);
                        trackControlFlags.put(TrackId, controlFlag);

                        Thread thread = new Thread(() -> playWithRegion(manager, NewPlayer, regionname, world, DimensionId, x1, y1, z1, x2, y2, z2, volume, Option, controlFlag, TrackId));
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

    public static boolean isInsideRegion(double x, double y, double z,
                                         double x1, double y1, double z1,
                                         double x2, double y2, double z2) {
        double minX = Math.min(x1, x2);
        double maxX = Math.max(x1, x2);
        double minY = Math.min(y1, y2);
        double maxY = Math.max(y1, y2);
        double minZ = Math.min(z1, z2);
        double maxZ = Math.max(z1, z2);

        return (x >= minX && x <= maxX) &&
                (y >= minY && y <= maxY) &&
                (z >= minZ && z <= maxZ);
    }

    private static void playWithRegion(ITrackManager manager, IMusicPlayer player, String regionname, String world, int DimensionId, int x1, int y1, int z1, int x2, int y2, int z2, int maxVolume, String option, AtomicBoolean controlFlag, String TrackId) {
        while (controlFlag.get() && manager.getCurrentTrack() != null) {
            maxVolume = GetMaxVolumeFromTrackId(TrackId);
            EntityPlayer clientPlayer = net.minecraft.client.Minecraft.getMinecraft().player;
            if (clientPlayer != null) {
                int volume = 0;
                if(Minecraft.getMinecraft().player.dimension == DimensionId) {
                    if (isInsideRegion(clientPlayer.getPosition().getX(), clientPlayer.getPosition().getY(), clientPlayer.getPosition().getZ(), x1, y1, z1, x2, y2, z2)) {
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