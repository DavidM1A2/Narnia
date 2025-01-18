package com.dslovikosky.narnia.common.model.scene.goal;

import com.dslovikosky.narnia.common.model.scene.Character;
import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

public class ChatLine {
    private final Supplier<Character> speaker;
    private final Supplier<Character> listener;
    private final Component component;
    private final int durationTicks;

    public ChatLine(final Supplier<Character> speaker, final Component component, final int durationTicks) {
        this(speaker, () -> null, component, durationTicks);
    }

    public ChatLine(final Supplier<Character> speaker, final Component component) {
        this(speaker, () -> null, component, Math.max(20, component.getString().length()));
    }

    public ChatLine(final Supplier<Character> speaker, final Supplier<Character> listener, final Component component) {
        this(speaker, listener, component, Math.max(40, component.getString().length() * 2));
    }

    public ChatLine(final Supplier<Character> speaker, final Supplier<Character> listener, final Component component, final int durationTicks) {
        this.speaker = speaker;
        this.listener = listener;
        this.component = component;
        this.durationTicks = durationTicks;
    }

    public Character getSpeaker() {
        return speaker.get();
    }

    public Character getListener() {
        return listener.get();
    }

    public Component getComponent() {
        return component;
    }

    public int getDurationTicks() {
        return durationTicks;
    }
}
