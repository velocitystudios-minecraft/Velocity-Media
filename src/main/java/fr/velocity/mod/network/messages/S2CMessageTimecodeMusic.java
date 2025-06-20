package fr.velocity.mod.network.messages;

import fr.velocity.Main;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.nio.charset.StandardCharsets;

public class S2CMessageTimecodeMusic implements IMessage
{
	private long position;
	private String TrackId;

	public S2CMessageTimecodeMusic() {}

	public S2CMessageTimecodeMusic(String TrackId, long position) {
		this.TrackId = TrackId;
		this.position = position;
	}

	@Override
	public void fromBytes(ByteBuf buffer) {
		this.TrackId = String.valueOf(buffer.readCharSequence(buffer.readInt(), StandardCharsets.UTF_8));
		this.position = buffer.readLong();
	}

	@Override
	public void toBytes(ByteBuf buffer) {
		buffer.writeInt(TrackId.length());
		buffer.writeCharSequence(TrackId, StandardCharsets.UTF_8);
		buffer.writeLong(position);
	}

	public static class Handler implements IMessageHandler<S2CMessageTimecodeMusic, IMessage> {

		@Override
		public IMessage onMessage(S2CMessageTimecodeMusic message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
			return null;
		}

		private void handle(S2CMessageTimecodeMusic message, MessageContext ctx)
		{
			Main.proxy.changeTimecodeMusic(message.TrackId, message.position);
		}
	}
}