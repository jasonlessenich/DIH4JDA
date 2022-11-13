package xyz.dynxsty.dih4jda.interactions.commands.application;

import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

/**
 * Model class which represents a single Context Command.
 *
 * @param <E> the {@link GenericCommandInteractionEvent} event.
 * @see ContextCommand.User#execute
 * @see ContextCommand.Message#execute
 * @since v1.5
 */
public abstract class ContextCommand<E extends GenericCommandInteractionEvent> extends BaseApplicationCommand<E, CommandData> {

	private ContextCommand() {}

	/**
	 * Creates a new user-context command.
	 */
	public abstract static class User extends ContextCommand<UserContextInteractionEvent> {
		/**
		 * Creates a new, default user command.
		 */
		public User() {}
	}

	/**
	 * Creates a new message-context command.
	 */
	public abstract static class Message extends ContextCommand<MessageContextInteractionEvent> {
		/**
		 * Creates a new, default message command.
		 */
		public Message() {}
	}
}
