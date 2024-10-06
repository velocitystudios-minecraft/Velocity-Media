package fr.velocity.music.lavaplayer.search;

import java.util.function.Consumer;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import fr.velocity.music.lavaplayer.api.search.ISearchResult;
import fr.velocity.music.lavaplayer.api.search.ITrackSearch;
import fr.velocity.music.lavaplayer.impl.AudioTrackImpl;
import fr.velocity.music.lavaplayer.impl.AudioTrackListImpl;

public class TrackSearch implements ITrackSearch {
	
	private final AudioPlayerManager audioPlayerManager;
	
	public TrackSearch(AudioPlayerManager audioplayermanager) {
		audioPlayerManager = audioplayermanager;
	}
	
	@Override
	public void getTracks(String uri, Consumer<ISearchResult> consumer) {
		audioPlayerManager.loadItem(uri, new AudioLoadResultHandler() {
			
			@Override
			public void trackLoaded(AudioTrack track) {
				consumer.accept(new SearchResult(uri, new AudioTrackImpl(track)));
			}
			
			@Override
			public void playlistLoaded(AudioPlaylist playlist) {
				consumer.accept(new SearchResult(uri, new AudioTrackListImpl(uri, playlist)));
			}
			
			@Override
			public void noMatches() {
				consumer.accept(new SearchResult(uri, new RuntimeException("No matches found")));
			}
			
			@Override
			public void loadFailed(FriendlyException exception) {
				consumer.accept(new SearchResult(uri, exception));
			}
		});
	}
}
