package fr.velocity.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import fr.velocity.mod.network.PacketHandler;
import fr.velocity.mod.network.messages.PlayerTrackmusicMessage;
import fr.velocity.mod.network.messages.PositionTrackmusicMessage;
import fr.velocity.mod.network.messages.RegionTrackmusicMessage;
import fr.velocity.mod.network.messages.TrackmusicMessage;
import net.minecraft.command.CommandException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.*;

import static net.minecraft.command.CommandBase.getEntityList;

public class ServerListPersistence {
    private static final String FILE_PATH = "saved_data.json";
    private static final Gson gson = new Gson();
    private static List<List<Object>> mainList = new ArrayList<>();

    public static void AddTrackSaved(long Duration, String url, int volume, String TrackId, String Option, String User) {
        RemoveTrackId(TrackId);

        List<Object> subList1 = new ArrayList<>();

        subList1.add("Track");
        subList1.add(System.currentTimeMillis());
        subList1.add(Duration);
        subList1.add(url);
        subList1.add(volume);
        subList1.add(TrackId);
        subList1.add(Option);
        subList1.add(User);

        mainList.add(subList1);
    }

    public static void saveASave() {
        File original = new File(FILE_PATH);
        if (!original.exists()) {
            System.out.println("Fichier d'origine introuvable : " + FILE_PATH);
            return;
        }

        int counter = 1;
        String baseName = "saved_data_backup";
        String extension = ".json";
        String backupPath;

        do {
            backupPath = baseName + (counter == 1 ? "" : "_" + counter) + extension;
            counter++;
        } while (new File(backupPath).exists());

        try {
            Files.copy(original.toPath(), new File(backupPath).toPath());
            System.out.println("Sauvegarde créée : " + backupPath);
        } catch (IOException e) {
            System.out.println("Erreur lors de la création de la sauvegarde.");
            e.printStackTrace();
        }
    }

    public static void AddPlayerTrackSaved(long Duration, String url, int volume, String TrackId, String Option, String User, int Radius) {
        RemoveTrackId(TrackId);

        List<Object> subList1 = new ArrayList<>();

        subList1.add("PlayerTrack");
        subList1.add(System.currentTimeMillis());
        subList1.add(Duration);
        subList1.add(url);
        subList1.add(volume);
        subList1.add(TrackId);
        subList1.add(Option);
        subList1.add(User);
        subList1.add(Radius);

        mainList.add(subList1);
    }

    public static void AddLocationTrackSaved(long Duration, String url, int volume, String TrackId, String Option, String User, int x, int y, int z, int radius) {
        RemoveTrackId(TrackId);

        List<Object> subList1 = new ArrayList<>();

        subList1.add("LocationTrack");
        subList1.add(System.currentTimeMillis());
        subList1.add(Duration);
        subList1.add(url);
        subList1.add(volume);
        subList1.add(TrackId);
        subList1.add(Option);
        subList1.add(User);
        subList1.add(x);
        subList1.add(y);
        subList1.add(z);
        subList1.add(radius);

        mainList.add(subList1);
    }

    public static void AddRegionTrackSaved(long Duration, String url, int volume, String TrackId, String Option, String User, int x1, int y1, int z1, int x2, int y2, int z2, String region, String world) {
        RemoveTrackId(TrackId);

        List<Object> subList1 = new ArrayList<>();

        subList1.add("RegionTrack");
        subList1.add(System.currentTimeMillis());
        subList1.add(Duration);
        subList1.add(url);
        subList1.add(volume);
        subList1.add(TrackId);
        subList1.add(Option);
        subList1.add(User);
        subList1.add(x1);
        subList1.add(y1);
        subList1.add(z1);
        subList1.add(x2);
        subList1.add(y2);
        subList1.add(z2);
        subList1.add(region);
        subList1.add(world);

        mainList.add(subList1);
    }

