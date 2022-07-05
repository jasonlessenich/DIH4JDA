package com.dynxsty.dih4jda.events;

import com.dynxsty.dih4jda.DIH4JDALogger;
import com.dynxsty.dih4jda.interactions.commands.AutoCompletable;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.interactions.AutoCompleteQuery;
import net.dv8tion.jda.api.interactions.ModalInteraction;
import net.dv8tion.jda.api.interactions.commands.CommandAutoCompleteInteraction;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.components.ComponentInteraction;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.Set;

public abstract class DIH4JDAListenerAdapter {

	/**
	 * An Event that gets fired when an exception gets raised while executing any Command.
	 *
	 * @param interaction The {@link CommandInteraction}.
	 * @param e           The Exception that was raised.
	 * @see com.dynxsty.dih4jda.interactions.commands.SlashCommand#execute(SlashCommandInteractionEvent)
	 * @see com.dynxsty.dih4jda.interactions.commands.SlashCommand.Subcommand#execute(SlashCommandInteractionEvent)
	 * @see com.dynxsty.dih4jda.interactions.commands.ContextCommand.User#execute(UserContextInteractionEvent)
	 * @see com.dynxsty.dih4jda.interactions.commands.ContextCommand.Message#execute(MessageContextInteractionEvent)
	 */
	public void onCommandException(CommandInteraction interaction, Exception e) {}

	/**
	 * An Event that gets fired when an exception gets raised while interacting with a message component.
	 *
	 * @param interaction The {@link ComponentInteraction}.
	 * @param e           The Exception that was raised.
	 */
	public void onComponentException(ComponentInteraction interaction, Exception e) {}

	/**
	 * An Event that gets fired when an exception gets raised while handling an AutoComplete interaction.
	 *
	 * @param interaction The {@link CommandAutoCompleteInteraction}.
	 * @param e           The Exception that was raised.
	 * @see AutoCompletable#handleAutoComplete(CommandAutoCompleteInteractionEvent, AutoCompleteQuery)
	 */
	public void onAutoCompleteException(CommandAutoCompleteInteraction interaction, Exception e) {}

	/**
	 * An Event that gets fired when an exception gets raised while handling a Modal interaction.
	 *
	 * @param interaction The {@link ModalInteraction}.
	 * @param e           The Exception that was raised.
	 */
	public void onModalException(ModalInteraction interaction, Exception e) {}

	/**
	 * An Event that gets fired when the user, which invoked the command, does NOT have one of the required Permissions.
	 *
	 * @param interaction The {@link CommandInteraction}.
	 * @param permissions The {@link Set} of {@link Permission}s which are required to run this commands.
	 * @see com.dynxsty.dih4jda.interactions.commands.CommandRequirements#requirePermissions(Permission...)
	 */
	public void onInsufficientPermissions(CommandInteraction interaction, Set<Permission> permissions) {}

	/**
	 * An Event that gets fired when the user, which invoked the command, is NOT allowed to use this command.
	 *
	 * @param interaction The {@link CommandInteraction}.
	 * @param userIds     The {@link Set} of {@link Long}s (user Ids) which are able to use this command.
	 * @see com.dynxsty.dih4jda.interactions.commands.CommandRequirements#requireUsers(Long...)
	 */
	public void onInvalidUser(CommandInteraction interaction, Set<Long> userIds) {}

	/**
	 * An Event that gets fired when the user, which invoked the command, does NOT have the required roles to use this command.
	 *
	 * @param interaction The {@link CommandInteraction}.
	 * @param roleIds     The {@link Set} of {@link Long}s (role Ids) which are able to use this command.
	 * @see com.dynxsty.dih4jda.interactions.commands.CommandRequirements#requireUsers(Long...)
	 */
	public void onInvalidRole(CommandInteraction interaction, Set<Long> roleIds) {}

	/**
	 * Fires an event from the {@link DIH4JDAListenerAdapter}.
	 *
	 * @param listeners A set of all classes that extend the {@link DIH4JDAListenerAdapter}.
	 * @param name      The event's name.
	 * @param args      The event's arguments.
	 * @since v1.5
	 */
	public static void fireEvent(@NotNull Set<DIH4JDAListenerAdapter> listeners, String name, Object... args) {
		if (listeners.isEmpty()) {
			DIH4JDALogger.warn(String.format("%s was fired, but not handled (No listener registered)", name), DIH4JDALogger.Type.EVENT_FIRED);
		}
		for (DIH4JDAListenerAdapter listener : listeners) {
			try {
				for (Method method : listener.getClass().getMethods()) {
					if (method.getName().equals(name)) {
						method.invoke(listener.getClass().getConstructor().newInstance(), args);
					}
				}
			} catch (ReflectiveOperationException e) {
				DIH4JDALogger.error(e.getMessage());
			}
		}
	}
}

