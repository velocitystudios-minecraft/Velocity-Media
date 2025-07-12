package fr.velocity.music.command.music;

import fr.velocity.mod.network.PacketHandler;
import fr.velocity.mod.network.messages.S2CMessageVolumeMusic;
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

public class VolumeCommand implements ISubCommand {

    @Override
    public String getSubName() {
        return "volume";
    }

    @Override
    public int getSubRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/music " + getSubName() + " <player> <volume> [<trackid>]";
    }

    @Override
    public void subExecute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 2) {
            throw new WrongUsageException(getUsage(sender));
        }

        List<Entity> entity = CommandBase.getEntityList(server, sender, args[0]);

        int volume;

        try {
            volume = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            throw new CommandException("Le volume doit être un nombre valide.");
        }

        String trackId = "ALL";
        if (args.length > 2) {
            trackId = args[2];
        }

        for (Entity e : entity) {
            if (e instanceof EntityPlayerMP) {
                PacketHandler.INSTANCE.sendTo(new S2CMessageVolumeMusic(trackId, volume), (EntityPlayerMP) e);
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