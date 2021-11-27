package com.dynxsty.dih4jda.message.selection_menu;

import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;

import java.util.List;

public interface ISelectionMenu {
    void handleSelectionMenu(SelectionMenuEvent event, List<SelectOption> options);
}
