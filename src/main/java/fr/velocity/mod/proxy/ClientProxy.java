package fr.velocity.mod.proxy;

import fr.velocity.mod.handler.ConfigHandler;
import fr.velocity.mod.handler.GuiEventHandler;
import fr.velocity.music.client.*;
import fr.velocity.music.musicplayer.MusicPlayerManager;
import fr.velocity.video.block.entity.TVBlockEntity;
import fr.velocity.video.client.gui.TVVideoScreen;
import fr.velocity.video.client.gui.VideoScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

	@Override
	public void registerItemRenderer(Item item, int meta, String id) {
		ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(item.getRegistryName(), id));
	}

	@Override
	public void openVideo(String url, int volume, boolean controlBlocked, int TimePosition, float VideoSpeed) {
		Minecraft.getMinecraft().displayGuiScreen(new VideoScreen(url, volume, controlBlocked, TimePosition, VideoSpeed));
	}

	@Override
	public void trackMusic(String url, int volume, String TrackId, String RepeatMode) {
		MusicTrack.trackMusic(url, volume, TrackId, RepeatMode);
	}

	@Override
	public void playerTrackMusic(String targetPlayer, int radius, String url, int volume, String TrackId, String RepeatMode) {
		PlayerTrackManager.playerTrackMusic(targetPlayer, radius, url, volume, TrackId, RepeatMode);
	}

	@Override
	public void positionTrackMusic(int x, int y, int z, int radius, String url, int volume, String TrackId, String RepeatMode) {
		PositionTrackManager.positionTrackMusic(x, y, z, radius, url, volume, TrackId, RepeatMode);
	}

	@Override
	public void regionTrackMusic(int x1, int y1, int z1, int x2, int y2, int z2, String region, String world, int DimensionId, String url, int volume, String TrackId, String RepeatMode) {
		RegionTrackManager.regionTrackMusic(x1, y1, z1, x2, y2, z2, region, world, DimensionId, url, volume, TrackId, RepeatMode);
	}

	@Override
	public void volumeMusic(String trackId, int volume) {
		MusicPlayerManager.changeVolume(trackId, volume);
	}

	@Override
	public void changeTimecodeMusic(String trackId, long position) {
		MusicPlayerManager.changeTimecodeMusic(trackId, position);
	}

	@Override
	public void pauseMusic(String trackId, boolean isPaused) {
        MusicPlayerManager.pauseMusic(trackId, isPaused);
	}

	@Override
	public void stopMusic(String trackId) {
		MusicPlayerManager.stopAudio(trackId);
	}

	@Override
	public void openVideoGUI(BlockPos pos, String url, int tick, int volume, boolean loop) {
		TileEntity be = Minecraft.getMinecraft().world.getTileEntity(pos);
		if (be instanceof TVBlockEntity) {
			TVBlockEntity tv = (TVBlockEntity) be;
			tv.setUrl(url);
			tv.setTick(tick);
			tv.setVolume(volume);
			tv.setLoop(loop);
			Minecraft.getMinecraft().displayGuiScreen(new TVVideoScreen(be, url, volume));
		}
	}

	@Override
	public void manageVideo(BlockPos pos, boolean playing, int tick) {
		TileEntity be = Minecraft.getMinecraft().world.getTileEntity(pos);
		if (be instanceof TVBlockEntity) {
			TVBlockEntity tv = (TVBlockEntity) be;
			tv.setPlaying(playing);
			tv.setTick(tick);
			if (tv.requestDisplay() != null) {
				if (playing)
					tv.requestDisplay().resume(tv.getUrl(), tv.getVolume(), tv.minDistance, tv.maxDistance, tv.isPlaying(), tv.isLoop(), tv.getTick());
				else
					tv.requestDisplay().pause(tv.getUrl(), tv.getVolume(), tv.minDistance, tv.maxDistance, tv.isPlaying(), tv.isLoop(), tv.getTick());
			}
		}
	}

	@Override
	public void preinit(FMLPreInitializationEvent event) {
		super.preinit(event);
	}
	
	@Override
	public void init(FMLInitializationEvent event) {
		ConfigHandler.init();
		MinecraftForge.EVENT_BUS.register(new GuiEventHandler());
		super.init(event);
	}
	
	@Override
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
	}
}
