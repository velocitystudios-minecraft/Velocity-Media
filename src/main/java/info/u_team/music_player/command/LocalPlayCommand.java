package info.u_team.music_player.command;

import info.u_team.music_player.lavaplayer.api.audio.IAudioTrack;
import info.u_team.music_player.lavaplayer.api.queue.ITrackManager;
import info.u_team.music_player.musicplayer.MusicPlayerManager;
import info.u_team.music_player.musicplayer.playlist.LoadedTracks;
import info.u_team.music_player.musicplayer.playlist.Playlist;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import org.apache.commons.lang3.tuple.Pair;

public class LocalPlayCommand extends CommandBase {
    @Override
    public String getName() {
        return "localmusic";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/localmusic <volume> <url>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 1) {
            /*sender.sendMessage(new TextComponentString(TextFormatting.RED + "Veuillez fournir un volume."));
            sender.sendMessage(new TextComponentString(TextFormatting.RED + "Usage : /playmusic <volume> <url>"));*/
            return;
        } else if (args.length < 2) {
            /*sender.sendMessage(new TextComponentString(TextFormatting.RED + "Veuillez fournir une URL valide."));
            sender.sendMessage(new TextComponentString(TextFormatting.RED + "Usage : /playmusic <volume> <url>"));*/
            return;
        }

        Playlist playlist = new Playlist();
        MusicPlayerManager.getPlayer().getTrackSearch().getTracks(args[1], result -> {
            if (result.hasError()) {
                sender.sendMessage(new TextComponentString(result.getErrorMessage()));
            } else {
                final IAudioTrack track = result.getTrack();

                final Runnable runnable = () -> {
                    final ITrackManager manager = MusicPlayerManager.getPlayer().getTrackManager();
                    playlist.add(track);

                    Pair<LoadedTracks, IAudioTrack> pair = playlist.getFirstTrack();
                    playlist.setPlayable(pair.getLeft(), pair.getRight());
                    manager.setTrackQueue(playlist);
                    manager.start();
                    MusicPlayerManager.getPlayer().setVolume(Integer.parseInt(args[1]));
                };

                if (!playlist.isLoaded()) {
                    playlist.load(runnable);
                } else {
                    runnable.run();
                }
            }
        });


        //sender.sendMessage(new TextComponentString("Lecture de la musique à partir de l'URL"));
    }
}