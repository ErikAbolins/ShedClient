package Shed.Modules.impl.combat;

import Shed.Modules.Category;
import Shed.Modules.Module;
import Shed.Modules.ModuleInfo;
import Shed.Setting.impl.BooleanSetting;
import Shed.Setting.impl.NumberSetting;
import Shed.event.impl.EventUpdate;
import Shed.Shed;
import Shed.util.ReachUtil;
import me.zero.alpine.listener.Listener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.util.MovingObjectPosition;

@ModuleInfo(
        name = "Reach",
        description = "Extends your attack reach",
        category = Category.COMBAT,
        enabled = false
)
public class Reach extends Module {

    private NumberSetting reach = new NumberSetting("Reach",  3.2, 3.0, 6.0, 0.1);
    private BooleanSetting onlySprinting = new BooleanSetting("Only Sprinting",  true);
    private BooleanSetting weaponOnly = new BooleanSetting("Weapon Only",  false);

    private final Listener<EventUpdate> updateListener = new Listener<>(e -> onUpdate());

    public Reach() {
        addSetting(reach);
        addSetting(onlySprinting);
        addSetting(weaponOnly);
    }

    @Override
    public void onEnable() {
        Shed.BUS.subscribe(updateListener);
    }

    @Override
    public void onDisable() {
        Shed.BUS.unsubscribe(updateListener);
    }

    public void onUpdate() {
        // Safety checks
        if (mc.thePlayer == null || mc.theWorld == null || mc.currentScreen != null) return;
        if (mc.thePlayer.isRiding() || mc.thePlayer.isInWater() || mc.thePlayer.isInLava()) return;
        if (mc.thePlayer.getFoodStats().getFoodLevel() <= 6) return;

        // Potion checks
        if (mc.thePlayer.isPotionActive(Potion.moveSlowdown)) return;
        if (mc.thePlayer.isPotionActive(Potion.digSlowdown)) return;
        if (mc.thePlayer.isPotionActive(Potion.confusion)) return;
        if (mc.thePlayer.isPotionActive(Potion.blindness)) return;
        if (mc.thePlayer.isPotionActive(Potion.jump)) return;
        if (mc.thePlayer.isPotionActive(Potion.weakness)) return;

        // Optional checks
        if (onlySprinting.isEnabled() && !mc.thePlayer.isSprinting()) return;

        if (weaponOnly.isEnabled()) {
            if (mc.thePlayer.getHeldItem() == null) return;
            if (!(mc.thePlayer.getHeldItem().getItem() instanceof net.minecraft.item.ItemSword) &&
                    !(mc.thePlayer.getHeldItem().getItem() instanceof net.minecraft.item.ItemAxe)) {
                return;
            }
        }

        // Get entity at extended reach
        MovingObjectPosition object = ReachUtil.getMouseOver(reach.getVal(), 0.0D, mc.timer.renderPartialTicks);
        if (object != null) {
            mc.objectMouseOver = object;
        }
    }
}