package com.dynxsty.dih4jda.interactions.components.select_menu;

import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;

import java.util.List;

public interface SelectMenu {
	void handleSelectMenu(SelectMenuInteractionEvent event, List<String> values);
}
