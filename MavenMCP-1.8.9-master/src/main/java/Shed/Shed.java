package Shed;

import Shed.Modules.ModuleManager;
import lombok.Getter;
import me.zero.alpine.bus.EventBus;
import me.zero.alpine.bus.EventManager;
import me.zero.alpine.listener.Subscribe;
import me.zero.alpine.listener.Subscriber;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import Shed.event.impl.Event2D;

@Getter
public enum Shed implements Subscriber {
    INSTANCE;

    private final Minecraft mc = Minecraft.getMinecraft();
    private final FontRenderer fr = mc.fontRendererObj;

    private ModuleManager moduleManager;

    public static final EventBus BUS = EventManager.builder()
            .setName("root/Shed")
            .setSuperListeners()
            .build();

    public void init(){
        BUS.subscribe(this);
        Display.setTitle("Shed Client v0.0.1");

        moduleManager = new ModuleManager();
    }

    public void shutdown(){
        BUS.unsubscribe(this);
    }
}
