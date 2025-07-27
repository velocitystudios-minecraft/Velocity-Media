package fr.velocity.mod.network.messages;

import fr.velocity.Main;
import fr.velocity.music.musicplayer.CustomPlayer;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.Map;

import static fr.velocity.music.musicplayer.MusicPlayerManager.playerCache;

public class S2CMessageInfoMusic implements IMessage
{

	public S2CMessageInfoMusic() {}


	@Override
	public void fromBytes(ByteBuf buffer) {}

	@Override
	public void toBytes(ByteBuf buffer) {}

	public static class Handler implements IMessageHandler<S2CMessageInfoMusic, IMessage> {

		@Override
		public IMessage onMessage(S2CMessageInfoMusic message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
			return null;
		}

		private void handle(S2CMessageInfoMusic message, MessageContext ctx)
		{
			boolean Found = false;
			for (Map.Entry<String, CustomPlayer> entry : playerCache.entrySet()) {
				if(entry.getValue().getPlayer().getVolume() > 0) {
					Found = true;

					TextComponentString GetText = new TextComponentString("§7- §a" + entry.getKey());
					Style style = new Style();
					String hoverText = "§7- CV : §a" + entry.getValue().getPlayer().getVolume();
					hoverText = hoverText + " §7- MV : §a" + entry.getValue().getMaxVolume();
					hoverText = hoverText + " §7- Mode : §a" + entry.getValue().getMode();
					hoverText = hoverText + " §7- Radius : §a" + entry.getValue().getRadius();
					hoverText = hoverText + " §7- Name : §a" + entry.getValue().getPlayer().getTrackManager().getCurrentTrack().getInfo().getFixedTitle();
					style.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(hoverText)));
					GetText.setStyle(style);
					Minecraft.getMinecraft().player.sendMessage(GetText);
				}
			}
			if(!Found) {
				Minecraft.getMinecraft().player.sendMessage(new TextComponentString("§cAucun son actuellement joué ou votre volume est a 0."));
			}
		}
	}
}