package fr.velocity.music.lavaplayer.api;

import javax.sound.sampled.DataLine;

import fr.velocity.music.lavaplayer.api.output.IOutputConsumer;
import fr.velocity.music.lavaplayer.api.queue.ITrackManager;
import fr.velocity.music.lavaplayer.api.search.ITrackSearch;

public interface IMusicPlayer {
	
	ITrackManager getTrackManager();
	
	ITrackSearch getTrackSearch();
	
	void startAudioOutput();
	
	void setMixer(String name);
	
	String getMixer();
	
	DataLine.Info getSpeakerInfo();
	
	int getVolume();
	
	void setVolume(int volume);
	
	void setOutputConsumer(IOutputConsumer consumer);
}
