package fr.velocity.mod.network.messages;

import fr.velocity.Main;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.nio.charset.StandardCharsets;

public class S2CMessagePositionTrackMusic implements IMessage
{

	private int x;
	private int y;
	private int z;
	private int radius;
	private String url;
	private int volume;
	private String trackId;
	private String options;

	public S2CMessagePositionTrackMusic() {}

	public S2CMessagePositionTrackMusic(int x, int y, int z, int radius, String url, int volume, String trackId, String options) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.radius = radius;
		this.url = url;
		this.volume = volume;
		this.trackId = trackId;
		this.options = options;
	}

	@Override
	public void fromBytes(ByteBuf buffer) {
		this.x = buffer.readInt();
		this.y = buffer.readInt();
		this.z = buffer.readInt();
		this.radius = buffer.readInt();
		this.url = ByteBufUtils.readUTF8String(buffer);
		this.volume = buffer.readInt();
		this.trackId = ByteBufUtils.readUTF8String(buffer);
		this.options = ByteBufUtils.readUTF8String(buffer);
	}

	@Override
	public void toBytes(ByteBuf buffer) {
		buffer.writeInt(x);
		buffer.writeInt(y);
		buffer.writeInt(z);
		buffer.writeInt(radius);
		ByteBufUtils.writeUTF8String(buffer, this.url);
		buffer.writeInt(volume);
		ByteBufUtils.writeUTF8String(buffer, this.trackId);
		ByteBufUtils.writeUTF8String(buffer, this.options);
	}

	public static class Handler implements IMessageHandler<S2CMessagePositionTrackMusic, IMessage> {

		@Override
		public IMessage onMessage(S2CMessagePositionTrackMusic message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
			return null;
		}

		private void handle(S2CMessagePositionTrackMusic message, MessageContext ctx)
		{
			Main.proxy.positionTrackMusic(message.x, message.y, message.z, message.radius, message.url, message.volume, message.trackId, message.options);
		}
	}
}