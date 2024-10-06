package info.u_team.music_player.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import java.util.Collections;
import java.util.List;

public class VolumeCommand extends CommandBase {

    @Override
    public String getName() {
        return "volumemusic";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "Usage: /volumemusic <player> <volume>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 1) {
            sender.sendMessage(new TextComponentString(getUsage(sender)));
            return;
        }

        String targetName = args[0];
        int volume;

        try {
            volume = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            return;
        }

        EntityPlayerMP target = getPlayer(server, sender, targetName);
        if (target == null) {
            return;
        }

        target.getServer().getCommandManager().executeCommand(target, "localvolumemusic " + volume);
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
        }
        return Collections.emptyList();
    }
}