package fr.velocity.mod.network.messages;

import fr.velocity.Main;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.nio.charset.StandardCharsets;

public class PausemusicMessage implements IMessage
{

	private String IsPaused;
	private String TrackId;

	public PausemusicMessage() {}

	public PausemusicMessage(String TrackId, String IsPaused) {
		this.IsPaused = IsPaused;
		this.TrackId = TrackId;
	}

	@Override
	public void fromBytes(ByteBuf buffer) {
		int l = buffer.readInt();
		this.IsPaused = String.valueOf(buffer.readCharSequence(l, StandardCharsets.UTF_8));
		int l2 = buffer.readInt();
		this.TrackId = String.valueOf(buffer.readCharSequence(l2, StandardCharsets.UTF_8));
	}

	@Override
	public void toBytes(ByteBuf buffer) {
		buffer.writeInt(IsPaused.length());
		buffer.writeCharSequence(IsPaused, StandardCharsets.UTF_8);
		buffer.writeInt(TrackId.length());
		buffer.writeCharSequence(TrackId, StandardCharsets.UTF_8);
	}

	public static class Handler implements IMessageHandler<PausemusicMessage, IMessage> {

		@Override
		public IMessage onMessage(PausemusicMessage message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
			return null;
		}

		private void handle(PausemusicMessage message, MessageContext ctx)
		{
			Main.proxy.Pausemusic(message.TrackId, message.IsPaused);
		}
	}
}