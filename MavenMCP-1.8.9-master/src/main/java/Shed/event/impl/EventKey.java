package Shed.event.impl;

import Shed.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor

public class EventKey extends Event{
    public final int key;
}
