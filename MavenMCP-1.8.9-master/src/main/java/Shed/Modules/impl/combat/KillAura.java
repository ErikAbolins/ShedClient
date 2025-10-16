package Shed.Modules.impl.combat;

import Shed.Modules.Category;
import Shed.Modules.Module;
import Shed.Modules.ModuleInfo;
import Shed.Setting.impl.BooleanSetting;
import Shed.Setting.impl.NumberSetting;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

@ModuleInfo(
        name = "KillAura",
        description = "Automatically attacks nearby entities without rotating camera",
        category = Category.COMBAT
)
public class KillAura extends Module {

    private final double range = 4.2;
    private final boolean autoBlock = true;
    private final boolean attackPlayers = true;
    private final boolean attackMobs = true;
    private final boolean attackAnimals = true;
    private final boolean extraKB = true;
    private final int aps = 10;

    private EntityLivingBase target;
    private List<EntityLivingBase> entities = new ArrayList<>();

    private long lastAttack = 0;

    public KillAura() {
        addSetting(
                new BooleanSetting("Hit Mobs", true),
                new NumberSetting("Range", 3.0, 2.5, 10.0, 0.1)
        );

        setKey(Keyboard.KEY_F);
    }

    @Override
    public void onEnable() {
        target = null;
        entities.clear();
    }

    @Override
    public void onDisable() {
        target = null;
        entities.clear();
    }

    @Override
    public void onUpdate() {
        if (mc.thePlayer == null || mc.theWorld == null) return;

        updateTargets();

        if (target != null && isValid(target)) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastAttack >= (1000 / aps)) {
                attack(target);
                lastAttack = currentTime;
            }

            if (autoBlock && mc.thePlayer.getHeldItem() != null &&
                    mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
            }
        }
    }

    private void updateTargets() {
        entities.clear();
        target = null;

        double closestDistance = range;

        for (Object obj : mc.theWorld.loadedEntityList) {
            if (!(obj instanceof EntityLivingBase)) continue;
            EntityLivingBase entity = (EntityLivingBase) obj;

            if (!isValid(entity)) continue;

            double distance = mc.thePlayer.getDistanceToEntity(entity);
            if (distance < closestDistance) {
                target = entity;
                closestDistance = distance;
            }
            entities.add(entity);
        }
    }

    private void attack(EntityLivingBase target) {
        if (target == null) return;

        if (extraKB && mc.thePlayer.getFoodStats().getFoodLevel() > 6) {
            mc.thePlayer.setSprinting(true);
            mc.getNetHandler().addToSendQueue(
                    new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING)
            );
        }

        mc.playerController.attackEntity(mc.thePlayer, target);
        mc.thePlayer.swingItem();

        if (extraKB && mc.thePlayer.getFoodStats().getFoodLevel() > 6) {
            mc.getNetHandler().addToSendQueue(
                    new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING)
            );
        }
    }

    private boolean isValid(EntityLivingBase entity) {
        if (entity == null) return false;
        if (entity == mc.thePlayer) return false;
        if (entity.isDead || entity.getHealth() <= 0) return false;
        if (entity.isInvisible()) return false;

        double distance = mc.thePlayer.getDistanceToEntity(entity);
        if (distance > range) return false;

        if (!mc.thePlayer.canEntityBeSeen(entity) && distance > 3.0) return false;

        if (entity instanceof EntityOtherPlayerMP && attackPlayers) return true;
        if (entity instanceof EntityMob && attackMobs) return true;
        if (entity instanceof EntityAnimal && attackAnimals) return true;

        return false;
    }
}
