package fr.velocity.music.command;

import fr.velocity.mod.network.PacketHandler;
import fr.velocity.mod.network.messages.PausemusicMessage;
import fr.velocity.mod.network.messages.PlaymusicMessage;
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

public class PauseCommand extends CommandBase {

    @Override
    public String getName() {
        return "pausemusic";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "Usage: /pausemusic <player> <pause>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 2) {  // Ensure there are at least 2 arguments
            sender.sendMessage(new TextComponentString(getUsage(sender)));
            return;
        }

        List<Entity> entity = getEntityList(server, sender, args[0]);
        String targetName = args[0];
        String pause = args[1];

        if (!pause.equalsIgnoreCase("true") && !pause.equalsIgnoreCase("false")) {
            sender.sendMessage(new TextComponentString("Error: The second argument must be 'true' or 'false'."));
            return;
        }

        for (Entity e : entity) {
            if (e instanceof EntityPlayerMP) {
                PacketHandler.INSTANCE.sendTo(new PausemusicMessage(pause), (EntityPlayerMP) e);
            }
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
        } else if (args.length == 2) {
            return getListOfStringsMatchingLastWord(args, "true", "false");
        }
        return Collections.emptyList();
    }
}