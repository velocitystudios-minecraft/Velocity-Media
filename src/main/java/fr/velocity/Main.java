package fr.velocity;

import fr.velocity.mod.handler.RegistryHandler;
import fr.velocity.mod.item.ModItems;
import fr.velocity.music.client.ClientManager;
import fr.velocity.music.command.*;
import fr.velocity.music.util.DebugRenderer;
import fr.velocity.util.ServerListPersistence;
import fr.velocity.video.block.entity.TVBlockEntity;
import fr.velocity.video.command.PlayVideoCommand;
import fr.velocity.mod.proxy.CommonProxy;
import fr.velocity.video.block.ModBlocks;
import me.srrapero720.watermedia.api.image.ImageRenderer;
import net.minecraft.command.CommandException;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Logger;

import javax.swing.*;

import static fr.velocity.util.ServerListPersistence.AskForSavedData;

@Mod(modid = Main.modid, name = Main.name, version = Main.version, acceptedMinecraftVersions = Main.mcversion, dependencies = Main.dependencies/*, updateJSON = Main.updateurl, clientSideOnly = true*/)
public class Main {

	public static final String modid = "velocitymedia";
	public static final String name = "Velocity Media";
	public static final String version = "@VERSION@";
	public static final String mcversion = "@MCVERSION@";
	public static final String dependencies = "required:forge@[14.23.5.2768,);after:watermedia";
	public static Logger LOGGER = null;
	public static Boolean DebugMode = false;

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
	public static ImageRenderer IMG_SKIP;
	public static ImageRenderer LOADINGGIF;

	@SideOnly(Side.CLIENT)
	public static ImageRenderer pausedImage() { return IMG_PAUSED; }
	public static ImageRenderer SkipImage() { return IMG_SKIP; }
	public static ImageRenderer loadingGif() { return LOADINGGIF; }


	private ClientManager clientManager;

	public static class ModEventSubscriber {
		@SubscribeEvent()
		public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) throws CommandException {
			System.out.println("Le joueur " + event.player.getName() + " a rejoint le jeu.");
			AskForSavedData(event.player);
		}
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		RegistryHandler.preInitRegistries(event);
		proxy.preinit(event);

		MinecraftForge.EVENT_BUS.register(new ModEventSubscriber());
		MinecraftForge.EVENT_BUS.register(ModItems.class);
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		GameRegistry.registerTileEntity(TVBlockEntity.class, new ResourceLocation(modid, "TVBlockEntity"));

		if(event.getSide().isClient()) {
			clientManager = new ClientManager();
			MinecraftForge.EVENT_BUS.register(DebugRenderer.INSTANCE);
		}

		proxy.init(event);
	}

	private void showWindowsNotification(String title, String message) {
		JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		if (FMLCommonHandler.instance().getSide().isClient()) {
			if (!me.srrapero720.watermedia.api.WaterMediaAPI.vlc_isReady()) {
				System.out.println("VLC n'es pas detecté");
				showWindowsNotification("Erreur fatale", "Le lecteur VLC n'est pas installé. Veuillez l'installer.");
				System.exit(1);
			} else {
				System.out.println("VLC est actif");
			}
		}
		proxy.postinit(event);
	}

	@Mod.EventHandler
	public void serverInit(FMLServerStartingEvent event) {
		event.registerServerCommand(new StopCommand());
		event.registerServerCommand(new VolumeCommand());
		event.registerServerCommand(new PauseCommand());
		event.registerServerCommand(new PositionCommand());
		event.registerServerCommand(new PlayVideoCommand());
		event.registerServerCommand(new TrackCommand());
		event.registerServerCommand(new PositionTrackCommand());
		event.registerServerCommand(new PlayerTrackCommand());

		ServerListPersistence.loadData();
	}

	@Mod.EventHandler
	public void serverStopped(FMLServerStoppedEvent event) {
		ServerListPersistence.saveData();
	}
}