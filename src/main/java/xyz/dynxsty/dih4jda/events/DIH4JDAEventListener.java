package xyz.dynxsty.dih4jda.events;

import xyz.dynxsty.dih4jda.interactions.commands.AutoCompletable;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.AutoCompleteQuery;
import net.dv8tion.jda.api.interactions.commands.CommandAutoCompleteInteraction;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.components.ComponentInteraction;
import net.dv8tion.jda.api.interactions.modals.ModalInteraction;
import xyz.dynxsty.dih4jda.interactions.commands.AbstractCommand;
import xyz.dynxsty.dih4jda.interactions.commands.ContextCommand;
import xyz.dynxsty.dih4jda.interactions.commands.SlashCommand;

import java.util.Set;

/**
 * An interface containing all events and their method that DIH4JDA can fire.
 */
public interface DIH4JDAEventListener {

	/**
	 * An Event that gets fired when an exception gets raised while executing any Command.
	 *
	 * @param interaction The {@link CommandInteraction}.
	 * @param e           The Exception that was raised.
	 * @see SlashCommand#execute(SlashCommandInteractionEvent)
	 * @see SlashCommand.Subcommand#execute(SlashCommandInteractionEvent)
	 * @see ContextCommand.User#execute(Object)
	 * @see ContextCommand.Message#execute(Object)
	 */
	default void onCommandException(CommandInteraction interaction, Exception e) {}

	/**
	 * An Event that gets fired when an exception gets raised while interacting with a message component.
	 *
	 * @param interaction The {@link ComponentInteraction}.
	 * @param e           The Exception that was raised.
	 */
	default void onComponentException(ComponentInteraction interaction, Exception e) {}

	/**
	 * An Event that gets fired when an exception gets raised while handling an AutoComplete interaction.
	 *
	 * @param interaction The {@link CommandAutoCompleteInteraction}.
	 * @param e           The Exception that was raised.
	 * @see AutoCompletable#handleAutoComplete(CommandAutoCompleteInteractionEvent, AutoCompleteQuery)
	 */
	default void onAutoCompleteException(CommandAutoCompleteInteraction interaction, Exception e) {}

	/**
	 * An Event that gets fired when an exception gets raised while handling a Modal interaction.
	 *
	 * @param interaction The {@link ModalInteraction}.
	 * @param e           The Exception that was raised.
	 */
	default void onModalException(ModalInteraction interaction, Exception e) {}

	/**
	 * An Event that gets fired when the user, which invoked the command, does NOT have one of the required Permissions.
	 *
	 * @param interaction The {@link CommandInteraction}.
	 * @param permissions The {@link Set} of {@link Permission}s which are required to run this commands.
	 * @see AbstractCommand#setRequiredPermissions(Permission...)
	 */
	default void onInsufficientPermissions(CommandInteraction interaction, Set<Permission> permissions) {}

	/**
	 * An Event that gets fired when the user, which invoked the command, is NOT allowed to use this command.
	 *
	 * @param interaction The {@link CommandInteraction}.
	 * @param userIds     The {@link Set} of {@link Long}s (user Ids) which are able to use this command.
	 * @see AbstractCommand#setRequiredUsers(Long...)
	 */
	default void onInvalidUser(CommandInteraction interaction, Set<Long> userIds) {}

	/**
	 * An Event that gets fired when the user, which invoked the command, does NOT have the required roles to use this command.
	 *
	 * @param interaction The {@link CommandInteraction}.
	 * @param roleIds     The {@link Set} of {@link Long}s (role Ids) which are able to use this command.
	 * @see AbstractCommand#setRequiredRoles(Long...)
	 */
	default void onInvalidRole(CommandInteraction interaction, Set<Long> roleIds) {}
}

