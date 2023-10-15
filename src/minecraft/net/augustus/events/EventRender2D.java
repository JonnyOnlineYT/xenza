package net.augustus.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EventRender2D extends Event{
    private int scaledX;
    private int scaledY;
}
