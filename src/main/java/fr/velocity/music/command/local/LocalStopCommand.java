package fr.velocity.music.command.local;

import fr.velocity.music.lavaplayer.api.queue.ITrackManager;
import fr.velocity.music.musicplayer.MusicPlayerManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class LocalStopCommand extends CommandBase {

    @Override
    public String getName() {
        return "localstopmusic";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/localstopmusic";
    }


    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        final ITrackManager manager = MusicPlayerManager.getPlayer().getTrackManager();
        manager.stop();
    }
}