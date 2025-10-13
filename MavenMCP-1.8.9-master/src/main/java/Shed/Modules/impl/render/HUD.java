package Shed.Modules.impl.render;

import Shed.Modules.Category;
import Shed.Modules.Module;
import Shed.Modules.ModuleInfo;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;


@ModuleInfo(
        name = "HUD",
        category = Category.RENDER,
        enabled = true
)

public class HUD extends Module {
    @Override
    public void on2D(ScaledResolution sr) {
        GL11.glPushMatrix();
        GL11.glScalef(1.75f, 1.75f, 1.75f);
        fr.drawString("Shed Client", 1, 1, -1);
        GL11.glPopMatrix();

    }
}
