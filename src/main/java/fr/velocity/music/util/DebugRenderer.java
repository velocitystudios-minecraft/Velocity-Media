package fr.velocity.music.util;

import fr.velocity.music.musicplayer.CustomPlayer;
import fr.velocity.util.DrawUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import static fr.velocity.music.musicplayer.MusicPlayerManager.playerCache;

public class DebugRenderer {

    public static DebugRenderer INSTANCE;

    public static final List<DebugZone> zones = new CopyOnWriteArrayList<>();
    private static boolean active = false;

    private static class DebugZone {
        BlockPos pos;
        int radius;
        String name;
        String mode;
        String Option;
        String Player;
        String Mode;
        BlockPos pos2;
        String region;
        String world;
        String methode;
        int DimensionId;

        DebugZone(BlockPos pos, int radius, String name, String mode, String Option, String Player, BlockPos pos2, String region, String world, String methode, int DimensionId) {
            this.pos = pos;
            this.radius = radius;
            this.name = name;
            this.mode = mode;
            this.Option = Option;
            this.Player = Player;
            this.pos2 = pos2;
            this.world = world;
            this.region = region;
            this.methode = methode;
            this.DimensionId = DimensionId;
        }


    }

    public static void init() {
        INSTANCE = new DebugRenderer();
    }

    public void enableDebug() {
        active = true;
    }

    public void disableDebug() {
        active = false;
    }

    public void addZone(BlockPos pos, int radius, String name, String mode, String Option, String Player) {
        zones.removeIf(zone ->
                zone.name.equals(name));

        zones.add(new DebugZone(pos, radius, name, mode, Option, Player, new BlockPos(0,0,0),"None","None","Sphere", 0));
        System.out.println("AJOUT D UN DEBUG ZONE " + name);
    }

    public void addRegionZone(int x1, int y1, int z1, int x2, int y2, int z2, String region, String world, String name, String mode, String Option, String Player, int DimensionId) {
        zones.removeIf(zone ->
                zone.name.equals(name));

        zones.add(new DebugZone(new BlockPos(x1, y1, z1), 0, name, mode, Option, Player, new BlockPos(x2,y2,z2), region, world, "Squar", DimensionId));
        System.out.println("AJOUT D UN DEBUG ZONE " + name);
    }

