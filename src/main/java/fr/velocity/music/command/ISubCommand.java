package fr.velocity.music.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public interface ISubCommand {

    String getSubName();

    String getUsage(ICommandSender sender);

    int getSubRequiredPermissionLevel();

    void subExecute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException;

    default List<String> getSubTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        return Collections.emptyList();
    }

}
