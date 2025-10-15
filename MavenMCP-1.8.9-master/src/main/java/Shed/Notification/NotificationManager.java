package Shed.Notification;

import java.util.ArrayList;
import java.util.List;

public class NotificationManager {
    private static final List<Notification> notifications = new ArrayList<>();

    public static void addNotification(String moduleName, boolean enabled) {
        notifications.add(new Notification(moduleName, enabled));
    }

    public static void renderNotifications() {
        int yOffset = 0;
        for(int i = 0; i < notifications.size(); i++) {
            Notification notification = notifications.get(i);
            notification.draw(yOffset);
            yOffset += 30;

            if(notification.shouldRemove()) {
                notifications.remove(i);
                i--;
            }
        }
    }
}
