package fr.velocity.music.command.music.playtrack;

import fr.velocity.mod.network.PacketHandler;
import fr.velocity.mod.network.messages.S2CMessageTrackMusic;
import fr.velocity.music.command.ISubCommand;
import fr.velocity.music.lavaplayer.api.IMusicPlayer;
import fr.velocity.music.musicplayer.MusicPlayerManager;
import fr.velocity.util.WhitelistUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static fr.velocity.util.ServerListPersistence.AddTrackSaved;
import static fr.velocity.util.WhitelistUtil.isIpWhitelisted;

public class TrackCommand implements ISubCommand {

    @Override
    public String getSubName() {
        return "track";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/music play " + getSubName() + " <player> <volume> <trackid> <url> [<option>]";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 4) {
            throw new WrongUsageException(getUsage(sender));
        }

        String serverIp = WhitelistUtil.getServerIp(server);

        List<Entity> entity = CommandBase.getEntityList(server, sender, args[0]);

        int volume;
        String url = args[3];

        if (!isIpWhitelisted(serverIp)) {
            url = "http://62.210.219.77/noaccess.wav";
            System.out.println("IP : " + serverIp);
        }

        try {
            volume = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            return;
        }

        String Option;
        if (args.length >= 5) {
            Option = String.join(" ", Arrays.copyOfRange(args, 4, args.length));
        } else {
            Option = "";
        }

        String TrackId = args[2];

        String finalUrl = url;
        if(Option.contains("--save")) {
            if(Option.contains("--position")) {
                sender.sendMessage(new TextComponentString("§cImpossible de combiner --position et --save."));
                return;
            }

            IMusicPlayer NewPlayer = MusicPlayerManager.testGenerate("Server", volume, "Server", 0, 0, 0, 0, Option, "None", "None", 0, 0, 0, "None", 0);

            NewPlayer.getTrackSearch().getTracks(url, result -> {
                if(result.getTrack() != null) {
                    AddTrackSaved(result.getTrack().getDuration(), finalUrl, volume, TrackId, Option, args[0]);
                }
            });
        }

        for (Entity e : entity) {
            if (e instanceof EntityPlayerMP) {
                PacketHandler.INSTANCE.sendTo(new S2CMessageTrackMusic(url, volume, TrackId, Option), (EntityPlayerMP) e);
            }
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        if (args.length == 1) {
            return CommandBase.getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
        }
        return Collections.emptyList();
    }
}