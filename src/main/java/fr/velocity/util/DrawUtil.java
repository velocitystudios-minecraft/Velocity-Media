package fr.velocity.util;

public class DrawUtil {

    public static double lerp(double start, double end, float t) {
        return start + (end - start) * t;
    }
    public static float lerp(float start, float end, float t) {
        return start + (end - start) * t;
    }

}
