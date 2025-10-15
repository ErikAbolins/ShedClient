package Shed.Modules;

import Shed.Notification.NotificationManager;
import lombok.Getter;
import lombok.Setter;
import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Subscriber;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.Minecraft;
import Shed.Shed;
import net.minecraft.client.gui.ScaledResolution;
import org.apache.commons.lang3.Validate;
import Shed.event.impl.Event2D;
import Shed.event.impl.EventKey;
import Shed.event.impl.EventUpdate;

@Getter
public abstract class Module implements Subscriber {
    private final String name, description;
    private final Category category;
    private final boolean enabledByDefault;
    private boolean toggled;

    @Setter
    private int key;

    protected final Minecraft mc = Shed.INSTANCE.getMc();
    protected final FontRenderer fr = Shed.INSTANCE.getFr();

    private final Listener<EventUpdate> eventUpdateListener = new Listener<>(e -> onUpdate());
    private final Listener<Event2D> event2DListener = new Listener<>(e -> on2D(e.getSr()));
    private final Listener<EventKey> eventKeyListener = new Listener<>(e -> onKey(e.getKey()));

    public Module() {
        ModuleInfo info = getClass().getAnnotation(ModuleInfo.class);
        Validate.notNull(info, "Confused annotation exception");

        this.name = info.name();
        this.description = info.description();
        this.category = info.category();
        this.enabledByDefault = info.enabled();

        if (enabledByDefault) toggle();
    }

    public void toggle() {
        setEnabled(!toggled);
    }

    public void setEnabled(boolean state) {
        if(this.toggled == state) return;
        this.toggled = state;

        onToggle();

        if (state) {
            Shed.BUS.subscribe(this);
            Shed.BUS.subscribe(eventUpdateListener);
            Shed.BUS.subscribe(event2DListener);
            Shed.BUS.subscribe(eventKeyListener);
            onEnable();
        } else {
            Shed.BUS.unsubscribe(this);
            Shed.BUS.unsubscribe(eventUpdateListener);
            Shed.BUS.unsubscribe(event2DListener);
            Shed.BUS.unsubscribe(eventKeyListener);
            onDisable();
        }
    }

    public void onToggle() {
        if(mc.theWorld != null) {
            if(!this.getName().equalsIgnoreCase("clickgui")) {
                NotificationManager.addNotification(this.getName(), toggled);
            }
        }
    }

    public void onEnable() {}
    public void onDisable() {}
    public void onUpdate() {}
    public void on2D(ScaledResolution sr) {}
    public void onKey(int key) {}
}