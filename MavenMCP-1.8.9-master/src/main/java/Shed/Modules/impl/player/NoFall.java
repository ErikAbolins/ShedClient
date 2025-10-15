package Shed.Modules.impl.player;

import Shed.Modules.Category;
import Shed.Modules.Module;
import net.minecraft.network.play.client.C03PacketPlayer;
import Shed.Modules.ModuleInfo;


@ModuleInfo(
        name = "NoFall",
        description = ("Removes fall damage"),
        category = Category.PLAYER,
        enabled = false
)

public class NoFall extends Module {
    @Override
    public void onUpdate() {
        if (mc.thePlayer.fallDistance > 2f && mc.thePlayer.fallDistance < 20) {
            mc.getNetHandler().addToSendQueue(new C03PacketPlayer(true));
            }
        }
    }

