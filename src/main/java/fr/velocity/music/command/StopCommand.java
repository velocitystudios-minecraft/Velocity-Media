package fr.velocity.music.command;

import fr.velocity.mod.network.PacketHandler;
import fr.velocity.mod.network.messages.PlaymusicMessage;
import fr.velocity.mod.network.messages.StopmusicMessage;
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

import static fr.velocity.util.ServerListPersistence.RemoveTrackId;

public class StopCommand extends CommandBase {

    @Override
    public String getName() {
        return "stopmusic";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "Usage: /stopmusic <player> [<trackid>]";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 1) {
            sender.sendMessage(new TextComponentString(getUsage(sender)));
            return;
        }

        String TrackId = "ALL";
        if (args.length > 1) {
            TrackId = args[1];
        }

        List<Entity> entity = getEntityList(server, sender, args[0]);

        RemoveTrackId(TrackId);


        for (Entity e : entity) {
            if (e instanceof EntityPlayerMP) {
                PacketHandler.INSTANCE.sendTo(new StopmusicMessage(TrackId), (EntityPlayerMP) e);
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