package fr.velocity.mod.network.messages;

import fr.velocity.Main;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.nio.charset.StandardCharsets;

public class S2CMessagePlayMusic implements IMessage
{

	private String url;
	private int volume;
	private String RepeatMode;

	public S2CMessagePlayMusic() {}

	public S2CMessagePlayMusic(String url, int volume, String RepeatMode) {
		this.url = url;
		this.volume = volume;
		this.RepeatMode = RepeatMode;
	}

	@Override
	public void fromBytes(ByteBuf buffer) {
		this.url = String.valueOf(buffer.readCharSequence(buffer.readInt(), StandardCharsets.UTF_8));
		this.volume = buffer.readInt();
		this.RepeatMode = String.valueOf(buffer.readCharSequence(buffer.readInt(), StandardCharsets.UTF_8));
	}

	@Override
	public void toBytes(ByteBuf buffer) {
		buffer.writeInt(url.length());
		buffer.writeCharSequence(url, StandardCharsets.UTF_8);
		buffer.writeInt(volume);
		buffer.writeInt(RepeatMode.length());
		buffer.writeCharSequence(RepeatMode, StandardCharsets.UTF_8);
	}

	public static class Handler implements IMessageHandler<S2CMessagePlayMusic, IMessage> {

		@Override
		public IMessage onMessage(S2CMessagePlayMusic message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
			return null;
		}

		private void handle(S2CMessagePlayMusic message, MessageContext ctx)
		{
			Main.proxy.playMusic(message.url, message.volume, message.RepeatMode);
		}
	}
}