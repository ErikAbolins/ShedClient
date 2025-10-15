package Shed.UI.MainMenu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;

public class MenuButton extends GuiButton {
    public MenuButton(int buttonID, int x, int y, int widthIn, int heightIn, String buttonText) {
        super(buttonID, x, y, widthIn, heightIn, buttonText);
    }
    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if(visible) {
            boolean hovered = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height;
            int primaryColor = 0xDD222222;
            int backgroundColor = hovered ? 0xDD333333 : primaryColor;
            drawRect(xPosition, yPosition, xPosition + width, yPosition + height, backgroundColor);

            GlStateManager.pushMatrix();
            GlStateManager.scale(1.2f, 1.2f, 1.2f);
            int textColor = -1;
            int textX = (int) ((xPosition + (float) width / 2) / 1.2f);
            int textY = (int) ((yPosition + (float) height / 8) / 1.2f);
            GlStateManager.popMatrix();
        }
    }
}
