package fr.velocity.mod.network.messages;

import fr.velocity.Main;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.nio.charset.StandardCharsets;

public class StopmusicMessage implements IMessage
{


	public StopmusicMessage() {}

	public StopmusicMessage(String url, int volume) {

	}

	@Override
	public void fromBytes(ByteBuf buffer) {

	}

	@Override
	public void toBytes(ByteBuf buffer) {

	}

	public static class Handler implements IMessageHandler<StopmusicMessage, IMessage> {

		@Override
		public IMessage onMessage(StopmusicMessage message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
			return null;
		}

		private void handle(StopmusicMessage message, MessageContext ctx)
		{
			Main.proxy.Stopmusic();
		}
	}
}