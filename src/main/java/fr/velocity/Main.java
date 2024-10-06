package fr.velocity;

import fr.velocity.mod.handler.RegistryHandler;
import fr.velocity.music.command.PauseCommand;
import fr.velocity.music.command.PlayCommand;
import fr.velocity.music.command.StopCommand;
import fr.velocity.music.command.VolumeCommand;
import fr.velocity.music.command.local.LocalPauseCommand;
import fr.velocity.music.command.local.LocalPlayCommand;
import fr.velocity.music.command.local.LocalStopCommand;
import fr.velocity.music.command.local.LocalVolumeCommand;
import fr.velocity.video.block.entity.TVBlockEntity;
import fr.velocity.video.command.PlayVideoCommand;
import fr.velocity.mod.proxy.CommonProxy;
import fr.velocity.video.block.ModBlocks;
import me.srrapero720.watermedia.api.image.ImageRenderer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.versioning.DefaultArtifactVersion;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Logger;

@Mod(modid = Main.modid, name = Main.name, version = Main.version, acceptedMinecraftVersions = Main.mcversion, dependencies = Main.dependencies, /*updateJSON = Main.updateurl,*/ clientSideOnly = true)
public class Main {
	
	public static final String modid = "velocity-media";
	public static final String name = "Velocity Média";
	public static final String version = "@VERSION@";
	public static final String mcversion = "@MCVERSION@";
	public static final String dependencies = "required:forge@[14.23.5.2768,);required-after:uteamcore@[2.2.5.305,);after:watermedia";
	public static final String updateurl = "https://api.u-team.info/update/musicplayer.json";
	public static Logger LOGGER = null;

	@SidedProxy(serverSide = "fr.velocity.mod.proxy.CommonProxy", clientSide = "fr.velocity.mod.proxy.ClientProxy")
	public static CommonProxy proxy;

	@Mod.Instance
	public static Main instance;

	public static CreativeTabs videoplayerTab = new CreativeTabs("videoplayer_tab") {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(Item.getItemFromBlock(ModBlocks.TV_BLOCK));
		}
	};

	@SideOnly(Side.CLIENT)
	public static ImageRenderer IMG_PAUSED;

	@SideOnly(Side.CLIENT)
	public static ImageRenderer pausedImage() { return IMG_PAUSED; }

	@EventHandler
	public void preinit(FMLPreInitializationEvent event) {
		RegistryHandler.preInitRegistries(event);
		proxy.preinit(event);
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		GameRegistry.registerTileEntity(TVBlockEntity.class, new ResourceLocation(modid, "TVBlockEntity"));
		proxy.init(event);
	}
	
	@EventHandler
	public void postinit(FMLPostInitializationEvent event) {
		if (FMLCommonHandler.instance().getSide().isClient()) {
			MissingModsException exception = new MissingModsException(modid, name);
			exception.addMissingMod(new DefaultArtifactVersion("[2.0,2.1)"), null, true);
			if (!Loader.instance().getIndexedModList().containsKey("watermedia")) throw exception;
		}
		proxy.postinit(event);
	}

	@Mod.EventHandler
	public void onServerStarting(FMLServerStartingEvent event) {
		event.registerServerCommand(new LocalPlayCommand());
		event.registerServerCommand(new LocalStopCommand());
		event.registerServerCommand(new LocalPauseCommand());
		event.registerServerCommand(new LocalVolumeCommand());
		event.registerServerCommand(new PlayCommand());
		event.registerServerCommand(new StopCommand());
		event.registerServerCommand(new VolumeCommand());
		event.registerServerCommand(new PauseCommand());
		event.registerServerCommand(new PlayVideoCommand());
	}
}