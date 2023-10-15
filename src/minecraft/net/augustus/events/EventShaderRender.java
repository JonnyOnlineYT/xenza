package net.augustus.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class EventShaderRender extends Event {
    private boolean bloom;
}
