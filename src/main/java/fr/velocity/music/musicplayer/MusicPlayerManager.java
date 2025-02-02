package fr.velocity.music.musicplayer;

import com.sun.org.apache.xpath.internal.operations.Bool;
import fr.velocity.music.lavaplayer.api.queue.ITrackManager;
import org.apache.logging.log4j.*;

import com.google.gson.*;

import fr.velocity.music.dependency.DependencyManager;
import fr.velocity.music.lavaplayer.api.IMusicPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.sound.midi.Track;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class MusicPlayerManager {
	
	private static final Logger logger = LogManager.getLogger();
	
	private static IMusicPlayer Globalplayer;
	private static IMusicPlayer Globalplayer2;

	private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
	private static final Map<String, IMusicPlayer> playerCache = new ConcurrentHashMap<>();

	public static void setup() {
		generatePlayer1();
		Globalplayer.startAudioOutput();
		Globalplayer.setVolume(50);
	}

	public static void ChangeVolume(String TrackId, int Volume) {
		if (Objects.equals(TrackId, "ALL")) {
			Globalplayer.setVolume(Volume);

			for (Map.Entry<String, IMusicPlayer> entry : playerCache.entrySet()) {
				entry.getValue().setVolume(Volume);
			}
		} else {
			TestGenerate(TrackId).setVolume(Volume);
		}
	}

	public static void Pause(String TrackId, Boolean PauseMode) {
		if (Objects.equals(TrackId, "ALL")) {
			final ITrackManager manager = Globalplayer.getTrackManager();
			manager.setPaused(PauseMode);

			for (Map.Entry<String, IMusicPlayer> entry : playerCache.entrySet()) {
				final ITrackManager newmanager = entry.getValue().getTrackManager();
				newmanager.setPaused(PauseMode);
			}
		} else {
			final ITrackManager manager = TestGenerate(TrackId).getTrackManager();
			manager.setPaused(PauseMode);
		}
	}

	public static void StopAudio(String TrackId) {
		if (Objects.equals(TrackId, "ALL")) {
			final ITrackManager manager = Globalplayer.getTrackManager();
			manager.stop();

			for (Map.Entry<String, IMusicPlayer> entry : playerCache.entrySet()) {
				final ITrackManager newmanager = entry.getValue().getTrackManager();
				newmanager.stop();
			}
		} else {
			final ITrackManager manager = TestGenerate(TrackId).getTrackManager();
			manager.stop();
		}
	}

	public static void generatePlayer1() {
		try {
			Class<?> clazz = Class.forName("fr.velocity.music.lavaplayer.MusicPlayer", true, DependencyManager.MUSICPLAYER_CLASSLOADER);
			if (!IMusicPlayer.class.isAssignableFrom(clazz)) {
				throw new IllegalAccessError("The class " + clazz + " does not implement IMusicPlayer! This should not happen?!");
			}
			Globalplayer = (IMusicPlayer) clazz.getConstructor().newInstance();
			logger.info("Successfully created music player instance");
		} catch (Exception ex) {
			logger.fatal("Cannot create music player instance. This is a serious bug and the mod will not work. Report to the mod authors", ex);
			FMLCommonHandler.instance().exitJava(0, false);
		}
	}


	public static IMusicPlayer TestGenerate(String trackId) {
		if (playerCache.containsKey(trackId)) {
			return playerCache.get(trackId);
		} else {
			try {
				Class<?> clazz = Class.forName("fr.velocity.music.lavaplayer.MusicPlayer", true, DependencyManager.MUSICPLAYER_CLASSLOADER);
				if (!IMusicPlayer.class.isAssignableFrom(clazz)) {
					throw new IllegalAccessError("The class " + clazz + " does not implement IMusicPlayer! This should not happen?!");
				}
				IMusicPlayer NewPlayer = (IMusicPlayer) clazz.getConstructor().newInstance();
				NewPlayer.startAudioOutput();
				NewPlayer.setVolume(50);
				playerCache.put(trackId, NewPlayer);
				return NewPlayer;
			} catch (Exception ex) {
				logger.fatal("Cannot create music player instance. This is a serious bug and the mod will not work. Report to the mod authors", ex);
				FMLCommonHandler.instance().exitJava(0, false);
			}
		}
        return null;
    }
	
	public static IMusicPlayer getPlayer() {
		return Globalplayer;
	}
}
