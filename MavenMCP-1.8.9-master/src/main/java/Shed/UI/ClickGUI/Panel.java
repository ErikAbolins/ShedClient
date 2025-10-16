package Shed.UI.ClickGUI;

import Shed.Modules.Module;
import Shed.Modules.Category;
import Shed.Shed;
import Shed.util.RenderUtil;
import net.minecraft.client.gui.FontRenderer;
import org.lwjgl.input.Mouse;

import java.util.HashMap;
import java.util.Map;

public class Panel {
    public float x, y;
    public Category category;
    public boolean dragging;
    private float startX, startY, lastX, lastY;
    private float scroll = 0;

    private final FontRenderer fr = Shed.INSTANCE.getFr();
    private final Map<Module, SettingPanel> settingPanels = new HashMap<>();

    public Panel(float x, float y, Category category) {
        this.x = x;
        this.y = y;
        this.category = category;
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0 && isHovering(x, y, 100, 25, mouseX, mouseY)) {
            startX = mouseX;
            startY = mouseY;
            lastX = x;
            lastY = y;
            dragging = true;
        }

        for(SettingPanel sp : settingPanels.values()) {
            if(sp.isOpen()) {
                sp.mouseClicked(mouseX, mouseY, mouseButton);
            }
        }


        // Click modules
        int count = 0;
        for (Module m : Shed.INSTANCE.getModuleManager().getModules(category)) {
            float moduleY = y + 25 + count * 15 - scroll;
            if (isHovering(x, moduleY, 100, 15, mouseX, mouseY)) {
                if (mouseButton == 0) m.toggle();
                else if (mouseButton == 1) {
                    settingPanels.computeIfAbsent(m, k -> new SettingPanel(m, fr)).toggleOpen();
                }
            }
            count++;
        }
    }

    public void mouseReleased(int mouseX, int mouseY, int state) {
        dragging = false;
    }

    public void drawPanel(int mouseX, int mouseY) {
        // Handle dragging
        if (dragging) {
            x = lastX + mouseX - startX;
            y = lastY + mouseY - startY;
        }

        // Draw panel background and header
        RenderUtil.drawRect((int)x, (int)y, 100, 150, 0xFF2A2A2A);
        RenderUtil.drawRect((int)x, (int)y, 100, 25, 0xFF1A1A1A);
        fr.drawString(category.name(), (int)x + 5, (int)y + 8, -1);

        // Draw modules
        Module[] modules = Shed.INSTANCE.getModuleManager().getModules(category);
        int count = 0;
        for (Module m : modules) {
            float moduleY = y + 25 + count * 15 - scroll;
            if (moduleY >= y + 25 && moduleY < y + 175) {
                int color = m.isToggled() ? 0xFF2AA5FF : 0xFF555555;
                RenderUtil.drawRect((int)x, (int)moduleY, 100, 15, color);
                fr.drawString(m.getName(), (int)x + 5, (int)moduleY + 4, -1);
            }
            count++;
        }

        // Draw all open SettingPanels on top AFTER panels/modules
        for (Module m : modules) {
            SettingPanel sp = settingPanels.get(m);
            if (sp != null && sp.isOpen()) {
                float moduleY = y + 25 + count * 15 - scroll;
                sp.draw(x, y + 25, mouseX, mouseY); // make sure to pass panel x and module y
            }
        }

        // Scroll
        if (Mouse.hasWheel() && isHovering(x, y, 100, 150, mouseX, mouseY)) {
            int wheel = Mouse.getDWheel();
            scroll -= wheel / 5f;
            float maxScroll = Math.max(0, count * 15 - 125);
            if (scroll < 0) scroll = 0;
            if (scroll > maxScroll) scroll = maxScroll;
        }
    }


    private boolean isHovering(float x, float y, float w, float h, int mx, int my) {
        return mx >= x && my >= y && mx <= x + w && my <= y + h;
    }
}
