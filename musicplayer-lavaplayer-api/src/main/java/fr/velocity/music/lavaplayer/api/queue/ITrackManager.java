package fr.velocity.music.lavaplayer.api.queue;

import fr.velocity.music.lavaplayer.api.audio.IPlayingTrack;

public interface ITrackManager {
	
	void start();
	
	void stop();
	
	void setTrackQueue(ITrackQueue queue);
	
	void skip();
	
	void setPaused(boolean value);
	
	boolean isPaused();
	
	IPlayingTrack getCurrentTrack();
}
