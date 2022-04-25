package com.dynxsty.dih4jda.interactions.autocomplete;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

public interface AutoComplete {
	void handleAutoComplete(CommandAutoCompleteInteractionEvent event, OptionMapping target);
}
