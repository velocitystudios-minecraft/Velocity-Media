package fr.velocity.music.command.music;

import fr.velocity.mod.network.PacketHandler;
import fr.velocity.mod.network.messages.S2CMessagePauseMusic;
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

public class PauseCommand implements ISubCommand {

    @Override
    public String getSubName() {
        return "pause";
    }

    @Override
    public int getSubRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/music " + getSubName() + " <player> <pause> [<trackid>]";
    }

    @Override
    public void subExecute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 2) {
            throw new WrongUsageException(getUsage(sender));
        }

        String trackId = "ALL";
        if (args.length > 2) {
            trackId = args[2];
        }

        List<Entity> entity = CommandBase.getEntityList(server, sender, args[0]);
        String targetName = args[0];
        boolean pause;
        try {
            pause = Boolean.parseBoolean(args[1]);
        } catch (Exception e) {
            throw new CommandException("Error: The second argument must be 'true' or 'false'.");
        }

        for (Entity e : entity) {
            if (e instanceof EntityPlayerMP) {
                PacketHandler.INSTANCE.sendTo(new S2CMessagePauseMusic(trackId, pause), (EntityPlayerMP) e);
            }
        }
    }

    @Override
    public List<String> getSubTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        System.out.println(args.length);
        System.out.println("Tab completions for PauseCommand called with args: " + String.join(", ", args));
        if (args.length == 1) {
            return CommandBase.getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
        } else if (args.length == 2) {
            return CommandBase.getListOfStringsMatchingLastWord(args, "true", "false");
        }
        return Collections.emptyList();
    }
}