package com.dynxsty.dih4jda.message.button;

import com.dynxsty.dih4jda.DIH4JDA;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.ButtonStyle;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ButtonBuilder {

    private final String label;
    private final ButtonStyle buttonStyle;
    private final List<String> params;
    private Class<? extends IButton> handler;
    private boolean disabled;

    public ButtonBuilder(String label, ButtonStyle buttonStyle) {
        params = new ArrayList<>();

        this.label = label;
        this.buttonStyle = buttonStyle;
    }

    public ButtonBuilder setHandler(Class<? extends IButton> handler) {
        this.handler = handler;
        return this;
    }

    public ButtonBuilder setDisabled(boolean disabled) {
        this.disabled = disabled;
        return this;
    }

    public ButtonBuilder addParam(Object param) {
        if (Objects.toString(param).contains(":"))
            throw new IllegalArgumentException("Parameter may not contain colon. (:)");
        this.params.add(Objects.toString(param));
        return this;
    }

    private String getHandlerAsString() {
        return handler.getName().replace(DIH4JDA.commandsPackage, "");
    }

    public Button build() {
        StringBuilder sb = new StringBuilder(getHandlerAsString());
        for (var s : params) {
            sb.append(":").append(s);
        }

        if (sb.toString().length() > 100) {
            throw new IllegalStateException(
                    "Component Id may not be longer than 100 characters! " +
                    "\nLength: " + sb.toString().length() +
                    "\nId: " + sb
            );
        }
        return Button.of(buttonStyle, sb.toString(), label).withDisabled(disabled);
    }
}
