package Shed.UI.ClickGUI;

import Shed.Modules.Category;
import Shed.Modules.Module;
import Shed.Shed;
import Shed.util.RenderUtil;
import net.minecraft.client.gui.FontRenderer;
import org.lwjgl.input.Mouse;
import java.awt.Color;

public class Panel {
    public float x, y;
    public Category category;
    public boolean dragging;
    private float startX, startY, lastX, lastY;
    private float scroll = 0;

    private final FontRenderer fr = Shed.INSTANCE.getFr();

    public Panel(float x, float y, Category category) {
        this.x = x;
        this.y = y;
        this.category = category;
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        // Drag panel by header
        if(mouseButton == 0 && isHovering(x, y, 100, 25, mouseX, mouseY)) {
            startX = mouseX;
            startY = mouseY;
            lastX = x;
            lastY = y;
            dragging = true;
        }

        // Click modules
        int count = 0;
        for(Module m : Shed.INSTANCE.getModuleManager().getModules(category)) {
            if(isHovering(x, y + 25 + count*15 - scroll, 100, 15, mouseX, mouseY)) {
                m.toggle();
            }
            count++;
        }


    }

    public void mouseReleased(int mouseX, int mouseY, int state) {
        dragging = false;
    }

    public void drawPanel(int mouseX, int mouseY) {
        // Handle dragging
        if(dragging) {
            x = lastX + mouseX - startX;
            y = lastY + mouseY - startY;
        }

        // Draw panel background
        RenderUtil.drawRect((int)x, (int)y, 100, 150, 0xFF2A2A2A);

        // Draw header
        RenderUtil.drawRect((int)x, (int)y, 100, 25, 0xFF1A1A1A);
        fr.drawString(category.name(), (int)x + 5, (int)y + 8, -1);

        // Draw modules with scissor clipping
        // (Add GL11 scissor stuff here like your original GUI had)

        Module[] modules = Shed.INSTANCE.getModuleManager().getModules(category);
        int count = 0;
        for(Module m : modules) {
            float moduleY = y + 25 + count*15 - scroll;






           if(moduleY >= y + 25 && moduleY < y + 175) {
                int color = m.isToggled() ? 0xFF2AA5FF : 0xFF555555;
                RenderUtil.drawRect((int)x, (int)moduleY, 100, 15, color);
                fr.drawString(m.getName(), (int)x + 5, (int)moduleY + 4, -1);
            }
            count++;
        }

        // Handle scroll
        if(Mouse.hasWheel() && isHovering(x, y, 100, 150, mouseX, mouseY)) {
            int wheel = Mouse.getDWheel();
            if(wheel < 0) scroll += 15;
            if(wheel > 0) scroll -= 15;

            // Clamp scroll
            float maxScroll = Math.max(0, count * 15 - 125);
            if(scroll < 0) scroll = 0;
            if(scroll > maxScroll) scroll = maxScroll;
        }
    }

    private boolean isHovering(float x, float y, float w, float h, int mx, int my) {
        return mx >= x && my >= y && mx <= x + w && my <= y + h;
    }
}