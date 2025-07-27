package fr.velocity.mod.network;

import fr.velocity.Main;
import fr.velocity.mod.network.messages.*;
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

        /**
         * Server to Client Messages
         * * These messages are sent from the server to the client.
         */

        INSTANCE.registerMessage(S2CMessageOpenVideoManagerScreen.Handler.class, S2CMessageOpenVideoManagerScreen.class, nextID(), Side.CLIENT);
        INSTANCE.registerMessage(S2CMessageSendVideo.Handler.class, S2CMessageSendVideo.class, nextID(), Side.CLIENT);
        INSTANCE.registerMessage(S2CMessagePlayMusic.Handler.class, S2CMessagePlayMusic.class, nextID(), Side.CLIENT);
        INSTANCE.registerMessage(S2CMessagePlayerTrackMusic.Handler.class, S2CMessagePlayerTrackMusic.class, nextID(), Side.CLIENT);
        INSTANCE.registerMessage(S2CMessageTrackMusic.Handler.class, S2CMessageTrackMusic.class, nextID(), Side.CLIENT);
        INSTANCE.registerMessage(S2CMessagePositionTrackMusic.Handler.class, S2CMessagePositionTrackMusic.class, nextID(), Side.CLIENT);
        INSTANCE.registerMessage(S2CMessageStopMusic.Handler.class, S2CMessageStopMusic.class, nextID(), Side.CLIENT);
        INSTANCE.registerMessage(S2CMessagePauseMusic.Handler.class, S2CMessagePauseMusic.class, nextID(), Side.CLIENT);
        INSTANCE.registerMessage(S2CMessageVolumeMusic.Handler.class, S2CMessageVolumeMusic.class, nextID(), Side.CLIENT);
        INSTANCE.registerMessage(S2CMessageTimecodeMusic.Handler.class, S2CMessageTimecodeMusic.class, nextID(), Side.CLIENT);
        INSTANCE.registerMessage(S2CMessageRegionTrackMusic.Handler.class, S2CMessageRegionTrackMusic.class, nextID(), Side.CLIENT);
        INSTANCE.registerMessage(S2CMessageFrameVideo.Handler.class, S2CMessageFrameVideo.class, nextID(), Side.CLIENT);
        INSTANCE.registerMessage(S2CMessageInfoMusic.Handler.class, S2CMessageInfoMusic.class, nextID(), Side.CLIENT);

        /**
         * Client to Server Messages
         * * These messages are sent from the client to the server.
         */

        INSTANCE.registerMessage(C2SMessageUploadVideoUpdate.Handler.class, C2SMessageUploadVideoUpdate.class, nextID(), Side.SERVER);
    }
}