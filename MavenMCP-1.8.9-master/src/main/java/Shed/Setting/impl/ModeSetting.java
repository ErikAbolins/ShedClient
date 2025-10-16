package Shed.Setting.impl;

import Shed.Setting.Setting;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

@Getter
public class ModeSetting extends Setting {
    private final List<String> modes;

    @Setter
    private String currMode;
    @Setter
    private int modeIndex;

    public ModeSetting(String name, String... modes) {
        this.name = name;
        this.modes = Arrays.asList(modes);
        this.modeIndex = this.modes.indexOf(modes[0]);
        if(currMode == null) currMode = modes[0];
    }

    public void cycleForwards() {
        modeIndex++;
        if(modeIndex >= modes.size()) modeIndex = 0;
        currMode = modes.get(modeIndex);
    }

    public void cycleBack() {
        modeIndex--;
        if(modeIndex < 0) modeIndex = modes.size() - 1;
        currMode = modes.get(modeIndex);
    }
}
