package Shed.Modules.impl.render;

import Shed.Modules.Category;
import Shed.Modules.Module;
import Shed.Modules.ModuleInfo;
import Shed.Shed;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ModuleInfo(
        name = "HUD",
        description = "Displays client HUD",
        category = Category.RENDER,
        enabled = true
)
public class HUD extends Module {

    @Override
    public void on2D(ScaledResolution sr) {
        renderWatermark();
        renderInfo();
        renderArraylist(sr);
    }

    private void renderWatermark() {
        GL11.glPushMatrix();
        GL11.glScalef(1.75f, 1.75f, 1.75f);
        fr.drawStringWithShadow("Shed Client v0.0.1", 2, 2, 0xFFFFFF);
        GL11.glPopMatrix();
    }

    private void renderInfo() {
        // FPS
        int fps = mc.getDebugFPS();
        fr.drawStringWithShadow("FPS: " + fps, 2, 20, fps > 60 ? 0x00FF00 : 0xFF0000);

        // Coordinates (only if player exists)
        if(mc.thePlayer != null) {
            String coords = String.format("XYZ: %.1f, %.1f, %.1f",
                    mc.thePlayer.posX,
                    mc.thePlayer.posY,
                    mc.thePlayer.posZ
            );
            fr.drawStringWithShadow(coords, 2, 30, 0xFFFF00);
        }
    }

    private void renderArraylist(ScaledResolution sr) {
        // Get all enabled modules except HUD itself
        List<Module> enabledModules = new ArrayList<>();
        for(Module m : Shed.INSTANCE.getModuleManager().getModules().values()) {
            if(m.isToggled() && m != this) { // Don't show HUD in the arraylist
                enabledModules.add(m);
            }
        }

        // Sort by name width (longest first)
        Collections.sort(enabledModules, (m1, m2) ->
                Integer.compare(
                        fr.getStringWidth(m2.getName()),
                        fr.getStringWidth(m1.getName())
                )
        );

        // Render modules
        float yOffset = 2;
        for(Module module : enabledModules) {
            String name = module.getName();
            int width = fr.getStringWidth(name);

            // Right align
            float xPos = sr.getScaledWidth() - width - 4;

            // Draw background
            int bgColor = 0x90000000; // Semi-transparent black
            drawRect((int)xPos - 2, (int)yOffset, sr.getScaledWidth(), (int)yOffset + 10, bgColor);

            // Draw module name
            fr.drawStringWithShadow(name, xPos, yOffset + 1, 0xFF6B6BFF); // Purple

            yOffset += 11;
        }
    }

    // Helper method for drawing rectangles
    private void drawRect(int left, int top, int right, int bottom, int color) {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        float a = (float)(color >> 24 & 255) / 255.0F;
        float r = (float)(color >> 16 & 255) / 255.0F;
        float g = (float)(color >> 8 & 255) / 255.0F;
        float b = (float)(color & 255) / 255.0F;

        GL11.glColor4f(r, g, b, a);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2d(left, bottom);
        GL11.glVertex2d(right, bottom);
        GL11.glVertex2d(right, top);
        GL11.glVertex2d(left, top);
        GL11.glEnd();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }
}