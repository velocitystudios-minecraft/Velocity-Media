package info.u_team.music_player.command;

import info.u_team.music_player.lavaplayer.api.queue.ITrackManager;
import info.u_team.music_player.musicplayer.MusicPlayerManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.util.Objects;

public class LocalPauseCommand extends CommandBase {

    @Override
    public String getName() {
        return "localpausemusic";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/localpausemusic";
    }


    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 1) {
            sender.sendMessage(new TextComponentString(TextFormatting.RED + "Veuillez fournir un status."));
            sender.sendMessage(new TextComponentString(TextFormatting.RED + "Usage : /localpausemusic <true/false>"));
            return;
        }

        final ITrackManager manager = MusicPlayerManager.getPlayer().getTrackManager();
        if (Objects.equals(args[0], "true")) {
            if (!manager.isPaused()) {
                manager.setPaused(true);
            }
        } else {
            if (manager.isPaused()) {
                manager.setPaused(false);
            }
        }
    }
}