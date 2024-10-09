package fr.velocity.mod.proxy;

import fr.velocity.Main;
import fr.velocity.mod.handler.RegistryHandler;
import fr.velocity.video.block.entity.TVBlockEntity;
import fr.velocity.mod.network.PacketHandler;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.MissingModsException;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.versioning.DefaultArtifactVersion;

public class CommonProxy {
	
	public void preinit(FMLPreInitializationEvent event) {
		Main.LOGGER = event.getModLog();
		RegistryHandler.preInitRegistries(event);
		PacketHandler.registerMessages();
	}
	
	public void init(FMLInitializationEvent event) {
		GameRegistry.registerTileEntity(TVBlockEntity.class, new ResourceLocation(Main.modid, "TVBlockEntity"));
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

	public void openVideo(String url, int volume, boolean controlBlocked, int TimePosition, float VideoSpeed) {}
	public void openVideoGUI(BlockPos pos, String url, int tick, int volume, boolean loop) {}
	public void manageVideo(BlockPos pos, boolean playing, int tick) {}
	public void Playmusic(String url, int volume, String RepeatMode) {}
	public void Stopmusic() {}
	public void Volumemusic(int volume) {}
	public void Pausemusic(String IsPaused) {}
}
