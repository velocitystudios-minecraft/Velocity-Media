package fr.velocity.mod.network.messages;

import fr.velocity.Main;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.nio.charset.StandardCharsets;

public class S2CMessagePlayMusic implements IMessage
{

	private String url;
	private int volume;
	private String repeatMode;

	public S2CMessagePlayMusic() {}

	public S2CMessagePlayMusic(String url, int volume, String repeatMode) {
		this.url = url;
		this.volume = volume;
		this.repeatMode = repeatMode;
	}

	@Override
	public void fromBytes(ByteBuf buffer) {
		this.url = ByteBufUtils.readUTF8String(buffer);
		this.volume = buffer.readInt();
		this.repeatMode = ByteBufUtils.readUTF8String(buffer);
	}

	@Override
	public void toBytes(ByteBuf buffer) {
		ByteBufUtils.writeUTF8String(buffer, this.url);
		buffer.writeInt(volume);
		ByteBufUtils.writeUTF8String(buffer, this.repeatMode);
	}

	public static class Handler implements IMessageHandler<S2CMessagePlayMusic, IMessage> {

		@Override
		public IMessage onMessage(S2CMessagePlayMusic message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
			return null;
		}

		private void handle(S2CMessagePlayMusic message, MessageContext ctx)
		{
			Main.proxy.playMusic(message.url, message.volume, message.repeatMode);
		}
	}
}