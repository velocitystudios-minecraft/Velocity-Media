package fr.velocity.mod.network.messages;

import fr.velocity.Main;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.nio.charset.StandardCharsets;

public class S2CMessageTrackMusic implements IMessage
{

	private String url;
	private int volume;
	private String trackId;
	private String options;

	public S2CMessageTrackMusic() {}

	public S2CMessageTrackMusic(String url, int volume, String trackId, String options) {
		this.url = url;
		this.volume = volume;
		this.trackId = trackId;
		this.options = options;
	}

	@Override
	public void fromBytes(ByteBuf buffer) {
		this.url = ByteBufUtils.readUTF8String(buffer);
		this.volume = buffer.readInt();
		this.trackId = ByteBufUtils.readUTF8String(buffer);
		this.options = ByteBufUtils.readUTF8String(buffer);
	}

	@Override
	public void toBytes(ByteBuf buffer) {
		ByteBufUtils.writeUTF8String(buffer, this.url);
		buffer.writeInt(volume);
		ByteBufUtils.writeUTF8String(buffer, this.trackId);
		ByteBufUtils.writeUTF8String(buffer, this.options);
	}

	public static class Handler implements IMessageHandler<S2CMessageTrackMusic, IMessage> {

		@Override
		public IMessage onMessage(S2CMessageTrackMusic message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
			return null;
		}

		private void handle(S2CMessageTrackMusic message, MessageContext ctx)
		{
			Main.proxy.trackMusic(message.url, message.volume, message.trackId, message.options);
		}
	}
}