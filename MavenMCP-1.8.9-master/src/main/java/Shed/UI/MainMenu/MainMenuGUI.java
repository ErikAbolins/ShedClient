package Shed.UI.MainMenu;

import Shed.Shed;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;

public class MainMenuGUI extends GuiScreen {

    private float animatedMouseX = 0;
    private float animatedMouseY = 0;

    private float zoom1 = 1, zoom2 = 1, zoom3 = 1;

    private final int bgWidth = 3841;
    private final int bgHeight = 1194;

    @Override
    public void initGui() {
        // No traditional buttons - using custom icon buttons
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ScaledResolution sr = new ScaledResolution(mc);

        // Draw parallax background
        drawBackground(sr);

        // Draw logo
        drawLogo(sr);

        // Draw button icons
        drawButtons(sr, mouseX, mouseY);

        // Draw credits
        drawCredits(sr);

        // Smooth mouse animation
        animatedMouseX += ((mouseX - animatedMouseX) / 1.8f) + 0.1f;
        animatedMouseY += ((mouseY - animatedMouseY) / 1.8f) + 0.1f;

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawBackground(ScaledResolution sr) {
        mc.getTextureManager().bindTexture(new ResourceLocation("shed/textures/mainmenubg.png"));
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        int screenWidth = sr.getScaledWidth();
        int screenHeight = sr.getScaledHeight();

        drawModalRectWithCustomSizedTexture(
                0, 0,
                0, 0,
                screenWidth, screenHeight,
                screenWidth, screenHeight
        );
    }


    private void drawLogo(ScaledResolution sr) {
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.enableAlpha();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        mc.getTextureManager().bindTexture(new ResourceLocation("shed/textures/logo.png"));

        float logoWidth = (323 / 2f) * 4.0f;
        float logoHeight = (161 / 2f) * 4.0f;
        float x = sr.getScaledWidth() / 2f - logoWidth / 2f;
        float y = sr.getScaledHeight() / 2f - logoHeight - 50;

        drawModalRectWithCustomSizedTexture((int)x, (int)y, 0, 0, (int)logoWidth, (int)logoHeight, (int)logoWidth, (int)logoHeight);
    }


    private void drawButtons(ScaledResolution sr, int mouseX, int mouseY) {
        float baseOffset = sr.getScaledWidth()/2 - 289/2f + 8 - 16;
        float height = sr.getScaledHeight()/2 + 29/2f - 8 + 0.5f;
        float spacing = 122/2f;

        // Singleplayer
        drawIconButton(baseOffset, height, "singleplayer", mouseX, mouseY, 1);

        // Multiplayer
        drawIconButton(baseOffset + spacing, height, "multiplayer", mouseX, mouseY, 2);

        // Settings
        drawIconButton(baseOffset + spacing*2, height, "settings", mouseX, mouseY, 3);
    }

    private void drawIconButton(float x, float y, String iconName, int mouseX, int mouseY, int buttonId) {
        boolean hovered = isMouseHovering(x + 4, y + 4, 64-8, 64-8, mouseX, mouseY);

        // Get zoom for this button
        float zoom = getZoom(buttonId);

        // Update zoom
        if(hovered && zoom < 1.2f) {
            setZoom(buttonId, Math.min(1.2f, zoom + 0.05f));
        } else if(!hovered && zoom > 1.0f) {
            setZoom(buttonId, Math.max(1.0f, zoom - 0.067f));
        }

        GlStateManager.pushMatrix();

        // Apply zoom from center
        if(zoom > 1.0f) {
            GlStateManager.translate(x + 32, y + 32, 0);
            GlStateManager.scale(zoom, zoom, 1);
            GlStateManager.translate(-(x + 32), -(y + 32), 0);
        }

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.enableAlpha();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);


        mc.getTextureManager().bindTexture(new ResourceLocation("shed/textures/" + iconName + ".png"));
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        drawModalRectWithCustomSizedTexture((int)x, (int)y, 0, 0, 64, 64, 64, 64);

        GlStateManager.popMatrix();

        // Draw label on hover
        if(zoom > 1.0f) {
            GlStateManager.pushMatrix();
            String label = getButtonLabel(iconName);
            float labelX = x + 32 - fontRendererObj.getStringWidth(label)/2f;
            float labelY = y + 70;
            float alpha = Math.max(0, Math.min(1, 0.5f + (zoom-1)*2.5f));

            fontRendererObj.drawStringWithShadow(label, labelX, labelY,
                    new Color(0.4f, 0.4f, 0.4f, alpha).getRGB());

            GlStateManager.popMatrix();
        }
    }

    private void drawCredits(ScaledResolution sr) {
        String leftText = "Shed Client v0.0.1";
        String rightText = "Minecraft 1.8.9";

        fontRendererObj.drawStringWithShadow(leftText, 5, sr.getScaledHeight() - 15, -1);
        fontRendererObj.drawStringWithShadow(rightText,
                sr.getScaledWidth() - fontRendererObj.getStringWidth(rightText) - 5,
                sr.getScaledHeight() - 15, -1);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if(mouseButton != 0) return;

        ScaledResolution sr = new ScaledResolution(mc);
        float baseOffset = sr.getScaledWidth()/2 - 289/2f + 8 - 16;
        float height = sr.getScaledHeight()/2 + 29/2f - 8 + 0.5f;
        float spacing = 122/2f;

        // Check which button was clicked
        if(isMouseHovering(baseOffset + 4, height + 4, 56, 56, mouseX, mouseY)) {
            mc.displayGuiScreen(new GuiSelectWorld(this));
        } else if(isMouseHovering(baseOffset + spacing + 4, height + 4, 56, 56, mouseX, mouseY)) {
            mc.displayGuiScreen(new GuiMultiplayer(this));
        } else if(isMouseHovering(baseOffset + spacing*2 + 4, height + 4, 56, 56, mouseX, mouseY)) {
            mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings));
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    private boolean isMouseHovering(float x, float y, float w, float h, int mx, int my) {
        return mx >= x && my >= y && mx <= x + w && my <= y + h;
    }

    private float getZoom(int buttonId) {
        switch(buttonId) {
            case 1: return zoom1;
            case 2: return zoom2;
            case 3: return zoom3;
            default: return 1.0f;
        }
    }

    private void setZoom(int buttonId, float value) {
        switch(buttonId) {
            case 1: zoom1 = value; break;
            case 2: zoom2 = value; break;
            case 3: zoom3 = value; break;
        }
    }

    private String getButtonLabel(String iconName) {
        switch(iconName) {
            case "singleplayer": return "Singleplayer";
            case "multiplayer": return "Multiplayer";
            case "settings": return "Settings";
            default: return "";
        }
    }
}