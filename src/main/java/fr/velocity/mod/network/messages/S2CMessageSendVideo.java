package fr.velocity.mod.network.messages;

import fr.velocity.Main;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.nio.charset.StandardCharsets;

public class S2CMessageSendVideo implements IMessage
{

	private String url;
	private int volume;
	private boolean controlBlocked;
	private int TimePosition;
	private float VideoSpeed;

	public S2CMessageSendVideo() {}

	public S2CMessageSendVideo(String url, int volume, boolean controlBlocked, int TimePosition, float VideoSpeed) {
		this.url = url;
		this.volume = volume;
		this.controlBlocked = controlBlocked;
		this.TimePosition = TimePosition;
		this.VideoSpeed = VideoSpeed;
	}

	@Override
	public void fromBytes(ByteBuf buffer) {
		int l = buffer.readInt();
		this.url = String.valueOf(buffer.readCharSequence(l, StandardCharsets.UTF_8));
		this.volume = buffer.readInt();
		this.controlBlocked = buffer.readBoolean();
		this.TimePosition = buffer.readInt();
		this.VideoSpeed = buffer.readFloat();
	}

	@Override
	public void toBytes(ByteBuf buffer) {
		buffer.writeInt(url.length());
		buffer.writeCharSequence(url, StandardCharsets.UTF_8);
		buffer.writeInt(volume);
		buffer.writeBoolean(controlBlocked);
		buffer.writeInt(TimePosition);
		buffer.writeFloat(VideoSpeed);
	}

	public static class Handler implements IMessageHandler<S2CMessageSendVideo, IMessage> {

		@Override
		public IMessage onMessage(S2CMessageSendVideo message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
			return null;
		}

		private void handle(S2CMessageSendVideo message, MessageContext ctx)
		{
			Main.proxy.openVideo(message.url, message.volume, message.controlBlocked, message.TimePosition, message.VideoSpeed);
		}
	}
}