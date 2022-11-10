package xyz.dynxsty.dih4jda.events;

import net.dv8tion.jda.api.interactions.commands.CommandAutoCompleteInteraction;
import xyz.dynxsty.dih4jda.DIH4JDA;

public class AutoCompleteExceptionEvent extends DIH4JDAExceptionEvent<CommandAutoCompleteInteraction> {
	public AutoCompleteExceptionEvent(DIH4JDA dih4jda, CommandAutoCompleteInteraction interaction, Throwable throwable) {
		super("onAutoCompleteException", dih4jda, interaction, throwable);
	}
}
