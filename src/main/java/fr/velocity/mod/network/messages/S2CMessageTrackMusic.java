package fr.velocity.mod.network.messages;

import fr.velocity.Main;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.nio.charset.StandardCharsets;

public class S2CMessageTrackMusic implements IMessage
{

	private String url;
	private int volume;
	private String Option;
	private String TrackId;

	public S2CMessageTrackMusic() {}

	public S2CMessageTrackMusic(String url, int volume, String TrackId, String Option) {
		this.url = url;
		this.volume = volume;
		this.TrackId = TrackId;
		this.Option = Option;
	}

	@Override
	public void fromBytes(ByteBuf buffer) {
		this.url = String.valueOf(buffer.readCharSequence(buffer.readInt(), StandardCharsets.UTF_8));
		this.volume = buffer.readInt();
		this.TrackId = String.valueOf(buffer.readCharSequence(buffer.readInt(), StandardCharsets.UTF_8));
		this.Option = String.valueOf(buffer.readCharSequence(buffer.readInt(), StandardCharsets.UTF_8));
	}

	@Override
	public void toBytes(ByteBuf buffer) {
		buffer.writeInt(url.length());
		buffer.writeCharSequence(url, StandardCharsets.UTF_8);
		buffer.writeInt(volume);
		buffer.writeInt(TrackId.length());
		buffer.writeCharSequence(TrackId, StandardCharsets.UTF_8);
		buffer.writeInt(Option.length());
		buffer.writeCharSequence(Option, StandardCharsets.UTF_8);
	}

	public static class Handler implements IMessageHandler<S2CMessageTrackMusic, IMessage> {

		@Override
		public IMessage onMessage(S2CMessageTrackMusic message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
			return null;
		}

		private void handle(S2CMessageTrackMusic message, MessageContext ctx)
		{
			Main.proxy.trackMusic(message.url, message.volume, message.TrackId, message.Option);
		}
	}
}