    public static void saveData() {
        try (Writer writer = new FileWriter(FILE_PATH)) {
            gson.toJson(mainList, writer);
            System.out.println("Données sauvegardées dans le fichier : " + FILE_PATH);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadData() {
        File file = new File(FILE_PATH);
        if (file.exists()) {
            try (Reader reader = new FileReader(file)) {
                Type listType = new TypeToken<List<List<Object>>>() {}.getType();
                mainList = gson.fromJson(reader, listType);
                System.out.println("Données chargées depuis le fichier : " + mainList);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Aucune donnée trouvée. Création d'une nouvelle liste.");
        }
    }

    public static void RemoveTrackId(String TrackId) {
        if (Objects.equals(TrackId, "ALL")) {
            mainList = new ArrayList<>();
        } else {
            Iterator<List<Object>> iterator = mainList.iterator();
            while (iterator.hasNext()) {
                List<Object> subList = iterator.next();
                String GetTrackId = (String) subList.get(5);
                if (Objects.equals(GetTrackId, TrackId)) {
                    iterator.remove();
                }
            }
        }
    }

    public static double GetDouble(Object o) {
        if (o instanceof Long) {
            return ((Long) o).doubleValue();
        } else if (o instanceof Double) {
            return (Double) o;
        } else if (o instanceof Integer) {
            return (Integer) o;
        } else {
            throw new IllegalArgumentException("Unsupported type: " + o.getClass().getName());
        }
    }

    public static void AskForSavedData(EntityPlayer player) throws CommandException {
        if (player == null) {
            System.out.println("Le joueur est nul, impossible de traiter.");
            return;
        }

        EntityPlayerMP MPPlayer = (EntityPlayerMP) player;

        if(MPPlayer != null) {
            long currentTime = System.currentTimeMillis();
            List<List<Object>> itemsToRemove = new ArrayList<>();

            for (List<Object> subList : mainList) {
                String TrackType = (String) subList.get(0);

                double startTime = GetDouble(subList.get(1));
                double duration = GetDouble(subList.get(2));
                String option = (String) subList.get(6);
                if(!option.contains("--norestartsave")) {
                    if (currentTime - startTime >= duration) {
                        if(!option.contains("--repeat")) {
                            itemsToRemove.add(subList);
                            continue;
                        }
                    }
                } else {
                    itemsToRemove.add(subList);
                    continue;
                }

                String url = (String) subList.get(3);
                double volume = GetDouble(subList.get(4));
                String TrackId = (String) subList.get(5);
                String User = (String) subList.get(7);

                double elapsedTime = (currentTime - startTime);
                double currentVideoTime = elapsedTime % duration;
                if (currentVideoTime < 0) {
                    currentVideoTime += duration;
                }


                if (MPPlayer.getServer() == null) {
                    System.out.println("Le serveur est nul, impossible de récupérer la liste des entités.");
                    continue;
                }

                if (User == null || User.trim().isEmpty()) {
                    System.out.println("L'utilisateur spécifié est vide ou nul.");
                    continue;
                }

                List<Entity> entitylist = Collections.emptyList();
                if(!Objects.equals(TrackType, "PlayerTrack")) {
                    if (User.startsWith("@")) {
                        entitylist = getEntityList(MPPlayer.getServer(), MPPlayer, User);
                    } else {
                        EntityPlayerMP targetPlayer = MPPlayer.getServer().getPlayerList().getPlayerByUsername(User);
                        if (targetPlayer == null) {
                            return;
                        }
                        entitylist = new ArrayList<>();
                        entitylist.add(targetPlayer);
                    }

                    if (entitylist == null || entitylist.isEmpty()) {
                        System.out.println("Aucune entité trouvée pour : " + User);
                        return;
                    }
                }

                option = option + " --position" + currentVideoTime;

                // Jouer les fameux sons
                if(Objects.equals(TrackType, "Track")) {
                    if(entitylist.contains(MPPlayer)) {
                        PacketHandler.INSTANCE.sendTo(new TrackmusicMessage(url, (int) volume, TrackId, option), MPPlayer);
                    }
                } else {
                    if(Objects.equals(TrackType, "LocationTrack")) {
                        if(entitylist.contains(MPPlayer)) {
                            double X = GetDouble(subList.get(8));
                            double Y = GetDouble(subList.get(9));
                            double Z = GetDouble(subList.get(10));
                            double Radius = GetDouble(subList.get(11));

                            PacketHandler.INSTANCE.sendTo(new PositionTrackmusicMessage((int) X, (int) Y, (int) Z, (int) Radius, url, (int) volume, TrackId, option), MPPlayer);
                        }
                    } else {
                        if(Objects.equals(TrackType, "PlayerTrack")) {
                            double Radius = GetDouble(subList.get(8));

                            if(User.equals(player.getName())) {
                                System.out.println("Le joueur est le mec a qui le son est relié cela joue le son donc a tous");
                                List<Entity> allplayer = getEntityList(MPPlayer.getServer(), MPPlayer, "@a");
                                for (Entity e : allplayer) {
                                    if (e instanceof EntityPlayerMP) {
                                        PacketHandler.INSTANCE.sendTo(new PlayerTrackmusicMessage(User, (int) Radius, url, (int) volume, TrackId, option), (EntityPlayerMP) e);
                                    }
                                }
                            }
                            if(User.equals("@a")) {
                                System.out.println("Tout les joueurs font du sons");
                                List<Entity> allplayer = getEntityList(MPPlayer.getServer(), MPPlayer, "@a");
                                for (Entity e : allplayer) {
                                    if (e instanceof EntityPlayerMP) {
                                        PacketHandler.INSTANCE.sendTo(new PlayerTrackmusicMessage(e.getName(), (int) Radius, url, (int) volume, TrackId, option), MPPlayer);
                                    }
                                }
                            } else {
                                System.out.println("Lancer un son jouer sur probablement quelqu'un...");
                                PacketHandler.INSTANCE.sendTo(new PlayerTrackmusicMessage(User, (int) Radius, url, (int) volume, TrackId, option), MPPlayer);
                            }
                        } else {
                            if(Objects.equals(TrackType, "LocationTrack")) {
                                if(entitylist.contains(MPPlayer)) {
                                    double X1 = GetDouble(subList.get(8));
                                    double Y1 = GetDouble(subList.get(9));
                                    double Z1 = GetDouble(subList.get(10));
                                    double X2 = GetDouble(subList.get(11));
                                    double Y2 = GetDouble(subList.get(12));
                                    double Z2 = GetDouble(subList.get(13));
                                    String region = (String) subList.get(14);
                                    String world = (String) subList.get(15);

                                    PacketHandler.INSTANCE.sendTo(new RegionTrackmusicMessage((int) X1, (int) Y1, (int) Z1, (int) X2, (int) Y2, (int) Z2, region, world, url, (int) volume, TrackId, option), MPPlayer);
                                }
                            }
                        }
                    }
                }
            }

            mainList.removeAll(itemsToRemove);
        }
    }
}
