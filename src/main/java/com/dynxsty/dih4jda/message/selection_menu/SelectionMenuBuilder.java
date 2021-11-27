package com.dynxsty.dih4jda.message.selection_menu;

import com.dynxsty.dih4jda.DIH4JDA;
import com.dynxsty.dih4jda.message.button.IButton;
import net.dv8tion.jda.api.interactions.components.ButtonStyle;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SelectionMenuBuilder {

    private final List<SelectOption> options;
    private String placeholder;
    private int min, max;
    private ButtonStyle buttonStyle;
    private Class<? extends IButton> handler;
    private boolean disabled;

    public SelectionMenuBuilder() {
        options = new ArrayList<>();
    }

    public SelectionMenuBuilder setHandler(Class<? extends IButton> handler) {
        this.handler = handler;
        return this;
    }

    public SelectionMenuBuilder setDisabled(boolean disabled) {
        this.disabled = disabled;
        return this;
    }

    public SelectionMenuBuilder setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        return this;
    }

    public SelectionMenuBuilder setMin(int min) {
        this.min = min;
        return this;
    }

    public SelectionMenuBuilder setMax(int max) {
        this.max = max;
        return this;
    }

    public SelectionMenuBuilder addOptions(SelectOption... options) {
        Collections.addAll(this.options, options);
        return this;
    }

    private String getHandlerAsString() {
        return handler.getName().replace(DIH4JDA.commandsPackage, "");
    }

    public SelectionMenu build() {
        if (options.isEmpty()) throw new IllegalStateException("Selection Menu options may not be empty!");
        if (min > max) throw new IllegalArgumentException("Min Value may not be greater than maxValue!");
        var builder = SelectionMenu.create(handler.getName())
                .setMinValues(min)
                .setMaxValues(Math.min(max, options.size())) // maxValue cannot be greater than the provided amount of options
                .addOptions(options)
                .setDisabled(disabled);
        if (placeholder != null && placeholder.length() > 0) builder.setPlaceholder(placeholder);
        return builder.build();
    }
}
