package Shed.util;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

import java.util.List;

public class ReachUtil {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static MovingObjectPosition getMouseOver(double distance, double expand, float partialTicks) {
        if (mc.getRenderViewEntity() == null || mc.theWorld == null) {
            return null;
        }

        Entity entity = null;
        Vec3 eyePos = mc.getRenderViewEntity().getPositionEyes(partialTicks);
        Vec3 lookVec = mc.getRenderViewEntity().getLook(partialTicks);
        Vec3 reachVec = eyePos.addVector(
                lookVec.xCoord * distance,
                lookVec.yCoord * distance,
                lookVec.zCoord * distance
        );

        Vec3 hitVec = null;
        float borderSize = 1.0F;

        List<Entity> entities = mc.theWorld.getEntitiesWithinAABBExcludingEntity(
                mc.getRenderViewEntity(),
                mc.getRenderViewEntity().getEntityBoundingBox()
                        .addCoord(lookVec.xCoord * distance, lookVec.yCoord * distance, lookVec.zCoord * distance)
                        .expand(borderSize, borderSize, borderSize)
        );

        double closestDistance = distance;

        for (Entity target : entities) {
            if (target.canBeCollidedWith()) {
                float collisionBorder = target.getCollisionBorderSize();
                AxisAlignedBB targetBox = target.getEntityBoundingBox()
                        .expand(collisionBorder, collisionBorder, collisionBorder)
                        .expand(expand, expand, expand);

                MovingObjectPosition intercept = targetBox.calculateIntercept(eyePos, reachVec);

                if (targetBox.isVecInside(eyePos)) {
                    if (0.0D < closestDistance || closestDistance == 0.0D) {
                        entity = target;
                        hitVec = (intercept == null) ? eyePos : intercept.hitVec;
                        closestDistance = 0.0D;
                    }
                } else if (intercept != null) {
                    double dist = eyePos.distanceTo(intercept.hitVec);
                    if (dist < closestDistance || closestDistance == 0.0D) {
                        entity = target;
                        hitVec = intercept.hitVec;
                        closestDistance = dist;
                    }
                }
            }
        }

        if (closestDistance < distance && entity instanceof EntityLivingBase) {
            EntityLivingBase living = (EntityLivingBase) entity;
            if (living.canEntityBeSeen(mc.thePlayer)) {
                return new MovingObjectPosition(entity, hitVec);
            }
        }

        return null;
    }
}