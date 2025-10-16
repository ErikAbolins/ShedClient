package Shed.Modules.impl.render;

import Shed.Modules.Category;
import Shed.Modules.Module;
import Shed.Modules.ModuleInfo;
import Shed.Setting.impl.BooleanSetting;
import Shed.Setting.impl.NumberSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

import java.awt.*;

@ModuleInfo(
        name = "Charms",
        description = "",
        category = Category.RENDER
)

public class Charms extends Module {

    private static BooleanSetting playersOnly;
    private static BooleanSetting chroma;
    private static NumberSetting red;
    private static NumberSetting blue;
    private static NumberSetting green;

    public static boolean enabled;

    public Charms() {
        addSetting(
                playersOnly = new BooleanSetting("Players Only", true),
                chroma = new BooleanSetting("Chroma", false),
                red = new NumberSetting("Red", 0.6, 0.0, 1.0, 0.01),
                green = new NumberSetting("Green", 1.0, 0.0, 1.0, 0.01),
                blue = new NumberSetting("Blue", 0.4, 0.0, 1.0, 0.01)
        );
    }

    @Override
    public void onEnable() {
        enabled = true;
    }
    @Override
    public void onDisable() {
        enabled = false;
    }

    public static void preRender(Entity entity) {
        if(!enabled) return;
        if (entity == null) return;
        if (playersOnly.isEnabled() && !(entity instanceof EntityPlayer)) return;

        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
        GL11.glPolygonOffset(1.0f, -1100000f);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        Color color = getCharmsColor();
        GL11.glColor4f(color.getRed() / 255, color.getGreen() / 255, color.getBlue() / 255, color.getAlpha() / 255);
    }

    public static void postRender(Entity entity) {
        if(!enabled) return;
        if (entity == null) return;
        if (playersOnly.isEnabled() && !(entity instanceof EntityPlayer)) return;

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
        GL11.glPopMatrix();
    }


    private static Color getCharmsColor() {
        if (chroma.isEnabled()){
            float hue = (System.currentTimeMillis()/10000L) / 10000f;
            return Color.getHSBColor(hue, 1.0f, 1.0f);
        } else {
            return new Color((float) red.getVal(), (float) green.getVal(), (float) blue.getVal());
        }
    }
}
