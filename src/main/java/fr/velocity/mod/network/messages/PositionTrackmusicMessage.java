package fr.velocity.mod.network.messages;

import fr.velocity.Main;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.nio.charset.StandardCharsets;

public class PositionTrackmusicMessage implements IMessage
{

	private int x;
	private int y;
	private int z;
	private int radius;
	private String url;
	private int volume;
	private String Option;
	private String TrackId;

	public PositionTrackmusicMessage() {}

	public PositionTrackmusicMessage(int x, int y, int z, int radius, String url, int volume, String TrackId, String Option) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.radius = radius;
		this.url = url;
		this.volume = volume;
		this.TrackId = TrackId;
		this.Option = Option;
	}

	@Override
	public void fromBytes(ByteBuf buffer) {
		this.x = buffer.readInt();
		this.y = buffer.readInt();
		this.z = buffer.readInt();
		this.radius = buffer.readInt();
		this.url = String.valueOf(buffer.readCharSequence(buffer.readInt(), StandardCharsets.UTF_8));
		this.volume = buffer.readInt();
		this.TrackId = String.valueOf(buffer.readCharSequence(buffer.readInt(), StandardCharsets.UTF_8));
		this.Option = String.valueOf(buffer.readCharSequence(buffer.readInt(), StandardCharsets.UTF_8));
	}

	@Override
	public void toBytes(ByteBuf buffer) {
		buffer.writeInt(x);
		buffer.writeInt(y);
		buffer.writeInt(z);
		buffer.writeInt(radius);
		buffer.writeInt(url.length());
		buffer.writeCharSequence(url, StandardCharsets.UTF_8);
		buffer.writeInt(volume);
		buffer.writeInt(TrackId.length());
		buffer.writeCharSequence(TrackId, StandardCharsets.UTF_8);
		buffer.writeInt(Option.length());
		buffer.writeCharSequence(Option, StandardCharsets.UTF_8);
	}

	public static class Handler implements IMessageHandler<PositionTrackmusicMessage, IMessage> {

		@Override
		public IMessage onMessage(PositionTrackmusicMessage message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
			return null;
		}

		private void handle(PositionTrackmusicMessage message, MessageContext ctx)
		{
			Main.proxy.PositionTrackmusic(message.x, message.y, message.z, message.radius, message.url, message.volume, message.TrackId, message.Option);
		}
	}
}