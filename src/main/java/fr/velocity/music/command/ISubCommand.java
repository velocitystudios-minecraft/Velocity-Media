package fr.velocity.music.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public interface ISubCommand {

    String getName();

    String getUsage(ICommandSender sender);

    int getRequiredPermissionLevel();

    void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException;

    default List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        return Collections.emptyList();
    }

}
