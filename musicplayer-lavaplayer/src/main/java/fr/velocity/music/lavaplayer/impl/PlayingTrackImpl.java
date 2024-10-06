package fr.velocity.music.lavaplayer.impl;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import fr.velocity.music.lavaplayer.api.audio.IAudioTrack;
import fr.velocity.music.lavaplayer.api.audio.IPlayingTrack;

public class PlayingTrackImpl extends AudioTrackImpl implements IPlayingTrack {
	
	public PlayingTrackImpl(AudioTrack track) {
		super(track);
	}
	
	@Override
	public IAudioTrack getOriginalTrack() {
		return track.getUserData(IAudioTrack.class);
	}
	
}
