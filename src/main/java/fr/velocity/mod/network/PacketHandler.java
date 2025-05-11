package fr.velocity.mod.network;

import fr.velocity.mod.network.messages.*;
import fr.velocity.Main;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler {
    private static int packetId = 0;

    public static SimpleNetworkWrapper INSTANCE = null;

    public PacketHandler() {
    }

    public static int nextID() {
        return packetId++;
    }

    public static void registerMessages() {
        if (PacketHandler.INSTANCE != null)
            return;
        System.out.println("Register PacketHandler");
        INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Main.modid);
        register();
    }

    private static void register() {
        System.out.println("REGISTER PACKET");
        INSTANCE.registerMessage(OpenVideoManagerScreen.Handler.class, OpenVideoManagerScreen.class, nextID(), Side.CLIENT);
        INSTANCE.registerMessage(SendVideoMessage.Handler.class, SendVideoMessage.class, nextID(), Side.CLIENT);
        INSTANCE.registerMessage(PlaymusicMessage.Handler.class, PlaymusicMessage.class, nextID(), Side.CLIENT);
        INSTANCE.registerMessage(PlayerTrackmusicMessage.Handler.class, PlayerTrackmusicMessage.class, nextID(), Side.CLIENT);
        INSTANCE.registerMessage(TrackmusicMessage.Handler.class, TrackmusicMessage.class, nextID(), Side.CLIENT);
        INSTANCE.registerMessage(PositionTrackmusicMessage.Handler.class, PositionTrackmusicMessage.class, nextID(), Side.CLIENT);
        INSTANCE.registerMessage(StopmusicMessage.Handler.class, StopmusicMessage.class, nextID(), Side.CLIENT);
        INSTANCE.registerMessage(PausemusicMessage.Handler.class, PausemusicMessage.class, nextID(), Side.CLIENT);
        INSTANCE.registerMessage(VolumemusicMessage.Handler.class, VolumemusicMessage.class, nextID(), Side.CLIENT);
        INSTANCE.registerMessage(PositionmusicMessage.Handler.class, PositionmusicMessage.class, nextID(), Side.CLIENT);
        INSTANCE.registerMessage(FrameVideoMessage.Handler.class, FrameVideoMessage.class, nextID(), Side.CLIENT);
        INSTANCE.registerMessage(UploadVideoUpdateMessage.Handler.class, UploadVideoUpdateMessage.class, nextID(), Side.SERVER);
    }
}