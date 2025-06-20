package fr.velocity.mod.network.messages;

import fr.velocity.Main;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.nio.charset.StandardCharsets;

public class S2CMessagePauseMusic implements IMessage
{

	private String trackId;
	private boolean isPaused;

	public S2CMessagePauseMusic() {}

	public S2CMessagePauseMusic(String trackId, boolean isPaused) {
		this.trackId = trackId;
		this.isPaused = isPaused;
	}

	@Override
	public void fromBytes(ByteBuf buffer) {
		this.trackId = ByteBufUtils.readUTF8String(buffer);
		this.isPaused = buffer.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buffer) {
		ByteBufUtils.writeUTF8String(buffer, trackId);
		buffer.writeBoolean(isPaused);
	}

	public static class Handler implements IMessageHandler<S2CMessagePauseMusic, IMessage> {

		@Override
		public IMessage onMessage(S2CMessagePauseMusic message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
			return null;
		}

		private void handle(S2CMessagePauseMusic message, MessageContext ctx)
		{
			Main.proxy.pauseMusic(message.trackId, message.isPaused);
		}
	}
}