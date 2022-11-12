package xyz.dynxsty.dih4jda.events;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.AutoCompleteQuery;
import net.dv8tion.jda.api.interactions.commands.CommandAutoCompleteInteraction;
import xyz.dynxsty.dih4jda.DIH4JDA;
import xyz.dynxsty.dih4jda.interactions.AutoCompletable;

/**
 * An event that gets fired when an exception gets raised while handling an autocomplete interaction.
 *
 * @see AutoCompletable#handleAutoComplete(CommandAutoCompleteInteractionEvent, AutoCompleteQuery)
 */
public class AutoCompleteExceptionEvent extends DIH4JDAThrowableEvent<CommandAutoCompleteInteraction> {
	public AutoCompleteExceptionEvent(DIH4JDA dih4jda, CommandAutoCompleteInteraction interaction, Throwable throwable) {
		super("onAutoCompleteException", dih4jda, interaction, throwable);
	}
}
