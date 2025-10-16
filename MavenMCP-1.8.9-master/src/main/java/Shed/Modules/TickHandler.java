package Shed.Modules;

import Shed.Modules.impl.movement.Eagle;
import net.minecraft.client.Minecraft;


public class TickHandler {
    private final Minecraft mc = Minecraft.getMinecraft();

    public void onClientTick() {
        if (Eagle.INSTANCE != null && Eagle.INSTANCE.isToggled()){
            Eagle.INSTANCE.onTickEvent();
        }
    }
}
