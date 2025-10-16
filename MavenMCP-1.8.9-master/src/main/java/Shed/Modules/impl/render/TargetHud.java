package Shed.Modules.impl.render;

import Shed.Modules.Category;
import Shed.Modules.Module;
import Shed.Modules.ModuleInfo;
import Shed.util.RenderUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

@ModuleInfo(
        name = "TargetHud",
        description = " ",
        category = Category.RENDER
)

public class TargetHud extends Module {
    private List<Long> clicks = new ArrayList<Long>();

    int width;
    int height;
    int x;
    int y;
    EntityLivingBase target;


    @Override
    public void on2D(ScaledResolution sr) {
        width = 120;
        height = 55;
        x = sr.getScaledWidth() / 2 - width / 2;
        y = sr.getScaledHeight() / 2 - height / 2;

        if(!(mc.pointedEntity instanceof EntityItemFrame)){
            EntityLivingBase target = (EntityLivingBase)mc.pointedEntity;
            if(target != null){
                RenderUtil.drawRect(x, y, width, height, 0xFF313335);
                fr.drawString(String.format("%.0f\u2764", target.getHealth()), x + width - 30, y + 4, 0xFFFF0000);

                if(target instanceof EntityPlayer) {
                    drawPlayerHead(x + 3, y + 3, 24, (EntityPlayer) target);
                    fr.drawStringWithShadow(target.getName(), x + 32, y + 8, -1);
                } else {
                    fr.drawStringWithShadow(target.getName(), x + 10, y + 8, -1);
                }
                fr.drawString(String.format("%.2f Blocks", target.getDistance(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ)), x + 5, y + -5, -1);
                fr.drawString("CPS: " + getCPSLeft(),  x + width - 40, y + height - 5, -1);

                renderHealthBar(target);
            }
        }
    }


    private int getCPSLeft() {
        final long time = System.currentTimeMillis();
        clicks.removeIf(aLong -> aLong + 1000 < time);
        return clicks.size();
    }

    private void renderHealthBar(EntityLivingBase target) {
        int healthBarWidth = width - 10;
        int healthBarHeight = 5;
        int left = x + 5;
        int top = y + height - 15;

        RenderUtil.drawRect(left, top, healthBarWidth, healthBarHeight, 0xFF19170D);

        float healthPercent = target.getHealth() / target.getMaxHealth();
        int healthWidth = (int) (healthBarWidth * healthPercent);

        RenderUtil.drawRect(left, top, healthWidth, healthBarHeight, 0xFFE93741);
    }

    private void drawPlayerHead(int x, int y, int size, EntityPlayer player){
        NetworkPlayerInfo playerInfo = mc.getNetHandler().getPlayerInfo(player.getUniqueID());
        if(playerInfo != null){
            mc.getTextureManager().bindTexture(playerInfo.getLocationSkin());
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            Gui.drawScaledCustomSizeModalRect(x, y, 8f, 8f, 8, 8, size, size, 64f, 64f);
        }
    }
}
