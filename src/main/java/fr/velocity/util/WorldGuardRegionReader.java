package fr.velocity.util;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;

public class WorldGuardRegionReader {

    public static class RegionBounds {
        public double minX, minY, minZ;
        public double maxX, maxY, maxZ;

        public RegionBounds(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
            this.minX = minX;
            this.minY = minY;
            this.minZ = minZ;
            this.maxX = maxX;
            this.maxY = maxY;
            this.maxZ = maxZ;
        }

        @Override
        public String toString() {
            return "Min: (" + minX + ", " + minY + ", " + minZ + ") | Max: (" + maxX + ", " + maxY + ", " + maxZ + ")";
        }
    }


    public static RegionBounds readRegionBounds(String world, String regionName) throws Exception {
        File file = new File("plugins/WorldGuard/worlds/" + world + "/regions.yml");
        if (!file.exists()) {
            System.out.println("Fichier regions.yml introuvable à l'emplacement : " + file.getAbsolutePath());
            return null;
        }


        try (FileInputStream input = new FileInputStream(file)) {
            Yaml yaml = new Yaml();
            Map<String, Object> data = yaml.load(input);

            Map<String, Object> regions = (Map<String, Object>) data.get("regions");
            if (!regions.containsKey(regionName)) {
                System.out.println("La région '" + regionName + "' n'existe pas.");
                return null;
            }

            if (regionName.equals("__global__")) {
                System.out.println("La région __global__ n'a pas de coordonnées.");
                return null;
            }

            Map<String, Object> region = (Map<String, Object>) regions.get(regionName);
            Map<String, Object> min = (Map<String, Object>) region.get("min");
            Map<String, Object> max = (Map<String, Object>) region.get("max");

            double minX = ((Number) min.get("x")).doubleValue();
            double minY = ((Number) min.get("y")).doubleValue();
            double minZ = ((Number) min.get("z")).doubleValue();

            double maxX = ((Number) max.get("x")).doubleValue();
            double maxY = ((Number) max.get("y")).doubleValue();
            double maxZ = ((Number) max.get("z")).doubleValue();

            return new RegionBounds(minX, minY, minZ, maxX, maxY, maxZ);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
