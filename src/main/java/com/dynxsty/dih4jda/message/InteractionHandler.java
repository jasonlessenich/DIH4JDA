package com.dynxsty.dih4jda.message;

import com.dynxsty.dih4jda.DIH4JDA;
import com.dynxsty.dih4jda.message.button.IButton;
import com.dynxsty.dih4jda.message.selection_menu.ISelectionMenu;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

@Slf4j
public class InteractionHandler extends ListenerAdapter {

    @Override
    public void onButtonClick(ButtonClickEvent event) {
        var id = event.getComponentId().split(":");
        IButton handler = (IButton) getHandlerByName(id[0]);
        if (handler == null) return;
        handler.handleButton(event, Arrays.copyOfRange(id, 1, id.length));
    }

    @Override
    public void onSelectionMenu(SelectionMenuEvent event) {
        var id = event.getComponentId().split(":");
        ISelectionMenu handler = (ISelectionMenu) getHandlerByName(id[0]);
        if (handler == null) return;
        handler.handleSelectionMenu(event, event.getSelectedOptions());
    }

    private Object getHandlerByName(String name) {
        try {
            return Class.forName(DIH4JDA.commandsPackage + name)
                    .getDeclaredConstructor()
                    .newInstance();
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException |
                IllegalAccessException | InvocationTargetException ignored) {
            return null;
        }
    }
}
