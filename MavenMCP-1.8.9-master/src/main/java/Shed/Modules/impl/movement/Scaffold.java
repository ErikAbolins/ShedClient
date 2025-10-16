package Shed.Modules.impl.movement;

import java.util.Arrays;
import java.util.List;

import Shed.Modules.Category;
import Shed.Modules.Module;
import Shed.Modules.ModuleInfo;
import Shed.Shed;
import Shed.event.impl.EventMotion;
import lombok.Getter;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCarpet;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.BlockSnow;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import org.lwjgl.input.Keyboard;

@ModuleInfo(
        name = "Scaffold",
        description = "Bridges for you",
        category = Category.MOVEMENT,
        enabled = false
)
@Getter
public class Scaffold extends Module {

    public Scaffold() {
        setKey(Keyboard.KEY_V);
    }

    private float yaw;
    private float pitch;
    private boolean grounded;
    private boolean headTurned;

    private static final List<Block> INVALID_BLOCKS = Arrays.asList(
            Blocks.air, Blocks.water, Blocks.flowing_water, Blocks.lava, Blocks.flowing_lava,
            Blocks.enchanting_table, Blocks.carpet, Blocks.glass_pane, Blocks.stained_glass_pane,
            Blocks.iron_bars, Blocks.snow_layer, Blocks.ice, Blocks.packed_ice,
            Blocks.coal_ore, Blocks.diamond_ore, Blocks.emerald_ore, Blocks.chest, Blocks.torch,
            Blocks.anvil, Blocks.trapped_chest, Blocks.noteblock, Blocks.jukebox,
            Blocks.gold_ore, Blocks.iron_ore, Blocks.lapis_ore, Blocks.lit_redstone_ore,
            Blocks.quartz_ore, Blocks.redstone_ore, Blocks.wooden_pressure_plate,
            Blocks.stone_pressure_plate, Blocks.light_weighted_pressure_plate,
            Blocks.heavy_weighted_pressure_plate, Blocks.stone_button, Blocks.wooden_button,
            Blocks.lever
    );

    private final Listener<EventMotion> motionListener = new Listener<>(e -> onMotion(e));

    @Override
    public void onEnable() {
        grounded = mc.thePlayer.onGround;
        headTurned = false;
        yaw = mc.thePlayer.rotationYaw;
        pitch = mc.thePlayer.rotationPitch;
        Shed.BUS.subscribe(motionListener);
    }

    @Override
    public void onDisable() {
        Shed.BUS.unsubscribe(motionListener);
        mc.timer.timerSpeed = 1.0F;
        grounded = mc.thePlayer.onGround;
        headTurned = false;
        yaw = mc.thePlayer.rotationYaw;
        pitch = mc.thePlayer.rotationPitch;
    }

    private void onMotion(EventMotion e) {
        // Auto move blocks from inventory to hotbar if needed
        if (hasBlocksInInventory()) {
            ItemStack dummyStack = new ItemStack(Item.getItemById(261));
            for (int i = 9; i < 36; i++) {
                if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                    if ((mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem() instanceof ItemBlock)
                            && isValidItem(mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem())) {
                        for (int m = 36; m < 45; m++) {
                            if (Container.canAddItemToSlot(mc.thePlayer.inventoryContainer.getSlot(m), dummyStack, true)) {
                                swap(i, m - 36);
                                break;
                            }
                        }
                        break;
                    }
                }
            }
        }

        if (!hasBlocks()) {
            return;
        }

        // Calculate position ahead of player
        double d1 = mc.thePlayer.posX;
        double d2 = mc.thePlayer.posZ;
        double d3 = mc.thePlayer.movementInput.moveForward;
        double d4 = mc.thePlayer.movementInput.moveStrafe;
        float f = mc.thePlayer.rotationYaw;

