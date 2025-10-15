package Shed.Notification;

import Shed.util.RenderUtil;
import net.minecraft.client.gui.ScaledResolution;
import Shed.Shed;

public class Notification {
    private final String moduleName;
    private final String status;
    private final long startTime;
    private final long displayTime;

    public Notification(String moduleName, boolean enabled)
    {
        this.moduleName = moduleName;
        this.status = enabled ? "Enabled" : "Disabled";
        this.startTime = System.currentTimeMillis();
        this.displayTime = 2000;
    }


    public boolean shouldRemove()
    {
        return System.currentTimeMillis() - this.startTime >= this.displayTime;
    }

    public void draw(int yOffset)
    {
        ScaledResolution sr = new ScaledResolution(Shed.INSTANCE.getMc());

        long elapsed = System.currentTimeMillis() - startTime;
        double remainingPercentage = (double) elapsed / displayTime;

        int width = 95;
        int height = 18;
        int x = sr.getScaledWidth() - width - 2;
        int y = sr.getScaledHeight() - yOffset - height - 2;

        RenderUtil.drawRect(x, y, x + width, y + height, 0x90000000);
        Shed.INSTANCE.getFr().drawStringWithShadow(moduleName + " " + status, x + 3, y + 5, -1);

        int progressBarWidth = (int) ((width) * (1 - remainingPercentage));
        RenderUtil.drawRect(x, y + height - 2, progressBarWidth, 2, 0xFF48BDFA);
    }
}
