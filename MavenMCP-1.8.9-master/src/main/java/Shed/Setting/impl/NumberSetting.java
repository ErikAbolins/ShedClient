package Shed.Setting.impl;

import Shed.Setting.Setting;
import lombok.Getter;
import lombok.Setter;

@Getter
public class NumberSetting extends Setting {
    @Setter
    private double val, minVal, maxVal, defaultVal, increment;

    public NumberSetting(String name, double defaultVal, double minVal, double maxVal, double increment) {
        this.name = name;
        this.defaultVal = defaultVal;
        this.minVal = minVal;
        this.maxVal = maxVal;
        this.increment = increment;
        this.val = defaultVal;
    }

    public void setVal(double val) {
        this.val = clamp(val, minVal, maxVal);
    }

    private double clamp(double val, double minVal, double maxVal) {
        return Math.min(maxVal, Math.max(minVal, val));
    }
}