        if (!mc.thePlayer.isCollidedHorizontally) {
            d1 += (d3 * 0.45D * Math.cos(Math.toRadians(f + 90.0F)) + d4 * 0.45D * Math.sin(Math.toRadians(f + 90.0F)));
            d2 += (d3 * 0.45D * Math.sin(Math.toRadians(f + 90.0F)) - d4 * 0.45D * Math.cos(Math.toRadians(f + 90.0F)));
        }

        BlockPos targetPos = new BlockPos(d1, mc.thePlayer.posY - 1.0D, d2);
        Block targetBlock = mc.theWorld.getBlockState(targetPos).getBlock();
        BlockData blockData = getBlockData(targetPos);

        if (e.getType() == EventMotion.Type.PRE) {
            if (isBlockAccessible(targetBlock) && blockData != null) {
                // Calculate rotations to face the block
                float[] rotations = getRotations(blockData.pos, blockData.facing);

                headTurned = true;
                yaw = rotations[0];
                pitch = rotations[1];

//                e.setYaw(rotations[0]);
//                e.setPitch(rotations[1]);

                // Spoof ground state if needed
//                if (!mc.gameSettings.keyBindJump.getIsKeyPressed() && mc.thePlayer.onGround
//                        && isNotCollidingBelow(0.001D) && mc.thePlayer.isCollidedVertically) {
//                    e.setOnGround(false);
//                    grounded = false;
//                } else {
//                    grounded = mc.thePlayer.onGround;
//                }
            }
        } else if (e.getType() == EventMotion.Type.POST) {
            if (isBlockAccessible(targetBlock) && blockData != null) {
                // Actually place the block
                int currentSlot = mc.thePlayer.inventory.currentItem;
                updateHotbar();

                mc.playerController.onPlayerRightClick(
                        mc.thePlayer,
                        mc.theWorld,
                        mc.thePlayer.inventory.getCurrentItem(),
                        blockData.pos,
                        blockData.facing,
                        getVec3(blockData.pos, blockData.facing)
                );

                mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());

                // Reset to original slot
                mc.thePlayer.inventory.currentItem = currentSlot;
                mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(currentSlot));
                mc.playerController.updateController();

