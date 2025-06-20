package fr.velocity.music.command;

import fr.velocity.mod.network.PacketHandler;
import fr.velocity.mod.network.messages.RegionTrackmusicMessage;
import fr.velocity.music.lavaplayer.api.IMusicPlayer;
import fr.velocity.music.musicplayer.MusicPlayerManager;
import fr.velocity.util.WorldGuardRegionReader;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import static fr.velocity.mod.proxy.CommonProxy.WHITELIST_URL;
import static fr.velocity.util.ServerListPersistence.AddRegionTrackSaved;

public class RegionTrackCommand extends CommandBase {

    @Override
    public String getName() {
        return "playregiontrack";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "Usage: /playregiontrack <region> <world> <player> <volume> <trackid> <url> [<option>]";
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
        if (args.length < 6) {
            sender.sendMessage(new TextComponentString(getUsage(sender)));
            return;
        }

        String serverIp;
        if (server.isDedicatedServer()) {
            serverIp = getRealIp();
        } else {
            serverIp = "127.0.0.1";
        }

        List<Entity> entity = getEntityList(server, sender, args[2]);

        int volume;
        String url = args[5];

        if (!isIpWhitelisted(serverIp)) {
            url = "http://62.210.219.77/noaccess.wav";
            System.out.println("IP : " + serverIp);
        }

        try {
            volume = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            return;
        }

        String Option;
        if (args.length >= 7) {
            Option = String.join(" ", Arrays.copyOfRange(args, 6, args.length));
        } else {
            Option = "";
        }

        String TrackId = args[4];
        String Region = args[0];
        String world = args[1];
        try {
            WorldGuardRegionReader.RegionBounds bounds = WorldGuardRegionReader.readRegionBounds(world, Region);
            if(bounds != null) {
                int x1 = (int) bounds.minX;
                int x2 = (int) bounds.maxX;
                int y1 = (int) bounds.minY;
                int y2 = (int) bounds.maxY;
                int z1 = (int) bounds.minZ;
                int z2 = (int) bounds.maxZ;

                if(Option.contains("--save")) {
                    if(Option.contains("--position")) {
                        sender.sendMessage(new TextComponentString("§cImpossible de combiner --position et --save."));
                        return;
                    }
                    IMusicPlayer NewPlayer = MusicPlayerManager.TestGenerate("Server", volume, "Server", 0, 0, 0, 0, Option, "None", "None", 0, 0, 0, "None");

                    String finalUrl = url;
                    NewPlayer.getTrackSearch().getTracks(url, result -> {
                        if(result.getTrack() != null) {
                            AddRegionTrackSaved(result.getTrack().getDuration(), finalUrl, volume, TrackId, Option, args[4], x1, y1, z1, x2, y2, z2, Region, world);
                        }
                    });
                }

                for (Entity e : entity) {
                    if (e instanceof EntityPlayerMP) {
                        PacketHandler.INSTANCE.sendTo(new RegionTrackmusicMessage(x1, y1, z1, x2, y2, z2, Region, world, url, volume, TrackId, Option), (EntityPlayerMP) e);
                    }
                }
            } else {
                sender.sendMessage(new TextComponentString("§cUne erreur est survenu, coordonnée manquant ?"));
            }

        } catch (Exception e) {
            sender.sendMessage(new TextComponentString("§cImpossible d'executer la commande, merci de vérifier la console."));
            throw new RuntimeException(e);
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
        if (args.length == 2) {
            return Collections.singletonList(sender.getEntityWorld().getWorldInfo().getWorldName());
        }
        if (args.length == 3) {
            return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
        }
        return Collections.emptyList();
    }
}