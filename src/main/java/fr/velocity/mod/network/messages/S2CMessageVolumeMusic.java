package fr.velocity.mod.network.messages;

import fr.velocity.Main;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.nio.charset.StandardCharsets;

public class S2CMessageVolumeMusic implements IMessage
{

	private String trackId;
	private int volume;

	public S2CMessageVolumeMusic() {}

	public S2CMessageVolumeMusic(String trackId, int volume) {
		this.trackId = trackId;
		this.volume = volume;
	}

	@Override
	public void fromBytes(ByteBuf buffer) {
		this.trackId = ByteBufUtils.readUTF8String(buffer);
		this.volume = buffer.readInt();
	}

	@Override
	public void toBytes(ByteBuf buffer) {
		ByteBufUtils.writeUTF8String(buffer, this.trackId);
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
			Main.proxy.volumeMusic(message.trackId, message.volume);
		}
	}
}