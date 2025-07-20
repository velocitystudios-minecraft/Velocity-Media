package fr.velocity.music.command.music;

import fr.velocity.mod.network.PacketHandler;
import fr.velocity.mod.network.messages.S2CMessagePauseMusic;
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
        boolean Found = false;
        for (Map.Entry<String, CustomPlayer> entry : playerCache.entrySet()) {
            if(entry.getValue().getPlayer().getVolume() > 0) {
                Found = true;

                TextComponentString GetText = new TextComponentString("§7- §a" + entry.getKey());
                Style style = new Style();
                String hoverText = "§7- CV : §a" + entry.getValue().getPlayer().getVolume();
                hoverText = hoverText + "§7- MV : §a" + entry.getValue().getMaxVolume();
                hoverText = hoverText + " §7- Mode : §a" + entry.getValue().getMode();
                hoverText = hoverText + " §7- Radius : §a" + entry.getValue().getRadius();
                hoverText = hoverText + " §7- Name : §a" + entry.getValue().getPlayer().getTrackManager().getCurrentTrack().getInfo().getFixedTitle();
                style.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(hoverText)));
                GetText.setStyle(style);
                sender.sendMessage(GetText);
            }
        }
        if(!Found) {
            sender.sendMessage(new TextComponentString("§cAucun son actuellement joué ou votre volume est a 0."));
        }
    }
}