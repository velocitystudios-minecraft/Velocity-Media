package fr.velocity.music.musicplayer;

import fr.velocity.music.lavaplayer.api.audio.IAudioTrack;
import fr.velocity.music.lavaplayer.api.queue.ITrackManager;
import fr.velocity.music.musicplayer.playlist.Playlist;
import fr.velocity.music.musicplayer.playlist.Skip;
import fr.velocity.music.musicplayer.playlist.*;

public final class MusicPlayerUtils {
	
	public static void skipForward() {
		final ITrackManager manager = MusicPlayerManager.getPlayer().getTrackManager();
		final Playlist playlist = MusicPlayerManager.getPlaylistManager().getPlaylists().getPlaying();
		if (playlist != null) {
			if (playlist.skip(Skip.FORWARD)) {
				manager.skip();
			}
		}
	}
	
	public static void skipBack() {
		final ITrackManager manager = MusicPlayerManager.getPlayer().getTrackManager();
		final Playlist playlist = MusicPlayerManager.getPlaylistManager().getPlaylists().getPlaying();
		if (playlist != null) {
			final IAudioTrack currentlyPlaying = manager.getCurrentTrack();
			
			if (currentlyPlaying == null) {
				return;
			}
			
			long maxDuration = currentlyPlaying.getDuration() / 10;
			if (maxDuration > 10000) {
				maxDuration = 10000;
			}
			if (currentlyPlaying.getPosition() > maxDuration && !currentlyPlaying.getInfo().isStream()) {
				currentlyPlaying.setPosition(0);
			} else {
				
				if (playlist.skip(Skip.PREVIOUS)) {
					manager.skip();
				}
			}
		}
	}
}
