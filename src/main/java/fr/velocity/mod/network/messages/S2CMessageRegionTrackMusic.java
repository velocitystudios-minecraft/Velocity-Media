package fr.velocity.mod.network.messages;

import fr.velocity.Main;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.nio.charset.StandardCharsets;

public class S2CMessageRegionTrackMusic implements IMessage
{

	private int x1;
	private int y1;
	private int z1;
	private int x2;
	private int y2;
	private int z2;
	private String region;
	private String world;
	private String url;
	private int volume;
	private String trackId;
	private String options;
	private int dimensionId;

	public S2CMessageRegionTrackMusic() {}

	public S2CMessageRegionTrackMusic(int x1, int y1, int z1, int x2, int y2, int z2, String region, String world, int DimensionId, String url, int volume, String trackId, String options) {
		this.x1 = x1;
		this.y1 = y1;
		this.z1 = z1;
		this.x2 = x2;
		this.y2 = y2;
		this.z2 = z2;
		this.region = region;
		this.world = world;
		this.url = url;
		this.volume = volume;
		this.trackId = trackId;
		this.options = options;
		this.dimensionId = DimensionId;
	}

	@Override
	public void fromBytes(ByteBuf buffer) {
		this.x1 = buffer.readInt();
		this.y1 = buffer.readInt();
		this.z1 = buffer.readInt();
		this.x2 = buffer.readInt();
		this.y2 = buffer.readInt();
		this.z2 = buffer.readInt();
		this.region = ByteBufUtils.readUTF8String(buffer);
		this.world = ByteBufUtils.readUTF8String(buffer);
		this.url = ByteBufUtils.readUTF8String(buffer);
		this.volume = buffer.readInt();
		this.trackId = ByteBufUtils.readUTF8String(buffer);
		this.options = ByteBufUtils.readUTF8String(buffer);
		this.dimensionId = buffer.readInt();
	}

	@Override
	public void toBytes(ByteBuf buffer) {
		buffer.writeInt(x1);
		buffer.writeInt(y1);
		buffer.writeInt(z1);
		buffer.writeInt(x2);
		buffer.writeInt(y2);
		buffer.writeInt(z2);
		ByteBufUtils.writeUTF8String(buffer, this.region);
		ByteBufUtils.writeUTF8String(buffer, this.world);
		ByteBufUtils.writeUTF8String(buffer, this.url);
		buffer.writeInt(volume);
		ByteBufUtils.writeUTF8String(buffer, this.trackId);
		ByteBufUtils.writeUTF8String(buffer, this.options);
		buffer.writeInt(dimensionId);
	}

	public static class Handler implements IMessageHandler<S2CMessageRegionTrackMusic, IMessage> {

		@Override
		public IMessage onMessage(S2CMessageRegionTrackMusic message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
			return null;
		}

		private void handle(S2CMessageRegionTrackMusic message, MessageContext ctx)
		{
			Main.proxy.regionTrackMusic(message.x1, message.y1, message.z1, message.x2, message.y2, message.z2, message.region, message.world, message.dimensionId, message.url, message.volume, message.trackId, message.options);
		}
	}
}