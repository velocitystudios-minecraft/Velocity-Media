package fr.velocity.music.musicplayer;

import net.minecraft.entity.player.EntityPlayer;
import org.apache.logging.log4j.*;

import com.google.gson.*;

import fr.velocity.music.dependency.DependencyManager;
import fr.velocity.music.lavaplayer.api.IMusicPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class MusicPlayerManager {
	
	private static final Logger logger = LogManager.getLogger();
	
	private static IMusicPlayer Globalplayer;

	private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
	
	private static final PlaylistManager playlistManager = new PlaylistManager(gson);
	
	public static void setup() {
		generatePlayer();
		Globalplayer.startAudioOutput();
		playlistManager.loadFromFile();

		Globalplayer.setVolume(50);
	}
	
	private static void generatePlayer() {
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
	
	public static IMusicPlayer getPlayer() {
		return Globalplayer;
	}


	
	public static PlaylistManager getPlaylistManager() {
		return playlistManager;
	}
}
