package fr.velocity.mod.network.messages;

import fr.velocity.Main;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.nio.charset.StandardCharsets;

public class VolumemusicMessage implements IMessage
{
	private int volume;

	public VolumemusicMessage() {}

	public VolumemusicMessage(int volume) {
		this.volume = volume;
	}

	@Override
	public void fromBytes(ByteBuf buffer) {
		this.volume = buffer.readInt();
	}

	@Override
	public void toBytes(ByteBuf buffer) {
		buffer.writeInt(volume);
	}

	public static class Handler implements IMessageHandler<VolumemusicMessage, IMessage> {

		@Override
		public IMessage onMessage(VolumemusicMessage message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
			return null;
		}

		private void handle(VolumemusicMessage message, MessageContext ctx)
		{
			Main.proxy.Volumemusic(message.volume);
		}
	}
}