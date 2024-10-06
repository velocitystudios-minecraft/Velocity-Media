package fr.velocity.music.lavaplayer.api.search;

import fr.velocity.music.lavaplayer.api.audio.IAudioTrack;
import fr.velocity.music.lavaplayer.api.audio.IAudioTrackList;

public interface ISearchResult {
	
	String getUri();
	
	boolean isList();
	
	IAudioTrackList getTrackList();
	
	IAudioTrack getTrack();
	
	boolean hasError();
	
	String getErrorMessage();
	
	StackTraceElement[] getStackTrace();
	
}
