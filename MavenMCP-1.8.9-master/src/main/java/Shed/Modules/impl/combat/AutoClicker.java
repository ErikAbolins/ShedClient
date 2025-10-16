package Shed.Modules.impl.combat;

import Shed.Modules.Category;
import Shed.Modules.Module;
import Shed.Modules.ModuleInfo;
import Shed.Setting.impl.BooleanSetting;
import Shed.Setting.impl.NumberSetting;
import Shed.event.impl.EventUpdate;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Mouse;

@ModuleInfo(
        name = "AutoClicker",
        description = " ",
        category = Category.COMBAT
)

// AutoClicker.java
// Drop into your modules package. Assumes you have access to:
// - mc (Minecraft instance)
// - a Settings system with NumberSetting and BooleanSetting
// - an EventSubscribe or Alpine-style listener system and EventUpdate

public class AutoClicker extends Module {
    public NumberSetting cps = new NumberSetting("CPS", 8.0, 1.0, 20.0, 0.1);
    public BooleanSetting randomize = new BooleanSetting("Randomize", true);
    public NumberSetting randRange = new NumberSetting("RandRange", 0.6, 0.0, 2.0, 0.05); // seconds variation multiplier

    private long lastAttackTime = 0L;
    private double nextDelayMs = 0.0;

    public AutoClicker() {
        addSetting(
                cps,
                randomize,
                randRange
        );
    }


    @Override
    public void onEnable() {
        lastAttackTime = System.currentTimeMillis();
        computeNextDelay();
    }

    @Override
    public void onUpdate() {
        if (!this.isToggled()) return;

        if (mc.gameSettings.keyBindAttack.isKeyDown()) {
            long now = System.currentTimeMillis();
            if (now - lastAttackTime >= (long) nextDelayMs) {
                // manually trigger a single attack without simulating release
                mc.thePlayer.swingItem(); // visual swing
                mc.clickMouse();          // triggers attack
                lastAttackTime = now;
                computeNextDelay();
            }
        }
    }

    private void computeNextDelay() {
        double baseCps = cps.getVal();
        if (baseCps <= 0.0) baseCps = 1.0;
        double baseDelay = 1000.0 / baseCps;

        if (randomize.isEnabled()) {
            double range = randRange.getVal();
            double factor = 1.0 + (randomDouble(-range, range) * 0.05);
            nextDelayMs = Math.max(1.0, baseDelay * factor);
        } else {
            nextDelayMs = baseDelay;
        }
    }

    private double randomDouble(double min, double max) {
        return min + Math.random() * (max - min);
    }
}