    public static Entity getEntityByUUID(World world, UUID uuid) {
        for (Entity entity : world.loadedEntityList) {
            if (entity.getUniqueID().equals(uuid)) {
                return entity;
            }
        }
        return null;
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if (!active) return;
        if (zones.isEmpty()) return;

        Minecraft mc = Minecraft.getMinecraft();
        double camX = mc.getRenderManager().viewerPosX;
        double camY = mc.getRenderManager().viewerPosY;
        double camZ = mc.getRenderManager().viewerPosZ;

        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.enableDepth();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GL11.glLineWidth(1.5F);

        GL11.glTranslated(-camX, -camY, -camZ);

        Iterator<DebugZone> iterator = zones.iterator();
        while (iterator.hasNext()) {
            DebugZone zone = iterator.next();

            CustomPlayer GetSuppl = playerCache.get(zone.name);
            String ToShow = "No mode found";

            double cx, cy, cz;

            if(Objects.equals(zone.mode, "PlayerTrack")) {
                // Gestion du mode PlayerTrack (inchangé)
                Entity entity = zone.Option.contains("--useuuid")
                    ? getEntityByUUID(Minecraft.getMinecraft().world, UUID.fromString(zone.Player))
                    : Minecraft.getMinecraft().world.getPlayerEntityByName(zone.Player);
                if(entity==null) continue;
                cx = DrawUtil.lerp(entity.lastTickPosX, entity.posX, event.getPartialTicks());
                cy = DrawUtil.lerp(entity.lastTickPosY, entity.posY, event.getPartialTicks()) + 4;
                cz = DrawUtil.lerp(entity.lastTickPosZ, entity.posZ, event.getPartialTicks());
                ToShow = "Sound link to : " + zone.Player;

                GlStateManager.pushMatrix();
                GlStateManager.translate(cx, cy, cz);
                drawWireSphere(zone.radius, 32, 32);
                GlStateManager.popMatrix();

            } else if(Objects.equals(zone.mode, "RegionTrack") || Objects.equals(zone.mode, "Square")) {
                if(!(Minecraft.getMinecraft().player.dimension == zone.DimensionId)) {
                    continue;
                }
                // Gestion du mode RegionTrack/Square
                BlockPos pos1 = zone.pos;
                BlockPos pos2 = zone.pos2;

                // Calcul du centre de la zone pour le texte
                cx = (pos1.getX() + pos2.getX()) / 2.0 + 0.5;
                cy = (pos1.getY() + pos2.getY()) / 2.0 + 0.5;
                cz = (pos1.getZ() + pos2.getZ()) / 2.0 + 0.5;

                ToShow = "Region from: " + pos1.getX() + "," + pos1.getY() + "," + pos1.getZ() +
                        " to " + pos2.getX() + "," + pos2.getY() + "," + pos2.getZ();

                // Dessin du cube/wireframe
                drawWireframeCube(pos1, pos2, 0, 0, 0);

            } else {
                // Mode PositionTrack par défaut
                cx = zone.pos.getX() + 0.5;
                cy = zone.pos.getY() + 0.5;
                cz = zone.pos.getZ() + 0.5;
                ToShow = "Position link to : x: " + zone.pos.getX() + ", y: " + zone.pos.getY() + ", z: " + zone.pos.getZ();

                GlStateManager.pushMatrix();
                GlStateManager.translate(cx, cy, cz);
                drawWireSphere(zone.radius, 32, 32);
                GlStateManager.popMatrix();
            }

            // Affichage du texte (inchangé)
            drawName(zone.name, cx + 0.5, cy + 0.5, cz + 0.5, 0.09F);
            drawName(TextFormatting.GRAY + ToShow, cx + 0.5, cy - 0.7, cz + 0.5, 0.05F);

            if (GetSuppl.getPlayer().getTrackManager().getCurrentTrack() != null) {
                long MaxTime = GetSuppl.getPlayer().getTrackManager().getCurrentTrack().getOriginalTrack().getDuration();
                long CurrentTime = GetSuppl.getPlayer().getTrackManager().getCurrentTrack().getPosition();
                long totalMaxSeconds = MaxTime / 1000;
                long Maxminutes = totalMaxSeconds / 60;
                long Maxseconds = totalMaxSeconds % 60;
                String formattedMax = String.format("%d:%02d", Maxminutes, Maxseconds);
                long totalCurrentSeconds = CurrentTime / 1000;
                long Currentminutes = totalCurrentSeconds / 60;
                long Currentseconds = totalCurrentSeconds % 60;
                String formattedCurrent = String.format("%d:%02d", Currentminutes, Currentseconds);

                drawName(GetSuppl.getPlayer().getTrackManager().getCurrentTrack().getOriginalTrack().getInfo().getFixedTitle(), cx + 0.5, cy - 1.5, cz + 0.5, 0.04F);
                drawName("C V: " + GetSuppl.getPlayer().getVolume() + " | Id: " + GetSuppl.getPlayer().getTrackManager().getCurrentTrack().getOriginalTrack().getInfo().getIdentifier(), cx + 0.5, cy - 2.3, cz + 0.5, 0.06F);
                if(Objects.equals(GetSuppl.getMode(), "RegionTrack")) {
                    drawName("M V: " + GetSuppl.getMaxVolume() + " | Region: " + zone.region + " | Monde: " + zone.world, cx + 0.5, cy - 3, cz + 0.5, 0.06F);
                } else {
                    drawName("M V: " + GetSuppl.getMaxVolume() + " | Radius: " + GetSuppl.getRadius(), cx + 0.5, cy - 3, cz + 0.5, 0.06F);
                }
                drawName(formattedCurrent + " / " + formattedMax, cx + 0.5, cy - 4, cz + 0.5, 0.05F);
            } else {
                drawName("No Current Playing", cx + 0.5, cy - 2.3, cz + 0.5, 0.05F);
            }
        }

        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }

