package fr.velocity.music.musicplayer;

import fr.velocity.music.lavaplayer.api.IMusicPlayer;

public class CustomPlayer {
    private final IMusicPlayer player;
    private String id;
    private int MaxVolume = 0;
    private final String Mode;
    private int x;
    private int y;
    private int z;
    private int radius;
    private String Option;
    private String Player;

    public CustomPlayer(IMusicPlayer player, String id, int volume, String mode, int x, int y, int z, int radius, String Option, String Player) {
        this.player = player;
        this.id = id;
        this.MaxVolume = volume;
        this.Mode = mode;
        this.x = x;
        this.y = y;
        this.z = z;
        this.radius = radius;
        this.Option = Option;
        this.Player = Player;
    }

    public IMusicPlayer getPlayer() {
        return player;
    }

    public int getMaxVolume() {
        return MaxVolume;
    }

    public int getX() { return x; }

    public int getY() { return y; }

    public int getZ() { return z; }

    public int getRadius() { return radius; }

    public String getMode() { return Mode; }

    public String getId() { return id; }

    public void setMaxVolume(int volume) {
        MaxVolume = volume;
    }

    public void setRadius(int Radius) {
        radius = Radius;
    }
}
