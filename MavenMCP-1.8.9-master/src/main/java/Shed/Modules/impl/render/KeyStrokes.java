package Shed.Modules.impl.render;

import Shed.Modules.Category;
import Shed.Modules.Module;
import Shed.Modules.ModuleInfo;
import Shed.Setting.impl.BooleanSetting;
import net.minecraft.client.gui.Gui;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.*;


@ModuleInfo(
        name = "KeyStrokes",
        description = " ",
        category = Category.RENDER
)
public class KeyStrokes extends Module {
    private static BooleanSetting showMouse;

    public KeyStrokes() {
        addSetting(
                showMouse = new BooleanSetting("Show Mouse", true)
        );
    }

    public void onRender2D() {
        int x = 10;
        int y = 60;
        int size = 22;
        int gap = 2;

        drawKey("W", x + size + gap, y, size, size, Keyboard.isKeyDown(Keyboard.KEY_W));
        drawKey("A", x, y + size + gap, size, size, Keyboard.isKeyDown(Keyboard.KEY_A));
        drawKey("S", x + size + gap, y + size + gap, size, size, Keyboard.isKeyDown(Keyboard.KEY_S));
        drawKey("D", x + (size + gap) * 2, y + size + gap, size, size, Keyboard.isKeyDown(Keyboard.KEY_D));


        if (showMouse.isEnabled()) {
            drawKey("LMB", x, y + (size + gap) * 2, size + 10, size, Mouse.isButtonDown(0));
            drawKey("RMB", x + size + gap + 10, y + (size + gap) * 2, size + 10, size, Mouse.isButtonDown(1));
        }
    }

    private void drawKey(String label, int x, int y, int w, int h, boolean pressed) {
        int bg = pressed ? new Color(80, 80, 80, 180).getRGB() : new Color(40, 40, 40, 140).getRGB();
        int outline = new Color(0, 0, 0, 200).getRGB();
        int text = pressed ? new Color(255, 255, 255).getRGB() : new Color(200, 200, 200).getRGB();

        Gui.drawRect(x - 1, y - 1, x + w + 1, y + h + 1, outline);
        Gui.drawRect(x, y, x + w, y + h, bg);

        mc.fontRendererObj.drawStringWithShadow(label,
                x + (w / 2f) - (mc.fontRendererObj.getStringWidth(label) / 2f),
                y + (h / 2f) - 4,
                text);
    }


}
