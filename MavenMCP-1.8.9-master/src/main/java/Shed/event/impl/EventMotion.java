package Shed.event.impl;

import net.minecraft.util.Vec3;
import Shed.event.Event;

public class EventMotion extends Event {
    public enum Type { PRE, POST }

    private Type type;
    private double posX, posY, posZ;
    private float yaw, pitch;
    private boolean onGround;

    public EventMotion(Type type, double x, double y, double z, float yaw, float pitch, boolean onGround) {
        this.type = type;
        this.posX = x;
        this.posY = y;
        this.posZ = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
    }

    public Type getType() { return type; }
    public double getX() { return posX; }
    public double getY() { return posY; }
    public double getZ() { return posZ; }
    public float getYaw() { return yaw; }
    public float getPitch() { return pitch; }
    public boolean isOnGround() { return onGround; }

    public void setYaw(float yaw) { this.yaw = yaw; }
    public void setPitch(float pitch) { this.pitch = pitch; }
    public void setOnGround(boolean onGround) { this.onGround = onGround; }
}
