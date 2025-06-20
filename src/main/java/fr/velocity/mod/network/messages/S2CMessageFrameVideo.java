package fr.velocity.mod.network.messages;

import fr.velocity.Main;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class S2CMessageFrameVideo implements IMessage {

    private BlockPos pos;
    private boolean playing;
    private int tick;

    public S2CMessageFrameVideo() {}

    public S2CMessageFrameVideo(BlockPos pos, boolean playing, int tick) {
        this.pos = pos;
        this.playing = playing;
        this.tick = tick;
    }

    @Override
    public void toBytes(ByteBuf buffer) {
        buffer.writeLong(pos.toLong());
        buffer.writeBoolean(playing);
        buffer.writeInt(tick);
    }

    @Override
    public void fromBytes(ByteBuf buffer) {
        this.pos = BlockPos.fromLong(buffer.readLong());
        this.playing = buffer.readBoolean();
        this.tick = buffer.readInt();
    }

    public static class Handler implements IMessageHandler<S2CMessageFrameVideo, IMessage> {

        @Override
        public IMessage onMessage(S2CMessageFrameVideo message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(S2CMessageFrameVideo message, MessageContext ctx)
        {
            Main.proxy.manageVideo(message.pos, message.playing, message.tick);
        }
    }
}