package xyz.dynxsty.dih4jda.events;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.AutoCompleteQuery;
import net.dv8tion.jda.api.interactions.commands.CommandAutoCompleteInteraction;
import xyz.dynxsty.dih4jda.DIH4JDA;
import xyz.dynxsty.dih4jda.interactions.AutoCompletable;

import javax.annotation.Nonnull;

/**
 * An event that gets fired when an exception gets raised while handling an autocomplete interaction.
 *
 * @see AutoCompletable#handleAutoComplete(CommandAutoCompleteInteractionEvent, AutoCompleteQuery)
 */
public class AutoCompleteExceptionEvent extends DIH4JDAThrowableEvent<CommandAutoCompleteInteraction> {

	/**
	 * Creates an exception event object.
	 *
	 * @param dih4jda the {@link DIH4JDA} instance that fired the event.
	 * @param interaction the {@link CommandAutoCompleteInteraction}.
	 * @param throwable the {@link Throwable} that was thrown.
	 */
	public AutoCompleteExceptionEvent(@Nonnull DIH4JDA dih4jda, @Nonnull CommandAutoCompleteInteraction interaction,
									  @Nonnull Throwable throwable) {
		super("onAutoCompleteException", dih4jda, interaction, throwable);
	}
}
