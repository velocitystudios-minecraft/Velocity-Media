package fr.velocity.video.command;

import fr.velocity.video.network.PacketHandler;
import fr.velocity.video.network.messages.SendVideoMessage;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nullable;
import java.lang.reflect.Array;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PlayVideoCommand extends CommandBase {


    @Override
    public String getName() {
        return "playvideo";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
        }

        if (args.length == 2 || args.length == 3) {
            return new ArrayList<>();
        }

        if (args.length == 4) {
            return getListOfStringsMatchingLastWord(args, "true", "false");
        }

        return null;
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/playvideo <target> <volume> <url> [<control_blocked>] [<position>] [<speed>]";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 3) {
            sender.sendMessage(new TextComponentString("Invalid command. The format is: /playvideo <target> <volume> <url> [<control_blocked>] [<position>] [<speed>]"));
            return;
        }

        List<Entity> entity = getEntityList(server, sender, args[0]);

        int volume = parseInt(args[1]);
        if (volume < 0 || volume > 100) {
            sender.sendMessage(new TextComponentString("Invalid volume, only between 0 and 100"));
        }

        String url = args[2];

        boolean controlBlocked = false;
        if (args.length >= 4) {
            controlBlocked = parseBoolean(args[3]);
        }

        int TimePosition = 0;
        if (args.length >= 5) {
            TimePosition = parseInt(args[4]);
        }

        float VideoSpeed = 1;
        if (args.length >= 6) {
            VideoSpeed = Float.parseFloat(args[5]);
            if (VideoSpeed < 0) {
                sender.sendMessage(new TextComponentString("Invalid speed, only between 0 and inf"));
            }
        }

        System.out.println("Speed: " + VideoSpeed + " Position: " + TimePosition);

        for (Entity e : entity) {
            if (e instanceof EntityPlayerMP) {
                PacketHandler.INSTANCE.sendTo(new SendVideoMessage(url, volume, controlBlocked, TimePosition, VideoSpeed), (EntityPlayerMP) e);
            }
        }
    }
}
