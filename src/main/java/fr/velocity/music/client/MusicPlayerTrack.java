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
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static fr.velocity.music.musicplayer.MusicPlayerManager.GetMaxVolumeFromTrackId;

@SideOnly(Side.CLIENT)
public class MusicPlayerTrack {

    private static final Map<String, Thread> activeThreads = new ConcurrentHashMap<>();
    private static final Map<String, AtomicBoolean> trackControlFlags = new ConcurrentHashMap<>();

    public static void PlayerTrackmusic(String targetPlayer, int radius, String url, int volume, String TrackId, String Option) {
        Playlist playlist = new Playlist();

        Thread musicthread = new Thread(() -> {
            IMusicPlayer NewPlayer = MusicPlayerManager.TestGenerate(TrackId, volume, "PlayerTrack", 0, 0, 0, radius, Option, targetPlayer, "None", 0, 0, 0, "None", 0);

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
                        manager.setTrackQueue(playlist);

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

                        stopThreadForTrackId(TrackId);
                        AtomicBoolean controlFlag = new AtomicBoolean(true);
                        trackControlFlags.put(TrackId, controlFlag);

                        Thread thread = new Thread(() -> playAroundEntity(manager, NewPlayer, targetPlayer, volume, radius, Option, controlFlag, TrackId));
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

    public static Entity getEntityByUUID(World world, UUID uuid) {
        for (Entity entity : world.loadedEntityList) {
            if (entity.getUniqueID().equals(uuid)) {
                return entity;
            }
        }
        return null;
    }

    private static void playAroundEntity(ITrackManager manager, IMusicPlayer player, String targetPlayer, int maxVolume, int maxDistance, String Option, AtomicBoolean controlFlag, String IdTrack) {
        Entity NewtargetPlayer = null;
        
        manager.start();

        int StartTime = 0;
        if (Option.contains("--position")) {
            Pattern pattern = Pattern.compile("--position(\\d+)");
            Matcher matcher = pattern.matcher(Option);
            if (matcher.find()) {
                StartTime = Integer.parseInt(matcher.group(1));
                System.out.println("[Velocity Media] --position trouvé : " + StartTime);
                IPlayingTrack currentTrack = manager.getCurrentTrack();
                if(currentTrack!=null) {
                    if(currentTrack.getDuration() < StartTime) {
                        System.out.println("[Velocity Media] Duration indiqué excède la limite de " + currentTrack.getDuration());
                    } else {
                        while (1==1) {
                            long CurrentPosition = manager.getCurrentTrack().getPosition();
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

        Boolean HasFade = Boolean.TRUE;
        if (Option.contains("--nofade")) {
            HasFade = Boolean.FALSE;
        }

        while (controlFlag.get() && manager.getCurrentTrack() != null) {
            maxVolume = GetMaxVolumeFromTrackId(IdTrack);
            EntityPlayer clientPlayer = Minecraft.getMinecraft().player;
            if (Option.contains("--useuuid")) {
                NewtargetPlayer = getEntityByUUID(Minecraft.getMinecraft().world, UUID.fromString(targetPlayer));
            } else {
                NewtargetPlayer = Minecraft.getMinecraft().world.getPlayerEntityByName(targetPlayer);
            }

            if (clientPlayer != null && NewtargetPlayer != null) {
                Vec3d source = NewtargetPlayer.getPositionVector();
                double distance = clientPlayer.getPositionVector().distanceTo(source);
                int volume = 0;

                if (distance < maxDistance) {
                    volume = (int) (maxVolume - (distance / maxDistance * maxVolume));
                    if (HasFade == Boolean.FALSE) {
                        volume = maxVolume;
                    }
                }

                int realvolume = (int) (ConfigHandler.VolumeGlobaux * volume);
                player.setVolume(realvolume);

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                player.setVolume(0);
            }
        }
    }
}
