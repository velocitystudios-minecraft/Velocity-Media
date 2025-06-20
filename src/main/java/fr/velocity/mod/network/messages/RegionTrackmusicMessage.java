package fr.velocity.mod.network.messages;

import fr.velocity.Main;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.nio.charset.StandardCharsets;

public class RegionTrackmusicMessage implements IMessage
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
	private String Option;
	private String TrackId;
	private int DimensionId;

	public RegionTrackmusicMessage() {}

	public RegionTrackmusicMessage(int x1, int y1, int z1, int x2, int y2, int z2, String region, String world, int DimensionId, String url, int volume, String TrackId, String Option) {
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
		this.TrackId = TrackId;
		this.Option = Option;
		this.DimensionId = DimensionId;
	}

	@Override
	public void fromBytes(ByteBuf buffer) {
		this.x1 = buffer.readInt();
		this.y1 = buffer.readInt();
		this.z1 = buffer.readInt();
		this.x2 = buffer.readInt();
		this.y2 = buffer.readInt();
		this.z2 = buffer.readInt();
		this.region = String.valueOf(buffer.readCharSequence(buffer.readInt(), StandardCharsets.UTF_8));
		this.world = String.valueOf(buffer.readCharSequence(buffer.readInt(), StandardCharsets.UTF_8));
		this.DimensionId = buffer.readInt();
		this.url = String.valueOf(buffer.readCharSequence(buffer.readInt(), StandardCharsets.UTF_8));
		this.volume = buffer.readInt();
		this.TrackId = String.valueOf(buffer.readCharSequence(buffer.readInt(), StandardCharsets.UTF_8));
		this.Option = String.valueOf(buffer.readCharSequence(buffer.readInt(), StandardCharsets.UTF_8));
	}

	@Override
	public void toBytes(ByteBuf buffer) {
		buffer.writeInt(x1);
		buffer.writeInt(y1);
		buffer.writeInt(z1);
		buffer.writeInt(x2);
		buffer.writeInt(y2);
		buffer.writeInt(z2);
		buffer.writeInt(region.length());
		buffer.writeCharSequence(region, StandardCharsets.UTF_8);
		buffer.writeInt(world.length());
		buffer.writeCharSequence(world, StandardCharsets.UTF_8);
		buffer.writeInt(DimensionId);
		buffer.writeInt(url.length());
		buffer.writeCharSequence(url, StandardCharsets.UTF_8);
		buffer.writeInt(volume);
		buffer.writeInt(TrackId.length());
		buffer.writeCharSequence(TrackId, StandardCharsets.UTF_8);
		buffer.writeInt(Option.length());
		buffer.writeCharSequence(Option, StandardCharsets.UTF_8);
	}

	public static class Handler implements IMessageHandler<RegionTrackmusicMessage, IMessage> {

		@Override
		public IMessage onMessage(RegionTrackmusicMessage message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
			return null;
		}

		private void handle(RegionTrackmusicMessage message, MessageContext ctx)
		{
			Main.proxy.RegionTrackmusic(message.x1, message.y1, message.z1, message.x2, message.y2, message.z2, message.region, message.world, message.DimensionId, message.url, message.volume, message.TrackId, message.Option);
		}
	}
}