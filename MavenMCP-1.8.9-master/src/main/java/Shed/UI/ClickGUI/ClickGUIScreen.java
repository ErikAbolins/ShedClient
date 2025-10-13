package Shed.UI.ClickGUI;

import Shed.Modules.Category;
import Shed.Shed;
import net.minecraft.client.gui.GuiScreen;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClickGUIScreen extends GuiScreen {

    private List<Panel> panels = new ArrayList<>();

    public ClickGUIScreen() {
        // Initialize panels for each category
        int x = 50;
        int y = 50;
        for(Category category : Category.values()) {
            panels.add(new Panel(x, y, category));
            x += 120; // Space panels horizontally
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // Draw all panels
        for(Panel panel : panels) {
            panel.drawPanel(mouseX, mouseY);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        // Pass clicks to panels
        for(Panel panel : panels) {
            panel.mouseClicked(mouseX, mouseY, mouseButton);
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        // Pass mouse release to panels
        for(Panel panel : panels) {
            panel.mouseReleased(mouseX, mouseY, state);
        }

        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false; // Don't pause game when GUI is open
    }
}