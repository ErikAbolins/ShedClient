package Shed.Modules.impl.movement;

import Shed.Modules.Category;
import Shed.Modules.Module;
import Shed.Modules.ModuleInfo;
import Shed.event.impl.EventUpdate;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFood;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

@ModuleInfo(
        name = "NoSlowDown",
        description = "Prevents slowdown when using items",
        category = Category.MOVEMENT
)
public class NoSlowDown extends Module {

    @Override
    public void onUpdate() {
        if(mc.thePlayer == null) return;

        // Check if player is using an item
        if(mc.thePlayer.isUsingItem()) {
            // Send fake stop using packet
            mc.getNetHandler().addToSendQueue(
                    new C07PacketPlayerDigging(
                            C07PacketPlayerDigging.Action.RELEASE_USE_ITEM,
                            BlockPos.ORIGIN,
                            EnumFacing.DOWN
                    )
            );

            // Send fake start using packet
            mc.getNetHandler().addToSendQueue(
                    new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem())
            );
        }
    }
}