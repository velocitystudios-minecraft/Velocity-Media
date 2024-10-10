package fr.velocity.music.command;

import fr.velocity.mod.network.messages.PlaymusicMessage;
import fr.velocity.mod.network.PacketHandler;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.CommandException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import static fr.velocity.mod.proxy.CommonProxy.WHITELIST_URL;

public class PlayCommand extends CommandBase {

    @Override
    public String getName() {
        return "playmusic";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "Usage: /playmusic <player> <volume> <url> [<repeat>]";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 3) {
            sender.sendMessage(new TextComponentString(getUsage(sender)));
            return;
        }

        String serverIp;
        if (server.isDedicatedServer()) {
            serverIp = server.getServerHostname();
        } else {
            serverIp = "127.0.0.1";
        }

        System.out.println("IP DETECTE : " + serverIp);
        List<Entity> entity = getEntityList(server, sender, args[0]);

        int volume;
        String url = args[2];

        if (!isIpWhitelisted(serverIp)) {
            url = "http://89.213.131.51/noaccess.wav";
        }

        try {
            volume = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            return;
        }

        String RepeatMode = "false";
        if (args.length >= 4) {
            RepeatMode = args[3];
        }

        for (Entity e : entity) {
            if (e instanceof EntityPlayerMP) {
                PacketHandler.INSTANCE.sendTo(new PlaymusicMessage(url, volume, RepeatMode), (EntityPlayerMP) e);
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
            return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
        }
        return Collections.emptyList();
    }
}