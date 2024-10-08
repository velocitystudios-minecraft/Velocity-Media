package fr.velocity.mod.proxy;

import fr.velocity.music.dependency.DependencyManager;
import fr.velocity.music.init.MusicPlayerFiles;
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
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.relauncher.*;

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
		System.setProperty("http.agent", "Chrome");
		
		DependencyManager.load();
	}
	
	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);
		MusicPlayerFiles.setup();
		MusicPlayerManager.setup();
	}
	
	@Override
	public void postinit(FMLPostInitializationEvent event) {
		super.postinit(event);
	}
}
