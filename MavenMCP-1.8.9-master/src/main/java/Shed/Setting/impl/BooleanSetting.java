package Shed.Setting.impl;

import Shed.Setting.Setting;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class BooleanSetting extends Setting {
    private boolean state;

    public BooleanSetting(String name, boolean state) {
        this.name = name;
        this.state = state;
    }

    public boolean isEnabled() {
        return state;
    }

    public void toggle() {
        setState(!isEnabled());
    }
}