    // Nouvelle méthode pour dessiner un cube/wireframe
    private void drawWireframeCube(BlockPos pos1, BlockPos pos2, double camX, double camY, double camZ) {
        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.color(1.0F, 0.3F, 0.3F, 0.6F);
        GL11.glLineWidth(1.5F);

        double minX = Math.min(pos1.getX(), pos2.getX()) - camX;
        double minY = Math.min(pos1.getY(), pos2.getY()) - camY;
        double minZ = Math.min(pos1.getZ(), pos2.getZ()) - camZ;
        double maxX = Math.max(pos1.getX(), pos2.getX()) + 1 - camX;
        double maxY = Math.max(pos1.getY(), pos2.getY()) + 1 - camY;
        double maxZ = Math.max(pos1.getZ(), pos2.getZ()) + 1 - camZ;

        GL11.glBegin(GL11.GL_LINE_STRIP);
        // Bas
        GL11.glVertex3d(minX, minY, minZ);
        GL11.glVertex3d(maxX, minY, minZ);
        GL11.glVertex3d(maxX, minY, maxZ);
        GL11.glVertex3d(minX, minY, maxZ);
        GL11.glVertex3d(minX, minY, minZ);
        GL11.glEnd();

        GL11.glBegin(GL11.GL_LINE_STRIP);
        // Haut
        GL11.glVertex3d(minX, maxY, minZ);
        GL11.glVertex3d(maxX, maxY, minZ);
        GL11.glVertex3d(maxX, maxY, maxZ);
        GL11.glVertex3d(minX, maxY, maxZ);
        GL11.glVertex3d(minX, maxY, minZ);
        GL11.glEnd();

        GL11.glBegin(GL11.GL_LINES);
        // Côtés
        GL11.glVertex3d(minX, minY, minZ);
        GL11.glVertex3d(minX, maxY, minZ);

        GL11.glVertex3d(maxX, minY, minZ);
        GL11.glVertex3d(maxX, maxY, minZ);

        GL11.glVertex3d(maxX, minY, maxZ);
        GL11.glVertex3d(maxX, maxY, maxZ);

        GL11.glVertex3d(minX, minY, maxZ);
        GL11.glVertex3d(minX, maxY, maxZ);
        GL11.glEnd();

        GlStateManager.popMatrix();
    }

    private void drawWireSphere(float radius, int stacks, int slices) {
        GlStateManager.pushMatrix();
        GlStateManager.color(1.0F, 0.3F, 0.3F, 0.6F);
        GL11.glLineWidth(1.5F);

        for (int i = 0; i <= stacks; ++i) {
            double lat0 = Math.PI * (-0.5 + (double)(i - 1) / stacks);
            double z0 = Math.sin(lat0);
            double zr0 = Math.cos(lat0);

            double lat1 = Math.PI * (-0.5 + (double)i / stacks);
            double z1 = Math.sin(lat1);
            double zr1 = Math.cos(lat1);

            GL11.glBegin(GL11.GL_LINE_STRIP);
            for (int j = 0; j <= slices; ++j) {
                double lng = 2 * Math.PI * (double)(j - 1) / slices;
                double x = Math.cos(lng);
                double y = Math.sin(lng);

                GL11.glVertex3d(radius * x * zr0, radius * y * zr0, radius * z0);
                GL11.glVertex3d(radius * x * zr1, radius * y * zr1, radius * z1);
            }
            GL11.glEnd();
        }

        GlStateManager.popMatrix();
    }

    private void drawName(String text, double x, double y, double z, float scale) {
        Minecraft mc = Minecraft.getMinecraft();

        float maxDistance = 40.0F;
        double distance = mc.player.getDistance(x, y, z);

        if(distance > maxDistance) {
            return; //TODO: Zoom ?
        }

        GlStateManager.pushMatrix();

        GlStateManager.enableTexture2D();
        mc.entityRenderer.enableLightmap();
        GlStateManager.depthFunc(519);
        GlStateManager.disableLighting();
        GlStateManager.disableColorMaterial();
        GlStateManager.disableDepth();
        RenderHelper.disableStandardItemLighting();

        GlStateManager.translate(x, y, z);

        GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(-scale, -scale, scale);

        int i = 15728880;
        int j = i % 65536;
        int k = i / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j, k);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        int width = mc.fontRenderer.getStringWidth(text) / 2;

        mc.fontRenderer.drawStringWithShadow(text, -width, 0, 0xFFFFFF);

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.enableLighting();
        GlStateManager.enableColorMaterial();
        GlStateManager.depthFunc(515);
        GlStateManager.enableDepth();
        mc.entityRenderer.disableLightmap();
        GlStateManager.disableTexture2D();
        GlStateManager.popMatrix();
    }
}
