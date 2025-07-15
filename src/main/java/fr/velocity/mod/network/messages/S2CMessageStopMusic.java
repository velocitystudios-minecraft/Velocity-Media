package fr.velocity.mod.network.messages;

import fr.velocity.Main;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class S2CMessageStopMusic implements IMessage
{
	private String trackId;

	public S2CMessageStopMusic() {}

	public S2CMessageStopMusic(String trackId) {
		this.trackId = trackId;
	}

	@Override
	public void fromBytes(ByteBuf buffer) {
		this.trackId = ByteBufUtils.readUTF8String(buffer);
	}

	@Override
	public void toBytes(ByteBuf buffer) {
		ByteBufUtils.writeUTF8String(buffer, this.trackId);
	}

	public static class Handler implements IMessageHandler<S2CMessageStopMusic, IMessage> {

		@Override
		public IMessage onMessage(S2CMessageStopMusic message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
			return null;
		}

		private void handle(S2CMessageStopMusic message, MessageContext ctx)
		{
			Main.proxy.stopMusic(message.trackId);
		}
	}
}