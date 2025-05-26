package fr.velocity.mod.handler;

import net.minecraftforge.common.config.Configuration;
import java.io.File;

public class ConfigHandler {
    private static Configuration config;
    private static final String CATEGORY_VOLUME = "volume";
    private static final String CONFIG_FILE_PATH = "config/velocitymedia/config.cfg";

    public static float VolumeGlobaux = 1.0F;

    public static void init() {
        if (config == null) {
            config = new Configuration(new File(CONFIG_FILE_PATH));
            loadConfig();
        }
    }

    private static void loadConfig() {
        VolumeGlobaux = config.getFloat("VolumeGlobaux", CATEGORY_VOLUME, VolumeGlobaux, 0.0F, 2.0F, "Volume Globaux");
        if (config.hasChanged()) {
            config.save();
        }
    }

    public static void saveConfig() {
        config.get(CATEGORY_VOLUME, "VolumeGlobaux", VolumeGlobaux).set(VolumeGlobaux);
        if (config.hasChanged()) {
            config.save();
        }
    }
}

