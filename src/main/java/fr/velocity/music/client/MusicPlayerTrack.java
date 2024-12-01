package fr.velocity.music.client;

import com.sun.media.jfxmedia.track.AudioTrack;
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

import javax.sound.midi.Track;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SideOnly(Side.CLIENT)
public class MusicPlayerTrack {

    private static final Map<String, Thread> activeThreads = new ConcurrentHashMap<>();
    private static final Map<String, AtomicBoolean> trackControlFlags = new ConcurrentHashMap<>();

    public static void PlayerTrackmusic(String targetPlayer, int radius, String url, int volume, String TrackId, String Option) {
        Playlist playlist = new Playlist();
        IMusicPlayer NewPlayer = MusicPlayerManager.TestGenerate(TrackId);

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
                    manager.start();

                    int StartTime = 0;
                    if (Option.contains("--position")) {
                        Pattern pattern = Pattern.compile("--position(\\d+)");
                        Matcher matcher = pattern.matcher(Option);
                        if (matcher.find()) {
                            StartTime = Integer.parseInt(matcher.group(1));
                            IPlayingTrack currentTrack = NewPlayer.getTrackManager().getCurrentTrack();
                            currentTrack.setPosition(StartTime);
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
        Entity NewtargetPlayer = Minecraft.getMinecraft().world.getPlayerEntityByName(targetPlayer);
        if (Option.contains("--useuuid")) {
            NewtargetPlayer = getEntityByUUID(Minecraft.getMinecraft().world, UUID.fromString(targetPlayer));
        }



        Boolean HasFade = Boolean.TRUE;
        if (Option.contains("--nofade")) {
            HasFade = Boolean.FALSE;
        }
        while (controlFlag.get() && manager.getCurrentTrack() != null) {
            EntityPlayer clientPlayer = net.minecraft.client.Minecraft.getMinecraft().player;
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

                player.setVolume(volume);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                stopThreadForTrackId(IdTrack);
            }
        }
    }
}
