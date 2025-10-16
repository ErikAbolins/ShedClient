package Shed.UI.ClickGUI;

import Shed.Modules.Module;
import Shed.Setting.Setting;
import Shed.Setting.impl.BooleanSetting;
import Shed.Setting.impl.NumberSetting;
import Shed.Setting.impl.ModeSetting;
import Shed.util.RenderUtil;
import net.minecraft.client.gui.FontRenderer;
import org.lwjgl.input.Mouse;

import java.util.List;

public class SettingPanel {

    private final Module module;
    private final FontRenderer fr;
    private float drawX, drawY;
    private boolean open = false;
    private float scroll = 0;

    private boolean draggingNumber = false;
    private NumberSetting activeNumberSetting = null;

    public SettingPanel(Module module, FontRenderer fr) {
        this.module = module;
        this.fr = fr;
    }

    public boolean isOpen() {
        return open;
    }

    public void toggleOpen() {
        open = !open;
    }

    public void draw(float baseX, float baseY, int mouseX, int mouseY) {
        if (!open) return;

        // Position relative to the module
        drawX = baseX + 30; // a little to the right of the module
        drawY = baseY;       // same vertical position as the module

        List<Setting> settings = module.getSettingList();
        if (settings == null || settings.isEmpty()) return;

        int panelWidth = 120;
        int padding = 5;
        int panelHeight = 15 + settings.size() * 20;

        RenderUtil.drawRect((int)drawX, (int)drawY, panelWidth, panelHeight, 0xFF222222);
        fr.drawString("Settings - " + module.getName(), (int)drawX + padding, (int)drawY + 4, -1);

        int offsetY = 20;
        for (Setting s : settings) {
            float sx = drawX + padding;
            float sy = drawY + offsetY;
            int settingHeight = 12;

            if (s instanceof BooleanSetting) {
                BooleanSetting b = (BooleanSetting) s;
                int color = b.isEnabled() ? 0xFF2AA5FF : 0xFF444444;
                RenderUtil.drawRect((int)sx, (int)sy, panelWidth - padding*2, settingHeight, color);
                fr.drawString(b.getName(), (int)sx + 3, (int)sy + 2, -1);
            }

            if (s instanceof NumberSetting) {
                NumberSetting n = (NumberSetting) s;
                RenderUtil.drawRect((int)sx, (int)sy, panelWidth - padding*2, settingHeight, 0xFF555555);
                float percent = (float)((n.getVal() - n.getMinVal()) / (n.getMaxVal() - n.getMinVal()));
                int fillWidth = (int)((panelWidth - padding*2) * percent);
                RenderUtil.drawRect((int)sx, (int)sy, fillWidth, settingHeight, 0xFF2AA5FF);
                fr.drawString(n.getName() + ": " + String.format("%.2f", n.getVal()), (int)sx + 3, (int)sy + 2, -1);
            }

            if (s instanceof ModeSetting) {
                ModeSetting m = (ModeSetting) s;
                RenderUtil.drawRect((int)sx, (int)sy, panelWidth - padding*2, settingHeight, 0xFF444444);
                fr.drawString(m.getName() + ": " + m.getCurrMode(), (int)sx + 3, (int)sy + 2, -1);
            }

            offsetY += 20;
        }
    }




    public void mouseClicked(int mx, int my, int button) {
        if (!open) return;

        int offsetY = 20;
        for (Setting s : module.getSettingList()) {
            float sx = drawX + 5;
            float sy = drawY + offsetY;
            int settingWidth = 110;
            int settingHeight = 12;

            if (s instanceof BooleanSetting && isHovering(sx, sy, settingWidth, settingHeight, mx, my) && button == 0) {
                ((BooleanSetting) s).toggle();
            }

            if (s instanceof ModeSetting && isHovering(sx, sy, settingWidth, settingHeight, mx, my) && button == 0) {
                ((ModeSetting) s).cycleForwards();
            }

            if (s instanceof NumberSetting && isHovering(sx, sy, settingWidth, settingHeight, mx, my) && button == 0) {
                activeNumberSetting = (NumberSetting) s;
                draggingNumber = true;
                updateNumberSetting(mx); // update value immediately on click
            }

            offsetY += 20;
        }
    }

    public void mouseReleased(int mx, int my, int button) {
        draggingNumber = false;
        activeNumberSetting = null;
    }

    public void mouseDragged(int mx, int my) {
        if (draggingNumber && activeNumberSetting != null) {
            updateNumberSetting(mx);
        }
    }

    private void updateNumberSetting(int mouseX) {
        if (activeNumberSetting == null) return;

        float sx = drawX + 5;
        int sliderWidth = 110;
        double percent = (mouseX - sx) / sliderWidth;
        percent = Math.max(0, Math.min(1, percent));
        double value = activeNumberSetting.getMinVal() + percent * (activeNumberSetting.getMaxVal() - activeNumberSetting.getMinVal());
        activeNumberSetting.setVal(value);
    }


    private boolean isHovering(float x, float y, float w, float h, int mx, int my) {
        return mx >= x && my >= y && mx <= x + w && my <= y + h;
    }
}
