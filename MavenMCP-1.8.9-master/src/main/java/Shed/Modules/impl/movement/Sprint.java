package Shed.Modules.impl.movement;

import Shed.Modules.Category;
import Shed.Modules.Module;
import Shed.Modules.ModuleInfo;

@ModuleInfo(
        name = "Sprint",
        description = "Automatically sprints",
        category = Category.MOVEMENT,
        enabled = false
)
public class Sprint extends Module {
    @Override
    public void onUpdate() {
        mc.gameSettings.keyBindSprint.setPressed(true);
    }
}
