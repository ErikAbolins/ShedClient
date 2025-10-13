package Shed.Modules.impl.movement;

import Shed.Modules.Category;
import Shed.Modules.Module;
import Shed.Modules.ModuleInfo;

@ModuleInfo(
        name = "Flight",
        description = "Simple Fly",
        category = Category.MOVEMENT,
        enabled = true
)
public class Flight extends Module {
    @Override
    public void onUpdate() {
        if(mc.thePlayer != null) {
            mc.thePlayer.capabilities.isFlying = true;

            if(mc.gameSettings.keyBindJump.isPressed()) {
                mc.thePlayer.motionY += 0.2;
            }

            if(mc.gameSettings.keyBindSneak.isPressed()) {
                mc.thePlayer.motionY -= 0.2;
            }

            if(mc.gameSettings.keyBindForward.isPressed()) {
                mc.thePlayer.capabilities.setFlySpeed(0.2f);
            }
        }
    }

    @Override
    public void onDisable() {
        mc.thePlayer.capabilities.isFlying = false;

    }
}
