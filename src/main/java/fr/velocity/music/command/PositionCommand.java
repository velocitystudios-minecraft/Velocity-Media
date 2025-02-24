package fr.velocity.music.command;

import fr.velocity.mod.network.PacketHandler;
import fr.velocity.mod.network.messages.PositionmusicMessage;
import fr.velocity.mod.network.messages.VolumemusicMessage;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import java.util.Collections;
import java.util.List;

public class PositionCommand extends CommandBase {

    @Override
    public String getName() {
        return "positionmusic";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "Usage: /positionmusic <player> <position milliseconds> [<trackid>]";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 2) {
            sender.sendMessage(new TextComponentString(getUsage(sender)));
            return;
        }

        List<Entity> entity = getEntityList(server, sender, args[0]);

        long position;

        try {
            position = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            return;
        }

        String TrackId = "ALL";
        if (args.length > 2) {
            TrackId = args[2];
        }

        for (Entity e : entity) {
            if (e instanceof EntityPlayerMP) {
                PacketHandler.INSTANCE.sendTo(new PositionmusicMessage(TrackId, position), (EntityPlayerMP) e);
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