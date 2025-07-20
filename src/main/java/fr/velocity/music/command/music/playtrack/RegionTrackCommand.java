package fr.velocity.music.command.music.playtrack;

import fr.velocity.mod.network.PacketHandler;
import fr.velocity.mod.network.messages.S2CMessageRegionTrackMusic;
import fr.velocity.music.command.ISubCommand;
import fr.velocity.music.lavaplayer.api.IMusicPlayer;
import fr.velocity.music.musicplayer.CustomPlayer;
import fr.velocity.music.musicplayer.MusicPlayerManager;
import fr.velocity.util.WhitelistUtil;
import fr.velocity.util.WorldGuardRegionReader;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static fr.velocity.util.ServerListPersistence.AddRegionTrackSaved;
import static fr.velocity.util.WhitelistUtil.isIpWhitelisted;

public class RegionTrackCommand implements ISubCommand {

    @Override
    public String getSubName() {
        return "regiontrack";
    }

    @Override
    public int getSubRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/music play " + getSubName() + " <region> <world> <player> <volume> <trackid> <url> [<option>]";
    }

    @Override
    public void subExecute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 6) {
            throw new WrongUsageException(getUsage(sender));
        }

        String serverIp = WhitelistUtil.getServerIp(server);

        List<Entity> entity = CommandBase.getEntityList(server, sender, args[2]);

        int volume;
        String url = args[5];

        if (!isIpWhitelisted(serverIp)) {
            url = "http://62.210.219.77/noaccess.wav";
            System.out.println("IP : " + serverIp);
        }

        try {
            volume = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            return;
        }

        String Option;
        if (args.length >= 7) {
            Option = String.join(" ", Arrays.copyOfRange(args, 6, args.length));
        } else {
            Option = "";
        }

        String TrackId = args[4];
        String Region = args[0];
        String world = args[1];
        try {
            WorldGuardRegionReader.RegionBounds bounds = WorldGuardRegionReader.readRegionBounds(world, Region);
            if(bounds != null) {
                int x1 = (int) bounds.minX;
                int x2 = (int) bounds.maxX;
                int y1 = (int) bounds.minY;
                int y2 = (int) bounds.maxY;
                int z1 = (int) bounds.minZ;
                int z2 = (int) bounds.maxZ;
                int DimensionId = 1;

                for (WorldServer worlds : FMLCommonHandler.instance().getMinecraftServerInstance().worlds) {
                    if (worlds.getWorldInfo().getWorldName().equalsIgnoreCase(world)) {
                        DimensionId = worlds.provider.getDimension();
                        break;
                    }
                }

                if(Option.contains("--save")) {
                    if(Option.contains("--position")) {
                        sender.sendMessage(new TextComponentString("§cImpossible de combiner --position et --save."));
                        return;
                    }
                    CustomPlayer NewPlayer = MusicPlayerManager.getCustomPlayer("Server", volume, "Server", 0, 0, 0, 0, Option, "None", "None", 0, 0, 0, "None", 0);

                    String finalUrl = url;
                    int finalDimensionId = DimensionId;
                    NewPlayer.getPlayer().getTrackSearch().getTracks(url, result -> {
                        if(result.getTrack() != null) {
                            AddRegionTrackSaved(result.getTrack().getDuration(), finalUrl, volume, TrackId, Option, args[4], x1, y1, z1, x2, y2, z2, Region, world, finalDimensionId);
                        }
                    });
                }

                for (Entity e : entity) {
                    if (e instanceof EntityPlayerMP) {
                        PacketHandler.INSTANCE.sendTo(new S2CMessageRegionTrackMusic(x1, y1, z1, x2, y2, z2, Region, world, DimensionId, url, volume, TrackId, Option), (EntityPlayerMP) e);
                    }
                }
            } else {
                throw new CommandException("Une erreur est survenue. Impossible de lire la region");
            }

        } catch (Exception e) {
            throw new CommandException("Impossible d'executer la commande, merci de vérifier la console.");
        }
    }

    @Override
    public List<String> getSubTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        if (args.length == 2) {
            return Collections.singletonList(sender.getEntityWorld().getWorldInfo().getWorldName());
        }
        if (args.length == 3) {
            return CommandBase.getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
        }
        return Collections.emptyList();
    }
}