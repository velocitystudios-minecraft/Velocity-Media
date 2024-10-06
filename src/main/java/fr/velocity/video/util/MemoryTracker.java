package fr.velocity.video.util;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;

@SideOnly(Side.CLIENT)
public class MemoryTracker {
   public static ByteBuffer create(int pSize) {
      return BufferUtils.createByteBuffer(pSize);
   }
}