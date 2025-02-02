package fr.velocity.music.command;

import fr.velocity.mod.network.PacketHandler;
import fr.velocity.mod.network.messages.PositionTrackmusicMessage;
import fr.velocity.mod.network.messages.TrackmusicMessage;
import fr.velocity.music.lavaplayer.api.IMusicPlayer;
import fr.velocity.music.musicplayer.MusicPlayerManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.*;

import static fr.velocity.mod.proxy.CommonProxy.WHITELIST_URL;
import static fr.velocity.util.ServerListPersistence.AddLocationTrackSaved;
import static fr.velocity.util.ServerListPersistence.AddTrackSaved;

public class PositionTrackCommand extends CommandBase {

    @Override
    public String getName() {
        return "playpositiontrack";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "Usage: /playpositiontrack <x> <y> <z> <radius> <player> <volume> <trackid> <url> [<option>]";
    }

    public static String getRealIp() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue;
                }
                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if (address.isSiteLocalAddress()) {
                        return address.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "127.0.0.1";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 8) {
            sender.sendMessage(new TextComponentString(getUsage(sender)));
            return;
        }

        String serverIp;
        if (server.isDedicatedServer()) {
            serverIp = getRealIp();
        } else {
            serverIp = "127.0.0.1";
        }

        List<Entity> entity = getEntityList(server, sender, args[4]);

        int volume;
        String url = args[7];

        if (!isIpWhitelisted(serverIp)) {
            url = "http://62.210.219.77/noaccess.wav";
            System.out.println("IP : " + serverIp);
        }

        try {
            volume = Integer.parseInt(args[5]);
        } catch (NumberFormatException e) {
            return;
        }

        String Option;
        if (args.length >= 9) {
            Option = String.join(" ", Arrays.copyOfRange(args, 8, args.length));
        } else {
            Option = "";
        }

        String TrackId = args[6];

        int x = parseInt(args[0]);
        int y = parseInt(args[1]);
        int z = parseInt(args[2]);
        int radius = parseInt(args[3]);

        if(Option.contains("--save")) {
            if(Option.contains("--position")) {
                sender.sendMessage(new TextComponentString("§cImpossible de combiner --position et --save."));
                return;
            }
            IMusicPlayer NewPlayer = MusicPlayerManager.TestGenerate("Server");

            String finalUrl = url;
            NewPlayer.getTrackSearch().getTracks(url, result -> {
                if(result.getTrack() != null) {
                    AddLocationTrackSaved(result.getTrack().getDuration(), finalUrl, volume, TrackId, Option, args[4], x, y, z, radius);
                }
            });
        }

        for (Entity e : entity) {
            if (e instanceof EntityPlayerMP) {
                PacketHandler.INSTANCE.sendTo(new PositionTrackmusicMessage(x, y, z, radius, url, volume, TrackId, Option), (EntityPlayerMP) e);
            }
        }
    }

    private boolean isIpWhitelisted(String serverIp) {
        try {
            URL url = new URL(WHITELIST_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine).append("\n");
            }
            in.close();
            connection.disconnect();

            String[] whitelistedIps = content.toString().split("\n");

            for (String ip : whitelistedIps) {
                if (ip.trim().equals(serverIp)) {
                    return true;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, String.valueOf(sender.getPosition().getX()));
        } else if (args.length == 2) {
            return getListOfStringsMatchingLastWord(args, String.valueOf(sender.getPosition().getY()));
        } else if (args.length == 3) {
            return getListOfStringsMatchingLastWord(args, String.valueOf(sender.getPosition().getZ()));
        }
        if (args.length == 5) {
            return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
        }
        return Collections.emptyList();
    }
}