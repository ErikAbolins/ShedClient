package Shed.Modules.impl.movement;

import Shed.Modules.Category;
import Shed.Modules.Module;
import Shed.Modules.ModuleInfo;
import Shed.Setting.impl.BooleanSetting;
import org.lwjgl.input.Keyboard;

@ModuleInfo(
        name = "InvMove",
        description = "Move while your inventory or GUIs are open",
        category = Category.MOVEMENT
)
public class InventoryMove extends Module {

    private BooleanSetting sneak, sprint;

    public InventoryMove() {
        addSetting(
                sneak = new BooleanSetting("Sneak", true),
                sprint = new BooleanSetting("Sprint", true)
        );

        setKey(Keyboard.KEY_NONE);
    }

    @Override
    public void onUpdate() {
        if(mc.currentScreen != null) {
            // Keep movement keys active while in a GUI
            mc.gameSettings.keyBindForward.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode());
            mc.gameSettings.keyBindBack.pressed    = Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode());
            mc.gameSettings.keyBindLeft.pressed    = Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode());
            mc.gameSettings.keyBindRight.pressed   = Keyboard.isKeyDown(mc.gameSettings.keyBindRight.getKeyCode());
            mc.gameSettings.keyBindJump.pressed    = Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode());
            mc.gameSettings.keyBindSneak.pressed   = Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode());
        }
    }

}
