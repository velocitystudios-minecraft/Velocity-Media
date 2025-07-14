package fr.velocity.music.command.music;

import fr.velocity.music.command.AdvancedCommandBase;
import fr.velocity.music.command.ISubCommand;
import fr.velocity.music.command.music.playtrack.PlayerTrackCommand;
import fr.velocity.music.command.music.playtrack.PositionTrackCommand;
import fr.velocity.music.command.music.playtrack.RegionTrackCommand;
import fr.velocity.music.command.music.playtrack.TrackCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.List;

public class PlayCommand extends AdvancedCommandBase implements ISubCommand {

    public PlayCommand(AdvancedCommandBase parent) {
        super(parent);
        addSubCommand(new PlayerTrackCommand());
        addSubCommand(new PositionTrackCommand());
        addSubCommand(new RegionTrackCommand());
        addSubCommand(new TrackCommand());
    }

    @Override
    public String getName() {
        return "play";
    }

    @Override
    public String getSubName() {
        return getName();
    }


    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public int getSubRequiredPermissionLevel() {
        return getRequiredPermissionLevel();
    }


    @Override
    public List<String> getSubTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        return this.getTabCompletions(server, sender, args, targetPos);
    }

    @Override
    public void subExecute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        this.execute(server, sender, args);
    }

}