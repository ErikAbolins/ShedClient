package Shed.Modules.impl.movement;

import Shed.Modules.Category;
import Shed.Modules.Module;
import Shed.Modules.ModuleInfo;
import net.minecraft.potion.Potion;
import org.lwjgl.input.Keyboard;

@ModuleInfo(
        name = "BHOP",
        description = "Automatically bunny hops without holding jump",
        category = Category.MOVEMENT,
        enabled = false
)
public class BHop extends Module {

    private double moveSpeed;
    private double lastDist;
    private int stage;

    public BHop() {
        setKey(Keyboard.KEY_R);
    }

    @Override
    public void onEnable() {
        if (mc.thePlayer != null) {
            moveSpeed = getBaseMoveSpeed();
            lastDist = 0.0D;
            stage = 2;
            mc.timer.timerSpeed = 1.0f;
        }
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1.0f;
        moveSpeed = 0;
        stage = 0;
    }

    @Override
    public void onUpdate() {
        if (mc.thePlayer == null || mc.theWorld == null) return;
        if (mc.thePlayer.isInWater() || mc.thePlayer.isInLava()) return;

        double xDist = mc.thePlayer.posX - mc.thePlayer.prevPosX;
        double zDist = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
        lastDist = Math.sqrt(xDist * xDist + zDist * zDist);

        if (mc.thePlayer.moveForward == 0 && mc.thePlayer.moveStrafing == 0) {
            moveSpeed = getBaseMoveSpeed();
            return;
        }

        if (mc.thePlayer.onGround) {
            mc.thePlayer.jump();
            moveSpeed = getBaseMoveSpeed() * 1.75D;
        } else {
            moveSpeed = Math.max(moveSpeed * 0.985D, getBaseMoveSpeed());
        }

        applyMovement(moveSpeed);
    }

    private void applyMovement(double speed) {
        float yaw = mc.thePlayer.rotationYaw;
        double forward = mc.thePlayer.movementInput.moveForward;
        double strafe = mc.thePlayer.movementInput.moveStrafe;

        if (forward == 0 && strafe == 0) {
            mc.thePlayer.motionX = 0;
            mc.thePlayer.motionZ = 0;
            return;
        }

        if (forward != 0) {
            if (strafe > 0) yaw += (forward > 0 ? -45 : 45);
            else if (strafe < 0) yaw += (forward > 0 ? 45 : -45);
            strafe = 0;
            forward = forward > 0 ? 1 : -1;
        }

        double rad = Math.toRadians(yaw + 90.0F);
        mc.thePlayer.motionX = forward * speed * Math.cos(rad) + strafe * speed * Math.sin(rad);
        mc.thePlayer.motionZ = forward * speed * Math.sin(rad) - strafe * speed * Math.cos(rad);
    }

    private double getBaseMoveSpeed() {
        double baseSpeed = 0.2873D;
        if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            int amplifier = mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
            baseSpeed *= 1.0 + 0.2 * (amplifier + 1);
        }
        return baseSpeed;
    }
}
