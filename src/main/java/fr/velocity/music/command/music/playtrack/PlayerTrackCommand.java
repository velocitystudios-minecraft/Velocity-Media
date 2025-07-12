package fr.velocity.music.command.music.playtrack;

import fr.velocity.mod.network.PacketHandler;
import fr.velocity.mod.network.messages.S2CMessagePlayerTrackMusic;
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

import static fr.velocity.util.ServerListPersistence.AddPlayerTrackSaved;
import static fr.velocity.util.WhitelistUtil.isIpWhitelisted;

public class PlayerTrackCommand implements ISubCommand {

    @Override
    public String getSubName() {
        return "playertrack";
    }

    @Override
    public int getSubRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/music play " + getSubName() + " <player> <radius> <volume> <trackid> <url> [<option>]";
    }

    @Override
    public void subExecute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 5) {
            throw new WrongUsageException(getUsage(sender));
        }

        String serverIp = WhitelistUtil.getServerIp(server);

        List<Entity> entity = CommandBase.getEntityList(server, sender, "@a");

        int Radius;
        try {
            Radius = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            throw new CommandException("Le radius doit être un nombre valide.");
        }

        int volume;
        String url = args[4];

        if (!isIpWhitelisted(serverIp)) {
            url = "http://62.210.219.77/noaccess.wav";
            System.out.println("IP : " + serverIp);
        }

        try {
            volume = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            throw new CommandException("Le volume doit être un nombre valide.");
        }

        String Option;
        if (args.length >= 6) {
            Option = String.join(" ", Arrays.copyOfRange(args, 5, args.length));
        } else {
            Option = "false";
        }

        String TrackId = args[3];

        if(Option.contains("--save")) {
            if(Option.contains("--position")) {
                sender.sendMessage(new TextComponentString("§cImpossible de combiner --position et --save."));
                return;
            }
            if(args[0].contains("@")) {
                if(!args[0].contains("@a")) {
                    sender.sendMessage(new TextComponentString("§cUniquement @a est autorisé en sauvegarde."));
                    return;
                }
            }
            IMusicPlayer NewPlayer = MusicPlayerManager.testGenerate("Server", volume, "Server", 0, 0, 0, 0, Option, "None", "None", 0, 0, 0, "None", 0);

            String finalUrl = url;
            NewPlayer.getTrackSearch().getTracks(url, result -> {
                if(result.getTrack() != null) {
                    AddPlayerTrackSaved(result.getTrack().getDuration(), finalUrl, volume, TrackId, Option, args[0], Radius);
                }
            });
        }

        for (Entity e : entity) {
            if (e instanceof EntityPlayerMP) {
                PacketHandler.INSTANCE.sendTo(new S2CMessagePlayerTrackMusic(args[0], Radius, url, volume, TrackId, Option), (EntityPlayerMP) e);
            }
        }
    }

    @Override
    public List<String> getSubTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        if (args.length == 1) {
            return CommandBase.getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
        }
        return Collections.emptyList();
    }
}