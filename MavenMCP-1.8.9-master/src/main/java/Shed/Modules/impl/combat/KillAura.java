package Shed.Modules.impl.combat;

import Shed.Modules.Category;
import Shed.Modules.Module;
import Shed.Modules.ModuleInfo;
import Shed.Shed;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.util.MathHelper;

import java.util.ArrayList;
import java.util.List;

@ModuleInfo(
        name = "KillAura",
        description = "Automatically attacks entities",
        category = Category.COMBAT
)
public class KillAura extends Module {

    // Hardcoded settings for now (change these as needed)
    private final double range = 4.2;
    private final boolean autoBlock = true;
    private final boolean attackPlayers = true;
    private final boolean attackMobs = true;
    private final boolean attackAnimals = true;
    private final boolean extraKB = true;
    private final int aps = 10; // Attacks per second

    private EntityLivingBase target;
    private List<EntityLivingBase> entities = new ArrayList<>();

    private float yaw, pitch;
    private float progressYaw, progressPitch;

    private long lastAttack = 0;

    @Override
    public void onEnable() {
        if(mc.thePlayer != null) {
            progressYaw = normalise(mc.thePlayer.rotationYaw, -180, 180);
            yaw = progressYaw;
            progressPitch = mc.thePlayer.rotationPitch;
            pitch = progressPitch;
        }
    }

    @Override
    public void onDisable() {
        target = null;
        entities.clear();
    }

    @Override
    public void onUpdate() {
        System.out.println("[KillAura] Update tick - Target: " + (target != null ? target.getName() : "null"));
        if(mc.thePlayer == null || mc.theWorld == null) return;

        // Find targets
        updateTargets();

        // Attack if we have a target
        if(target != null && isValid(target)) {
            // Calculate rotations
            calculateRotations();

            // Smooth rotation progress
            smoothRotations();

            // Attack with delay
            long currentTime = System.currentTimeMillis();
            if(currentTime - lastAttack >= (1000 / aps)) {
                attack();
                lastAttack = currentTime;
            }

            // AutoBlock
            if(autoBlock && mc.thePlayer.getHeldItem() != null &&
                    mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
                // Block logic here if needed
            }
        } else {
            // Reset rotations to player's actual rotation
            progressYaw = normalise(mc.thePlayer.rotationYaw, -180, 180);
            yaw = progressYaw;
            progressPitch = mc.thePlayer.rotationPitch;
            pitch = progressPitch;
        }
    }

    private void updateTargets() {
        entities.clear();
        target = null;

        System.out.println("[KillAura] Scanning for targets...");
        System.out.println("[KillAura] Total entities in world: " + mc.theWorld.loadedEntityList.size());

        double closestDistance = range;
        int validCount = 0;

        for(Object obj : mc.theWorld.loadedEntityList) {
            if(!(obj instanceof EntityLivingBase)) continue;

            EntityLivingBase entity = (EntityLivingBase) obj;

            System.out.println("[KillAura] Checking entity: " + entity.getName() + " | Valid: " + isValid(entity));

            if(!isValid(entity)) continue;

            validCount++;
            double distance = mc.thePlayer.getDistanceToEntity(entity);
            System.out.println("[KillAura] Valid target found: " + entity.getName() + " | Distance: " + distance);

            if(distance < closestDistance) {
                target = entity;
                closestDistance = distance;
            }

            entities.add(entity);
        }

        System.out.println("[KillAura] Valid targets: " + validCount + " | Selected: " + (target != null ? target.getName() : "null"));
    }

    private void calculateRotations() {
        if(target == null) return;

        double posX = target.posX - mc.thePlayer.posX;
        double posZ = target.posZ - mc.thePlayer.posZ;
        double posY = (target.posY + target.getEyeHeight()) - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
        double helper = MathHelper.sqrt_double((posX * posX) + (posZ * posZ));

        float newYaw = (float) Math.toDegrees(-Math.atan(posX / posZ));
        float newPitch = (float) -Math.toDegrees(Math.atan(posY / helper));

        if(posZ < 0 && posX < 0) {
            newYaw = (float)(90 + Math.toDegrees(Math.atan(posZ / posX)));
        } else if(posZ < 0 && posX > 0) {
            newYaw = (float)(-90 + Math.toDegrees(Math.atan(posZ / posX)));
        }

        yaw = newYaw;
        pitch = newPitch;
    }

    private void smoothRotations() {
        int speed = 3;

        if(progressYaw != yaw) {
            progressYaw += ((yaw - progressYaw) / speed) + (progressYaw > yaw ? -0.1f : 0.1f);
        }

        if(progressPitch != pitch) {
            progressPitch += ((pitch - progressPitch) / speed) + (progressPitch > pitch ? -0.1f : 0.1f);
        }

        // Apply rotations to player
        mc.thePlayer.rotationYaw = progressYaw;
        mc.thePlayer.rotationPitch = progressPitch;
    }

    private void attack() {
        if(target == null) return;

        // Extra knockback
        if(extraKB && mc.thePlayer.getFoodStats().getFoodLevel() > 6) {
            mc.thePlayer.setSprinting(true);
            mc.getNetHandler().addToSendQueue(
                    new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING)
            );
        }

        // Attack
        mc.playerController.attackEntity(mc.thePlayer, target);
        mc.thePlayer.swingItem();

        // Stop sprinting
        if(extraKB && mc.thePlayer.getFoodStats().getFoodLevel() > 6) {
            mc.getNetHandler().addToSendQueue(
                    new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING)
            );
        }
    }

    private boolean isValid(EntityLivingBase entity) {
        if(entity == null) {
            System.out.println("  - Null entity");
            return false;
        }
        if(entity == mc.thePlayer) {
            System.out.println("  - Is player");
            return false;
        }
        if(entity.isDead || entity.getHealth() <= 0) {
            System.out.println("  - Dead or no health");
            return false;
        }
        if(entity.isInvisible()) {
            System.out.println("  - Invisible");
            return false;
        }

        double distance = mc.thePlayer.getDistanceToEntity(entity);
        System.out.println("  - Distance: " + distance + " | Range: " + range);
        if(distance > range) return false;

        // Wall check
        if(!mc.thePlayer.canEntityBeSeen(entity) && distance > 3.0) {
            System.out.println("  - Behind wall");
            return false;
        }

        // Entity type check
        System.out.println("  - Type: " + entity.getClass().getSimpleName());
        if(entity instanceof EntityOtherPlayerMP && attackPlayers) return true;
        if(entity instanceof EntityMob && attackMobs) return true;
        if(entity instanceof EntityAnimal && attackAnimals) return true;

        System.out.println("  - Not a valid entity type");
        return false;
    }

    private float normalise(float value, float start, float end) {
        float width = end - start;
        float offsetValue = value - start;
        return (offsetValue - ((float)Math.floor(offsetValue / width) * width)) + start;
    }
}