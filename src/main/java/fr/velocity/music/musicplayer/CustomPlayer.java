package fr.velocity.music.musicplayer;

import fr.velocity.music.lavaplayer.api.IMusicPlayer;

public class CustomPlayer {
    private final IMusicPlayer player;
    private int MaxVolume;

    public CustomPlayer(IMusicPlayer player, int volume) {
        this.player = player;
        this.MaxVolume = volume;
    }

    public IMusicPlayer getPlayer() {
        return player;
    }

    public int getMaxVolume() {
        return MaxVolume;
    }

    public void setMaxVolume(int volume) {
        MaxVolume = volume;
    }
}
