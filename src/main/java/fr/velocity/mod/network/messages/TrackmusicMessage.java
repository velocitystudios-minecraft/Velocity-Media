package fr.velocity.mod.network.messages;

import fr.velocity.Main;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.nio.charset.StandardCharsets;

public class TrackmusicMessage implements IMessage
{

	private String url;
	private int volume;
	private String RepeatMode;
	private String TrackId;

	public TrackmusicMessage() {}

	public TrackmusicMessage(String url, int volume, String TrackId, String RepeatMode) {
		this.url = url;
		this.volume = volume;
		this.TrackId = TrackId;
		this.RepeatMode = RepeatMode;
	}

	@Override
	public void fromBytes(ByteBuf buffer) {
		this.url = String.valueOf(buffer.readCharSequence(buffer.readInt(), StandardCharsets.UTF_8));
		this.volume = buffer.readInt();
		this.TrackId = String.valueOf(buffer.readCharSequence(buffer.readInt(), StandardCharsets.UTF_8));
		this.RepeatMode = String.valueOf(buffer.readCharSequence(buffer.readInt(), StandardCharsets.UTF_8));
	}

	@Override
	public void toBytes(ByteBuf buffer) {
		buffer.writeInt(url.length());
		buffer.writeCharSequence(url, StandardCharsets.UTF_8);
		buffer.writeInt(volume);
		buffer.writeInt(TrackId.length());
		buffer.writeCharSequence(TrackId, StandardCharsets.UTF_8);
		buffer.writeInt(RepeatMode.length());
		buffer.writeCharSequence(RepeatMode, StandardCharsets.UTF_8);
	}

	public static class Handler implements IMessageHandler<TrackmusicMessage, IMessage> {

		@Override
		public IMessage onMessage(TrackmusicMessage message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
			return null;
		}

		private void handle(TrackmusicMessage message, MessageContext ctx)
		{
			Main.proxy.Trackmusic(message.url, message.volume, message.TrackId, message.RepeatMode);
		}
	}
}