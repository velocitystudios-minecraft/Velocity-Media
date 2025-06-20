package fr.velocity.music.init;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MusicPlayerFiles {
	
	private static final Logger logger = LogManager.getLogger();
	
	private static Path directory;
	
	public static void setup() {
		if (FMLCommonHandler.instance().getSide().isClient()) {
			directory = Paths.get(Minecraft.getMinecraft().gameDir.toString(), "config/musicplayer");
			try {
				Files.createDirectories(directory);
			} catch (Exception ex) {
				logger.error("Could not create music player directories", ex);
			}
		}
	}
	
	public static Path getDirectory() {
		if(directory==null) {
			setup();
		}
		return directory;
	}
}
