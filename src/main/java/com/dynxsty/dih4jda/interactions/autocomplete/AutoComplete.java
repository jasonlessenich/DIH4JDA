package com.dynxsty.dih4jda.interactions.autocomplete;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

//TODO-v1.4: Documentation
public interface AutoComplete {
	void handleAutoComplete(CommandAutoCompleteInteractionEvent event, OptionMapping target);
}
