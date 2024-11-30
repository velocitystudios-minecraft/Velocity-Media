package fr.velocity.music.command;

import fr.velocity.mod.network.PacketHandler;
import fr.velocity.mod.network.messages.StopmusicMessage;
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

public class VolumeCommand extends CommandBase {

    @Override
    public String getName() {
        return "volumemusic";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "Usage: /volumemusic <player> <volume> [<trackid>]";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 2) {
            sender.sendMessage(new TextComponentString(getUsage(sender)));
            return;
        }

        List<Entity> entity = getEntityList(server, sender, args[0]);

        int volume;

        try {
            volume = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            return;
        }

        String TrackId = "ALL";
        if (args.length > 2) {
            TrackId = args[2];
        }

        for (Entity e : entity) {
            if (e instanceof EntityPlayerMP) {
                PacketHandler.INSTANCE.sendTo(new VolumemusicMessage(TrackId, volume), (EntityPlayerMP) e);
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