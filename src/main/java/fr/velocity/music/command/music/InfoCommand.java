package fr.velocity.music.command.music;

import fr.velocity.mod.network.PacketHandler;
import fr.velocity.mod.network.messages.S2CMessageInfoMusic;
import fr.velocity.mod.network.messages.S2CMessagePauseMusic;
import fr.velocity.mod.network.messages.S2CMessagePlayerTrackMusic;
import fr.velocity.music.command.ISubCommand;
import fr.velocity.music.musicplayer.CustomPlayer;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.HoverEvent;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static fr.velocity.music.musicplayer.MusicPlayerManager.playerCache;

public class InfoCommand implements ISubCommand {

    @Override
    public String getSubName() {
        return "info";
    }

    @Override
    public int getSubRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/music info";
    }

    @Override
    public void subExecute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

        PacketHandler.INSTANCE.sendTo(new S2CMessageInfoMusic(), (EntityPlayerMP) sender);
    }
}