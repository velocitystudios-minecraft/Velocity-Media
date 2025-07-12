package fr.velocity.music.command.music;

import fr.velocity.mod.network.PacketHandler;
import fr.velocity.mod.network.messages.S2CMessageStopMusic;
import fr.velocity.music.command.ISubCommand;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import java.util.Collections;
import java.util.List;

import static fr.velocity.util.ServerListPersistence.RemoveTrackId;
import static fr.velocity.util.ServerListPersistence.saveASave;

public class StopCommand implements ISubCommand {

    @Override
    public String getSubName() {
        return "stop";
    }

    @Override
    public int getSubRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/music " + getSubName() + " <player> [<trackid>]";
    }

    @Override
    public void subExecute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 1) {
            throw new WrongUsageException(getUsage(sender));
        }

        String trackId = "ALL";
        if (args.length > 1) {
            trackId = args[1];
        }

        List<Entity> entity = CommandBase.getEntityList(server, sender, args[0]);

        if (args[0].equalsIgnoreCase("@a")) {
            if (trackId.equalsIgnoreCase("ALL")) {
                saveASave();
            }
            RemoveTrackId(trackId);
        }

        for (Entity e : entity) {
            if (e instanceof EntityPlayerMP) {
                PacketHandler.INSTANCE.sendTo(new S2CMessageStopMusic(trackId), (EntityPlayerMP) e);
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