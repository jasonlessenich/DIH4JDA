package xyz.dynxsty.dih4jda.events;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import xyz.dynxsty.dih4jda.DIH4JDA;
import xyz.dynxsty.dih4jda.interactions.commands.application.ContextCommand;
import xyz.dynxsty.dih4jda.interactions.commands.application.SlashCommand;

/**
 * An event that gets fired when an exception gets raised while executing any command.
 *
 * @see SlashCommand#execute(SlashCommandInteractionEvent)
 * @see SlashCommand.Subcommand#execute(SlashCommandInteractionEvent)
 * @see ContextCommand.User#execute(Object)
 * @see ContextCommand.Message#execute(Object)
 */
public class CommandExceptionEvent extends DIH4JDAThrowableEvent<CommandInteraction> {
	/**
	 * Creates a new instance of this event.
	 *
	 * @param dih4jda the {@link DIH4JDA} instance that fired this event.
	 * @param interaction the {@link CommandInteraction}.
	 * @param throwable the {@link Throwable} that was thrown.
	 */
	public CommandExceptionEvent(DIH4JDA dih4jda, CommandInteraction interaction, Throwable throwable) {
		super("onCommandException", dih4jda, interaction, throwable);
	}
}
