package Shed.Modules.impl.combat;

import Shed.Modules.Category;
import Shed.Modules.Module;
import Shed.Modules.ModuleInfo;
import Shed.Setting.impl.BooleanSetting;
import Shed.Setting.impl.NumberSetting;
import Shed.Setting.impl.ModeSetting;
import net.minecraft.client.entity.EntityOtherPlayerMP;
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

    // Settings
    private BooleanSetting attackPlayersSetting;
    private BooleanSetting canAttackMobs;
    private BooleanSetting attackAnimalsSetting;
    private NumberSetting Range;
    private NumberSetting aps;
    private BooleanSetting autoBlock;
    private BooleanSetting extraKBSetting;
    private BooleanSetting rotate;
    private ModeSetting priority;

    private EntityLivingBase target;
    private List<EntityLivingBase> entities = new ArrayList<>();
    private long lastAttack = 0;

    public KillAura() {
        addSetting(
                attackPlayersSetting = new BooleanSetting("Hit Players", true),
                canAttackMobs = new BooleanSetting("Hit Mobs", true),
                attackAnimalsSetting = new BooleanSetting("Hit Animals", false),
                Range = new NumberSetting("Range", 3.0, 2.5, 10.0, 0.1),
                aps = new NumberSetting("CPS", 6.0, 1.0, 50.0, 0.1),
                autoBlock = new BooleanSetting("AutoBlock", false),
                extraKBSetting = new BooleanSetting("Extra Knockback", true),
                rotate = new BooleanSetting("Rotate", true),
                priority = new ModeSetting("Priority", "Closest", "Health", "Armor")
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
            if (currentTime - lastAttack >= (1000 / aps.getVal())) {
                attack(target);
                lastAttack = currentTime;
            }

            if (autoBlock.isEnabled() && mc.thePlayer.getHeldItem() != null &&
                    mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
                // Auto-block logic
            }

            if (rotate.isEnabled()) {
                // Rotate to target logic
            }
        }
    }

    private void updateTargets() {
        entities.clear();
        target = null;

        double closestDistance = Range.getVal();
        EntityLivingBase bestTarget = null;

        for (Object obj : mc.theWorld.loadedEntityList) {
            if (!(obj instanceof EntityLivingBase)) continue;
            EntityLivingBase entity = (EntityLivingBase) obj;

            if (!isValid(entity)) continue;

            double distance = mc.thePlayer.getDistanceToEntity(entity);

            if (priority.getCurrMode().equals("Closest") && distance < closestDistance) {
                closestDistance = distance;
                bestTarget = entity;
            } else if (priority.getCurrMode().equals("Health")) {
                if (bestTarget == null || entity.getHealth() < bestTarget.getHealth()) {
                    bestTarget = entity;
                }
            } else if (priority.getCurrMode().equals("Armor")) {
                // You can implement armor-based priority here
                bestTarget = entity;
            }

            entities.add(entity);
        }

        target = bestTarget;
    }

    private void attack(EntityLivingBase target) {
        if (target == null) return;

        if (extraKBSetting.isEnabled() && mc.thePlayer.getFoodStats().getFoodLevel() > 6) {
            mc.thePlayer.setSprinting(true);
            mc.getNetHandler().addToSendQueue(
                    new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING)
            );
        }

        mc.playerController.attackEntity(mc.thePlayer, target);
        mc.thePlayer.swingItem();

        if (extraKBSetting.isEnabled() && mc.thePlayer.getFoodStats().getFoodLevel() > 6) {
            mc.getNetHandler().addToSendQueue(
                    new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING)
            );
        }
    }

    private boolean isValid(EntityLivingBase entity) {
        if (entity == null || entity == mc.thePlayer) return false;
        if (entity.isDead || entity.getHealth() <= 0) return false;
        if (entity.isInvisible()) return false;

        double distance = mc.thePlayer.getDistanceToEntity(entity);
        if (distance > Range.getVal()) return false;
        if (!mc.thePlayer.canEntityBeSeen(entity) && distance > 3.0) return false;

        if (entity instanceof EntityOtherPlayerMP && attackPlayersSetting.isEnabled()) return true;
        if (entity instanceof EntityMob && canAttackMobs.isEnabled()) return true;
        if (entity instanceof EntityAnimal && attackAnimalsSetting.isEnabled()) return true;

        return false;
    }
}
