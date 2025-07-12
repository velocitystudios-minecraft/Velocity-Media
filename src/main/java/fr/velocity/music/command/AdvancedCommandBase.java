package fr.velocity.music.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.*;

public abstract class AdvancedCommandBase extends CommandBase {

    protected AdvancedCommandBase parent;

    protected final Map<String, ISubCommand> subCommands = new HashMap<>();

    public AdvancedCommandBase() {
        this.parent = null;
    }

    public AdvancedCommandBase(AdvancedCommandBase parent) {
        this.parent = parent;
    }

    @Override
    public String getUsage(ICommandSender sender) {
        StringBuilder usage = new StringBuilder();
        subCommands.keySet().forEach(s -> usage.append("|").append(s));
        return "/" + (parent == null ? getName() : parent.getName() + " "+ getName()) + " <"+ usage.substring(1)+"> ";
    }

    protected void addSubCommand(ISubCommand command) {
        subCommands.put(command.getSubName(), command);
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length > 0 && subCommands.containsKey(args[0])) {
            ISubCommand command = subCommands.get(args[0]);
            if(sender.canUseCommand(command.getRequiredPermissionLevel(), command.getSubName())) {
                command.execute(server, sender, Arrays.copyOfRange(args, 1, args.length));
            }
        }
        else {
            throw new WrongUsageException(getUsage(sender));
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        List<String> r = new ArrayList<String>();
        if (args.length == 1) {
            r.addAll(subCommands.keySet());
        }
        else if(args.length > 1 && subCommands.containsKey(args[0])) {
            r.addAll(subCommands.get(args[0]).getTabCompletions(server, sender, Arrays.copyOfRange(args, 1, args.length), targetPos));
        }
        return getListOfStringsMatchingLastWord(args, r);
    }
}