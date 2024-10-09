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

import java.util.Collections;
import java.util.List;

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

        List<Entity> entity = getEntityList(server, sender, args[0]);

        int volume;
        String url = args[2];

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

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
        }
        return Collections.emptyList();
    }
}