package com.dynxsty.dih4jda.interactions.autocomplete;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.AutoCompleteQuery;

//TODO-v1.4: Documentation
public interface AutoCompleteHandler {
	void handleAutoComplete(CommandAutoCompleteInteractionEvent event, AutoCompleteQuery target);
}
