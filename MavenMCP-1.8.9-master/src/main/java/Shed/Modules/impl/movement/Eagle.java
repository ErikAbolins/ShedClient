package Shed.Modules.impl.movement;

import Shed.Modules.Category;
import Shed.Modules.Module;
import Shed.Modules.ModuleInfo;
import Shed.Setting.impl.BooleanSetting;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Mouse;

@ModuleInfo(
        name = "Eagle",
        description = "Legit Scaffolding",
        category = Category.MOVEMENT
)
public class Eagle extends Module {

    public static Eagle INSTANCE;
    private long lastSneakTime = 0;
    public final BooleanSetting blockOnly = new BooleanSetting("Block-Only", false);
    public final BooleanSetting backwardsOnly = new BooleanSetting("Backwards-Only", false);
    public final BooleanSetting holdingOnly = new BooleanSetting("Holding-Only", false);
    public final BooleanSetting onlyUnsneakWithBlocks = new BooleanSetting("Only Unsneak With Blocks", false);
    public final BooleanSetting unsneakWhenForwards = new BooleanSetting("Unsneak When Forwards", false);

    private final KeyBinding sneakKey = mc.gameSettings.keyBindSneak;
    private long lastToggle = 0;

    public Eagle() {
        INSTANCE = this;

        addSetting(blockOnly);
        addSetting(backwardsOnly);
        addSetting(holdingOnly);
        addSetting(onlyUnsneakWithBlocks);
        addSetting(unsneakWhenForwards);
    }

    private boolean holdingBlock() {
        ItemStack stack = mc.thePlayer.getHeldItem();
        return stack != null && stack.getItem() instanceof ItemBlock;
    }

    private boolean isButtonDown(int key) {
        return Mouse.isButtonDown(key);
    }

    private boolean overAir(Entity entity, double distance) {
        BlockPos pos = new BlockPos(
                MathHelper.floor_double(entity.posX),
                MathHelper.floor_double(entity.posY - distance),
                MathHelper.floor_double(entity.posZ)
        );
        return mc.theWorld.isAirBlock(pos);
    }

    private boolean canLegitWork() {
        return mc.thePlayer != null && mc.theWorld != null && !mc.thePlayer.capabilities.isFlying;
    }

    public void onTickEvent() {
        if (mc.thePlayer.capabilities.isFlying || !mc.thePlayer.onGround)
            return;

        if (blockOnly.isEnabled() && !holdingBlock())
            return;

        if (backwardsOnly.isEnabled() && mc.thePlayer.moveForward >= 0)
            return;

        if (holdingOnly.isEnabled() && !Mouse.isButtonDown(1))
            return;

        boolean overAir = overAir(mc.thePlayer, 1.0D);
        boolean walkingForward = mc.thePlayer.moveForward > 0;

        if (unsneakWhenForwards.isEnabled() && mc.thePlayer.isSneaking() && walkingForward) {
            if (onlyUnsneakWithBlocks.isEnabled() && !holdingBlock()) {
                setShift(true);
                return;
            }

            if (!overAir) {
                setShift(false);
            } else {
                setShift(true);
            }
            return;
        }

        if (overAir) {
            if (!mc.thePlayer.isSneaking()) {
                setShift(true);
            }
        } else {
            if (mc.thePlayer.isSneaking()) {
                setShift(false);
            }
        }
    }


    private void setShift(boolean value) {
        long now = System.currentTimeMillis();
        if (now - lastToggle < 50) return;
        sneakKey.pressed = value;
        mc.thePlayer.setSneaking(value);
        lastToggle = now;
    }
}
