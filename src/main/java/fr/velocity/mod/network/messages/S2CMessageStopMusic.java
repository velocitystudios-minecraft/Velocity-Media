package fr.velocity.mod.network.messages;

import fr.velocity.Main;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.nio.charset.StandardCharsets;

public class S2CMessageStopMusic implements IMessage
{
	private String TrackId;

	public S2CMessageStopMusic() {}

	public S2CMessageStopMusic(String TrackId) {
		this.TrackId = TrackId;
	}

	@Override
	public void fromBytes(ByteBuf buffer) {
		this.TrackId = String.valueOf(buffer.readCharSequence(buffer.readInt(), StandardCharsets.UTF_8));
	}

	@Override
	public void toBytes(ByteBuf buffer) {
		buffer.writeInt(TrackId.length());
		buffer.writeCharSequence(TrackId, StandardCharsets.UTF_8);
	}

	public static class Handler implements IMessageHandler<S2CMessageStopMusic, IMessage> {

		@Override
		public IMessage onMessage(S2CMessageStopMusic message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
			return null;
		}

		private void handle(S2CMessageStopMusic message, MessageContext ctx)
		{
			Main.proxy.stopMusic(message.TrackId);
		}
	}
}