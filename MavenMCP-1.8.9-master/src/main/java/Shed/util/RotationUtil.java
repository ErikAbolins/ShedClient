package Shed.util;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

// This is your separate helper class, like a static utility in C#
public class RotationUtil {

    private static final Minecraft mc = Minecraft.getMinecraft();

    // The heart of silent aim: calculating the exact Yaw and Pitch
    public static float[] getRotations(Entity entity) {
        if (mc.thePlayer == null || entity == null) {
            return new float[]{0F, 0F};
        }

        // Target's position (adjusting for the center of the entity's body)
        double x = entity.posX - mc.thePlayer.posX;
        // The vertical axis is the one that matters most for hitting the body,
        // so we adjust for the height of the head/body.
        double y = entity.posY + (double)entity.getEyeHeight() - (mc.thePlayer.posY + (double)mc.thePlayer.getEyeHeight());
        double z = entity.posZ - mc.thePlayer.posZ;

        // Calculate distance on the horizontal plane
        double distance = Math.sqrt(x * x + z * z);

        // Yaw: Rotation around the Y-axis (left/right)
        float yaw = (float) (Math.atan2(z, x) * 180.0D / Math.PI) - 90.0F;

        // Pitch: Rotation up/down
        float pitch = (float) -(Math.atan2(y, distance) * 180.0D / Math.PI);

        // Normalize the angles to make them server-friendly (-180 to 180)
        float currentYaw = mc.thePlayer.rotationYaw;
        float currentPitch = mc.thePlayer.rotationPitch;

        // This makes the rotation "smoother" and less suspicious
        // by clamping the difference to a maximum turning speed.
        // It's basic velocity clamping, just for rotation.
        float finalYaw = currentYaw + MathHelper.wrapAngleTo180_float(yaw - currentYaw);
        float finalPitch = currentPitch + MathHelper.wrapAngleTo180_float(pitch - currentPitch);

        // Clamping the pitch to prevent looking too far up/down (valid Minecraft angles are -90 to 90)
        finalPitch = MathHelper.clamp_float(finalPitch, -90F, 90F);

        return new float[]{finalYaw, finalPitch};
    }
}