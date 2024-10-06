package fr.velocity.music.lavaplayer.queue;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import fr.velocity.music.lavaplayer.api.audio.IAudioTrack;
import fr.velocity.music.lavaplayer.api.queue.ITrackQueue;
import fr.velocity.music.lavaplayer.impl.AudioTrackImpl;

public class TrackQueueWrapper {
	
	private final ITrackQueue queue;
	
	public TrackQueueWrapper(ITrackQueue queue) {
		this.queue = queue;
	}
	
	public boolean calculateNext() {
		return queue.calculateNext();
	}
	
	public AudioTrack getNext() {
		final IAudioTrack track = queue.getNext();
		if (track == null) {
			return null;
		} else {
			final AudioTrack audiotrack = ((AudioTrackImpl) track).getImplTrack().makeClone(); // We know this cast must work because this interface is only implemented by AudioTrackImpl. Still not the best.
			audiotrack.setUserData(track);
			return audiotrack;
		}
	}
	
}
