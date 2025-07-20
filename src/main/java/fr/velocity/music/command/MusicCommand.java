package fr.velocity.music.command;

import fr.velocity.music.command.music.*;

public class MusicCommand extends AdvancedCommandBase {

    public MusicCommand() {
        super();
        addSubCommand(new PlayCommand(this));
        addSubCommand(new InfoCommand());
        addSubCommand(new PauseCommand());
        addSubCommand(new StopCommand());
        addSubCommand(new ChangeTimecodeCommand());
        addSubCommand(new VolumeCommand());
    }

    @Override
    public String getName() {
        return "music";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

}