package fr.velocity.mod.network.messages;

import fr.velocity.Main;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.nio.charset.StandardCharsets;

public class S2CMessageVolumeMusic implements IMessage
{
	private int volume;
	private String TrackId;

	public S2CMessageVolumeMusic() {}

	public S2CMessageVolumeMusic(String TrackId, int volume) {
		this.TrackId = TrackId;
		this.volume = volume;
	}

	@Override
	public void fromBytes(ByteBuf buffer) {
		this.TrackId = String.valueOf(buffer.readCharSequence(buffer.readInt(), StandardCharsets.UTF_8));
		this.volume = buffer.readInt();
	}

	@Override
	public void toBytes(ByteBuf buffer) {
		buffer.writeInt(TrackId.length());
		buffer.writeCharSequence(TrackId, StandardCharsets.UTF_8);
		buffer.writeInt(volume);
	}

	public static class Handler implements IMessageHandler<S2CMessageVolumeMusic, IMessage> {

		@Override
		public IMessage onMessage(S2CMessageVolumeMusic message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
			return null;
		}

		private void handle(S2CMessageVolumeMusic message, MessageContext ctx)
		{
			Main.proxy.volumeMusic(message.TrackId, message.volume);
		}
	}
}