                headTurned = false;
                yaw = mc.thePlayer.rotationYaw;
                pitch = mc.thePlayer.rotationPitch;
            }
        }
    }

    private boolean hasBlocksInInventory() {
        for (int i = 9; i < 36; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                Item item = mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem();
                if (item instanceof ItemBlock && isValidItem(item)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasBlocks() {
        for (int i = 36; i < 45; i++) {
            try {
                ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (stack != null && stack.getItem() != null
                        && stack.getItem() instanceof ItemBlock
                        && isValidItem(stack.getItem())) {
                    return true;
                }
            } catch (Exception e) {}
        }
        return false;
    }

    private boolean isValidItem(Item item) {
        if (!(item instanceof ItemBlock)) return false;
        Block block = ((ItemBlock) item).getBlock();
        return !INVALID_BLOCKS.contains(block);
    }

    private void swap(int slot1, int slot2) {
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot1, slot2, 2, mc.thePlayer);
    }

    private void updateHotbar() {
        ItemStack dummyStack = new ItemStack(Item.getItemById(261));
        try {
            for (int i = 36; i < 45; i++) {
                int hotbarSlot = i - 36;
                if (!Container.canAddItemToSlot(mc.thePlayer.inventoryContainer.getSlot(i), dummyStack, true)
                        && mc.thePlayer.inventoryContainer.getSlot(i).getStack() != null
                        && mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem() instanceof ItemBlock
                        && isValidItem(mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem())
                        && mc.thePlayer.inventoryContainer.getSlot(i).getStack().stackSize != 0) {

                    if (mc.thePlayer.inventory.currentItem != hotbarSlot) {
                        mc.thePlayer.inventory.currentItem = hotbarSlot;
                        mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                        mc.playerController.updateController();
                        break;
                    }
                }
            }
        } catch (Exception e) {}
    }

    private BlockData getBlockData(BlockPos pos) {
        // Check immediate neighbors
        if (isValidBlock(pos.add(0, -1, 0))) return new BlockData(pos.add(0, -1, 0), EnumFacing.UP);
        if (isValidBlock(pos.add(-1, 0, 0))) return new BlockData(pos.add(-1, 0, 0), EnumFacing.EAST);
        if (isValidBlock(pos.add(1, 0, 0))) return new BlockData(pos.add(1, 0, 0), EnumFacing.WEST);
        if (isValidBlock(pos.add(0, 0, 1))) return new BlockData(pos.add(0, 0, 1), EnumFacing.NORTH);
        if (isValidBlock(pos.add(0, 0, -1))) return new BlockData(pos.add(0, 0, -1), EnumFacing.SOUTH);

        // Check extended positions (one layer down and out)
        BlockPos down = pos.add(0, -1, 0);
        if (isValidBlock(down.add(0, -1, 0))) return new BlockData(down.add(0, -1, 0), EnumFacing.UP);
        if (isValidBlock(down.add(-1, 0, 0))) return new BlockData(down.add(-1, 0, 0), EnumFacing.EAST);
        if (isValidBlock(down.add(1, 0, 0))) return new BlockData(down.add(1, 0, 0), EnumFacing.WEST);
        if (isValidBlock(down.add(0, 0, 1))) return new BlockData(down.add(0, 0, 1), EnumFacing.NORTH);
        if (isValidBlock(down.add(0, 0, -1))) return new BlockData(down.add(0, 0, -1), EnumFacing.SOUTH);

        return null;
    }

    private boolean isValidBlock(BlockPos pos) {
        Block block = mc.theWorld.getBlockState(pos).getBlock();
        if (block instanceof BlockLadder || block instanceof BlockCarpet
                || block instanceof BlockSnow || block instanceof BlockSkull) {
            return true;
        }
        if (block.getMaterial().isSolid() && !block.getMaterial().isLiquid()) {
            return true;
        }
        return false;
    }

    private boolean isBlockAccessible(Block block) {
        if (block.getMaterial().isReplaceable()) {
            if (block instanceof BlockSnow && block.getBlockBoundsMaxY() > 0.125D) {
                return false;
            }
            return true;
        }
        return false;
    }

    private boolean isNotCollidingBelow(double offset) {
        return !mc.theWorld.getCollidingBoundingBoxes(
                mc.thePlayer,
                mc.thePlayer.getEntityBoundingBox().offset(0.0D, -offset, 0.0D)
        ).isEmpty();
    }

    private float[] getRotations(BlockPos pos, EnumFacing facing) {
        double d1 = pos.getX() + 0.5D - mc.thePlayer.posX + facing.getFrontOffsetX() / 2.0D;
        double d2 = pos.getZ() + 0.5D - mc.thePlayer.posZ + facing.getFrontOffsetZ() / 2.0D;
        double d3 = mc.thePlayer.posY + mc.thePlayer.getEyeHeight() - (pos.getY() + 0.5D);
        double d4 = MathHelper.sqrt_double(d1 * d1 + d2 * d2);
        float yaw = (float)(Math.atan2(d2, d1) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float)(Math.atan2(d3, d4) * 180.0D / Math.PI);
        if (yaw < 0.0F) yaw += 360.0F;
        return new float[] { yaw, pitch };
    }

    private Vec3 getVec3(BlockPos pos, EnumFacing facing) {
        double x = pos.getX() + 0.5D + facing.getFrontOffsetX() / 2.0D;
        double y = pos.getY() + 0.5D + facing.getFrontOffsetY() / 2.0D;
        double z = pos.getZ() + 0.5D + facing.getFrontOffsetZ() / 2.0D;
        return new Vec3(x, y, z);
    }

    public class BlockData {
        public BlockPos pos;
        public EnumFacing facing;

        public BlockData(BlockPos pos, EnumFacing facing) {
            this.pos = pos;
            this.facing = facing;
        }
    }
}