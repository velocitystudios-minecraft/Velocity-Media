package fr.velocity.music.musicplayer;

import fr.velocity.mod.handler.ConfigHandler;
import fr.velocity.music.lavaplayer.api.queue.ITrackManager;
import fr.velocity.music.util.DebugRenderer;
import jdk.nashorn.internal.runtime.regexp.joni.Config;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.*;

import com.google.gson.*;

import fr.velocity.music.dependency.DependencyManager;
import fr.velocity.music.lavaplayer.api.IMusicPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.lwjgl.Sys;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class MusicPlayerManager {
	
	private static final Logger logger = LogManager.getLogger();

	private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
	public static final Map<String, CustomPlayer> playerCache = new ConcurrentHashMap<>();

	public static void setup() {

	}

	public static void SetMaxVolumeFromTrackId(String TrackId, int maxvolume) {
		if (playerCache.containsKey(TrackId)) {
			playerCache.get(TrackId).setMaxVolume(maxvolume);
		}
	}

	public static void ChangeVolume(String TrackId, int Volume) {
		if (Objects.equals(TrackId, "ALL")) {
			for (Map.Entry<String, CustomPlayer> entry : playerCache.entrySet()) {
				entry.getValue().setMaxVolume(Volume);
				int ModifiedVolume = (int) (Volume * ConfigHandler.VolumeGlobaux);
				entry.getValue().getPlayer().setVolume(ModifiedVolume);
			}
		} else {
			SetMaxVolumeFromTrackId(TrackId, Volume);
			int ModifiedVolume = (int) (Volume * ConfigHandler.VolumeGlobaux);
            Objects.requireNonNull(TestGenerate(TrackId, 0, "Server", 0, 0, 0, 0, "None", "None", "None", 0, 0, 0, "None")).setVolume(ModifiedVolume);
		}
	}

	public static void ChangePosition(String TrackId, long Position) {
		if (Objects.equals(TrackId, "ALL")) {
			for (Map.Entry<String, CustomPlayer> entry : playerCache.entrySet()) {
				entry.getValue().getPlayer().getTrackManager().getCurrentTrack().setPosition(Position);
			}
		} else {
			Objects.requireNonNull(TestGenerate(TrackId, 0, "Server", 0, 0, 0, 0, "None", "None", "None", 0, 0, 0, "None")).getTrackManager().getCurrentTrack().setPosition(Position);
		}
	}


	public static int GetMaxVolumeFromTrackId(String TrackId) {
		if (playerCache.containsKey(TrackId)) {
			return playerCache.get(TrackId).getMaxVolume();
		} else {
			return 0;
		}
	}

	public static void UpdateVolume() {
		for (Map.Entry<String, CustomPlayer> entry : playerCache.entrySet()) {
			int NewVolume = (int) (entry.getValue().getMaxVolume() * ConfigHandler.VolumeGlobaux);
			entry.getValue().getPlayer().setVolume(NewVolume);
		}
	}

	public static void Pause(String TrackId, Boolean PauseMode) {
		if (Objects.equals(TrackId, "ALL")) {
			for (Map.Entry<String, CustomPlayer> entry : playerCache.entrySet()) {
				final ITrackManager newmanager = entry.getValue().getPlayer().getTrackManager();
				newmanager.setPaused(PauseMode);
			}
		} else {
			final ITrackManager manager = TestGenerate(TrackId, 0, "Server", 0, 0, 0, 0, "None", "None", "None", 0, 0, 0, "None").getTrackManager();
			manager.setPaused(PauseMode);
		}
	}

	public static void StopAudio(String TrackId) {
		if (Objects.equals(TrackId, "ALL")) {
			for (Map.Entry<String, CustomPlayer> entry : playerCache.entrySet()) {
				final ITrackManager newmanager = entry.getValue().getPlayer().getTrackManager();
				newmanager.stop();
			}
		} else {
			final ITrackManager manager = TestGenerate(TrackId, 0, "Server", 0, 0, 0, 0, "None", "None", "None", 0, 0, 0, "None").getTrackManager();
			manager.stop();
		}
	}



	public static IMusicPlayer TestGenerate(String trackId, int volume, String mode, int X, int Y, int Z, int Radius, String Option, String Player, String region, int X2, int Y2, int Z2, String world) {
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
							Player
					);
				}
			}


			return playerCache.get(trackId).getPlayer();
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
								Player
						);
					}
				}

				return newPlayer;
			} catch (Exception ex) {
				logger.fatal("Impossible de créer une instance du lecteur de musique. C'est un bug sérieux et le mod ne fonctionnera pas. Signalez-le aux auteurs du mod", ex);
			}
		}
		return null;
	}


}
