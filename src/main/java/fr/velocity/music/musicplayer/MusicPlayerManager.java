package fr.velocity.music.musicplayer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.velocity.mod.handler.ConfigHandler;
import fr.velocity.music.dependency.DependencyManager;
import fr.velocity.music.lavaplayer.api.IMusicPlayer;
import fr.velocity.music.lavaplayer.api.queue.ITrackManager;
import fr.velocity.music.util.DebugRenderer;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sound.midi.Track;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class MusicPlayerManager {
	
	private static final Logger logger = LogManager.getLogger();

	private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
	public static final Map<String, CustomPlayer> playerCache = new ConcurrentHashMap<>();

	public static void setup() {

	}

	public static void setMaxVolumeFromTrackId(String TrackId, int maxvolume) {
		if (playerCache.containsKey(TrackId)) {
			playerCache.get(TrackId).setMaxVolume(maxvolume);
		}
	}

	public static void changeVolume(String TrackId, int Volume) {
		if (TrackId.equalsIgnoreCase("ALL")) {
			for (Map.Entry<String, CustomPlayer> entry : playerCache.entrySet()) {
				entry.getValue().setMaxVolume(Volume);
				int ModifiedVolume = (int) (Volume * ConfigHandler.VolumeGlobaux);
				entry.getValue().getPlayer().setVolume(ModifiedVolume);
			}
		} else {
			setMaxVolumeFromTrackId(TrackId, Volume);
			int ModifiedVolume = (int) (Volume * ConfigHandler.VolumeGlobaux);
            Objects.requireNonNull(getCustomPlayer(TrackId, 0, "Server", 0, 0, 0, 0, "None", "None", "None", 0, 0, 0, "None", 0)).getPlayer().setVolume(ModifiedVolume);
		}
	}

	public static void changeTimecodeMusic(String TrackId, long Position) {
		if (TrackId.equalsIgnoreCase("ALL")) {
			for (Map.Entry<String, CustomPlayer> entry : playerCache.entrySet()) {
				entry.getValue().getPlayer().getTrackManager().getCurrentTrack().setPosition(Position);
			}
		} else {
			Objects.requireNonNull(getCustomPlayer(TrackId, 0, "Server", 0, 0, 0, 0, "None", "None", "None", 0, 0, 0, "None", 0)).getPlayer().getTrackManager().getCurrentTrack().setPosition(Position);
		}
	}


	public static int getMaxVolumeFromTrackId(String TrackId) {
		if (playerCache.containsKey(TrackId)) {
			return playerCache.get(TrackId).getMaxVolume();
		} else {
			System.out.println("Warning, " + TrackId + " getMaxVolume not found");
			return 0;
		}
	}

	public static void updateVolume() {
		for (Map.Entry<String, CustomPlayer> entry : playerCache.entrySet()) {
			if (entry.getValue().getMode().equalsIgnoreCase("Track")) {
				int NewVolume = (int) (entry.getValue().getMaxVolume() * ConfigHandler.VolumeGlobaux);
				entry.getValue().getPlayer().setVolume(NewVolume);
			}
		}
	}

	public static void pauseMusic(String TrackId, Boolean PauseMode) {
		if (TrackId.equalsIgnoreCase("ALL")) {
			for (Map.Entry<String, CustomPlayer> entry : playerCache.entrySet()) {
				final ITrackManager newmanager = entry.getValue().getPlayer().getTrackManager();
				newmanager.setPaused(PauseMode);
			}
		} else {
			final ITrackManager manager = getCustomPlayer(TrackId, 0, "Server", 0, 0, 0, 0, "None", "None", "None", 0, 0, 0, "None", 0).getPlayer().getTrackManager();
			manager.setPaused(PauseMode);
		}
	}

	public static void stopAudio(String TrackId) {
		if (TrackId.equalsIgnoreCase("ALL")) {
			for (Map.Entry<String, CustomPlayer> entry : playerCache.entrySet()) {
				final ITrackManager newmanager = entry.getValue().getPlayer().getTrackManager();
				newmanager.stop();
			}
		} else {
			final ITrackManager manager = getCustomPlayer(TrackId, 0, "Server", 0, 0, 0, 0, "None", "None", "None", 0, 0, 0, "None", 0).getPlayer().getTrackManager();
			manager.stop();
		}
	}

	public static CustomPlayer getCustomPlayer(String trackId, int volume, String mode, int X, int Y, int Z, int Radius, String Option, String Player, String region, int X2, int Y2, int Z2, String world, int DimensionId) {
		if (playerCache.containsKey(trackId)) {
			if(Objects.equals(mode, "PositionTrack") || Objects.equals(mode, "PlayerTrack")) {
				System.out.println("Debug d'un " + mode + "...");
				DebugRenderer.INSTANCE.addZone(
						new BlockPos(X, Y, Z),
						Radius,
						trackId,
						mode,
						Option,
						Player
				);
			} else {
				if(Objects.equals(mode, "RegionTrack")) {
					System.out.println("Debug d'une region...");
					DebugRenderer.INSTANCE.addRegionZone(
							X,
							Y,
							Z,
							X2,
							Y2,
							Z2,
							region,
							world,
							trackId,
							mode,
							Option,
							Player,
							DimensionId
					);
				}
			}

			CustomPlayer GetInPlayerCache = playerCache.get(trackId);
			GetInPlayerCache.setMaxVolume(volume);
			GetInPlayerCache.setRadius(Radius);
			return GetInPlayerCache;
		} else {
			try {
				Class<?> clazz = Class.forName("fr.velocity.music.lavaplayer.MusicPlayer", true, DependencyManager.MUSICPLAYER_CLASSLOADER);
				if (!IMusicPlayer.class.isAssignableFrom(clazz)) {
					throw new IllegalAccessError("La classe " + clazz + " n'implémente pas IMusicPlayer ! Cela ne devrait pas arriver ?");
				}
				IMusicPlayer newPlayer = (IMusicPlayer) clazz.getConstructor().newInstance();
				newPlayer.startAudioOutput();
				newPlayer.setVolume(volume);
				CustomPlayer playerWithVolume = new CustomPlayer(newPlayer, trackId, volume, mode, X, Y, Z, Radius, Option, Player);
				playerCache.put(trackId, playerWithVolume);

				if(Objects.equals(mode, "PositionTrack") || Objects.equals(mode, "PlayerTrack")) {
					System.out.println("Debug d'un " + mode + "...");
					DebugRenderer.INSTANCE.addZone(
							new BlockPos(X, Y, Z),
							Radius,
							trackId,
							mode,
							Option,
							Player
					);
				} else {
					if(Objects.equals(mode, "RegionTrack")) {
						System.out.println("Debug d'une region...");
						DebugRenderer.INSTANCE.addRegionZone(
								X,
								Y,
								Z,
								X2,
								Y2,
								Z2,
								region,
								world,
								trackId,
								mode,
								Option,
								Player,
								DimensionId
						);
					}
				}

				return playerWithVolume;
			} catch (Exception ex) {
				logger.fatal("Impossible de créer une instance du lecteur de musique. C'est un bug sérieux et le mod ne fonctionnera pas. Signalez-le aux auteurs du mod", ex);
			}
		}
		return null;
	}


}
