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

public class StopCommand extends CommandBase {

    @Override
    public String getName() {
        return "stopmusic";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "Usage: /stopmusic <player>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 1) {
            sender.sendMessage(new TextComponentString(getUsage(sender)));
            return;
        }

        String targetName = args[0];
        EntityPlayerMP target = getPlayer(server, sender, targetName);

        if (target == null) {
            sender.sendMessage(new TextComponentString("Player not found!"));
            return;
        }

        target.getServer().getCommandManager().executeCommand(target, "localstopmusic");
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
        }
        return Collections.emptyList();
    }
}