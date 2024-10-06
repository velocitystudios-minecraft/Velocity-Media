package info.u_team.music_player;

import info.u_team.music_player.command.PauseCommand;
import info.u_team.music_player.command.PlayCommand;
import info.u_team.music_player.command.StopCommand;
import info.u_team.music_player.command.VolumeCommand;
import info.u_team.music_player.command.local.LocalPauseCommand;
import info.u_team.music_player.command.local.LocalPlayCommand;
import info.u_team.music_player.command.local.LocalStopCommand;
import info.u_team.music_player.command.local.LocalVolumeCommand;
import info.u_team.music_player.proxy.CommonProxy;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.*;

@Mod(modid = MusicPlayerMod.modid, name = MusicPlayerMod.name, version = MusicPlayerMod.version, acceptedMinecraftVersions = MusicPlayerMod.mcversion, dependencies = MusicPlayerMod.dependencies, updateJSON = MusicPlayerMod.updateurl, clientSideOnly = true)
public class MusicPlayerMod {
	
	public static final String modid = "velocity-media";
	public static final String name = "Velocity Média";
	public static final String version = "@VERSION@";
	public static final String mcversion = "@MCVERSION@";
	public static final String dependencies = "required:forge@[14.23.5.2768,);required-after:uteamcore@[2.2.5.305,);";
	public static final String updateurl = "https://api.u-team.info/update/musicplayer.json";
	
	@SidedProxy(serverSide = "info.u_team.music_player.proxy.CommonProxy", clientSide = "info.u_team.music_player.proxy.ClientProxy")
	private static CommonProxy proxy;
	
	@EventHandler
	public void preinit(FMLPreInitializationEvent event) {
		proxy.preinit(event);
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init(event);
	}
	
	@EventHandler
	public void postinit(FMLPostInitializationEvent event) {
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
	}
}