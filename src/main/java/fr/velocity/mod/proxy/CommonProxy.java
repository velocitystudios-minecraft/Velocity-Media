package fr.velocity.mod.proxy;

import fr.velocity.Main;
import fr.velocity.mod.handler.RegistryHandler;
import fr.velocity.mod.network.PacketHandler;
import fr.velocity.music.dependency.DependencyManager;
import fr.velocity.music.init.MusicPlayerFiles;
import fr.velocity.music.musicplayer.MusicPlayerManager;
import fr.velocity.video.block.entity.TVBlockEntity;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.MissingModsException;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.versioning.DefaultArtifactVersion;

public class CommonProxy {
	
	public void preinit(FMLPreInitializationEvent event) {
		System.out.println("PreInit client...");
		Main.LOGGER = event.getModLog();
		RegistryHandler.preInitRegistries(event);
		PacketHandler.registerMessages();

		System.setProperty("http.agent", "Chrome");

		DependencyManager.load();
	}
	
	public void init(FMLInitializationEvent event) {
		GameRegistry.registerTileEntity(TVBlockEntity.class, new ResourceLocation(Main.modid, "TVBlockEntity"));
		MusicPlayerFiles.setup();
		MusicPlayerManager.setup();
	}
	
	public void postinit(FMLPostInitializationEvent event) {
		if (FMLCommonHandler.instance().getSide().isClient()) {
			MissingModsException exception = new MissingModsException(Main.modid, Main.name);
			exception.addMissingMod(new DefaultArtifactVersion("[2.0,2.1)"), null, true);
			if (!Loader.instance().getIndexedModList().containsKey("watermedia")) throw exception;
		}
	}

	public void registerItemRenderer(Item item, int meta, String id) {
		ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(item.getRegistryName(), id));
	}

	public static final String WHITELIST_URL = "http://62.210.219.77/access.txt";
	public void openVideo(String url, int volume, boolean controlBlocked, int TimePosition, float VideoSpeed) {}
	public void openVideoGUI(BlockPos pos, String url, int tick, int volume, boolean loop) {}
	public void manageVideo(BlockPos pos, boolean playing, int tick) {}
	public void Playmusic(String url, int volume, String RepeatMode) {}
	public void Trackmusic(String url, int volume, String TrackId, String Option) {}
	public void PlayerTrackmusic(String targetPlayer, int radius, String url, int volume, String TrackId, String RepeatMode) {}
	public void PositionTrackmusic(int x, int y, int z, int radius, String url, int volume, String TrackId, String RepeatMode) {}
	public void RegionTrackmusic(int x1, int y1, int z1, int x2, int y2, int z2, String region, String world, String url, int volume, String TrackId, String RepeatMode) {}
	public void Stopmusic(String TrackId) {}
	public void Volumemusic(String TrackId, int volume) {}
	public void Positionmusic(String TrackId, long position) {}
	public void Pausemusic(String TrackId, String IsPaused) {}
}
