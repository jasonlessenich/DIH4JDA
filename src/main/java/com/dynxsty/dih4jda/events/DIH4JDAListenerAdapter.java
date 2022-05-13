package com.dynxsty.dih4jda.events;

import com.dynxsty.dih4jda.interactions.commands.AutoCompleteHandler;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.interactions.AutoCompleteQuery;
import net.dv8tion.jda.api.interactions.ModalInteraction;
import net.dv8tion.jda.api.interactions.commands.CommandAutoCompleteInteraction;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.components.ComponentInteraction;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.List;
import java.util.Set;

public abstract class DIH4JDAListenerAdapter {

	// Exceptions

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
	public void onCommandException(CommandInteraction interaction, Exception e) {
	}

	/**
	 * An Event that gets fired when an exception gets raised while interacting with a message component.
	 *
	 * @param interaction The {@link ComponentInteraction}.
	 * @param e           The Exception that was raised.
	 * @see com.dynxsty.dih4jda.interactions.components.button.ButtonHandler#handleButton(ButtonInteractionEvent, Button)
	 * @see com.dynxsty.dih4jda.interactions.components.select_menu.SelectMenuHandler#handleSelectMenu(SelectMenuInteractionEvent, List)
	 */
	public void onComponentException(ComponentInteraction interaction, Exception e) {
	}

	/**
	 * An Event that gets fired when an exception gets raised while handling an AutoComplete interaction.
	 *
	 * @param interaction The {@link CommandAutoCompleteInteraction}.
	 * @param e           The Exception that was raised.
	 * @see AutoCompleteHandler#handleAutoComplete(CommandAutoCompleteInteractionEvent, AutoCompleteQuery)
	 */
	public void onAutoCompleteException(CommandAutoCompleteInteraction interaction, Exception e) {
	}

	/**
	 * An Event that gets fired when an exception gets raised while handling a Modal interaction.
	 *
	 * @param interaction The {@link ModalInteraction}.
	 * @param e           The Exception that was raised.
	 * @see com.dynxsty.dih4jda.interactions.modal.ModalHandler#handleModal(ModalInteractionEvent, List)
	 */
	public void onModalException(ModalInteraction interaction, Exception e) {
	}

	// Other

	/**
	 * An Event that gets fired when the user, which invoked the command, does NOT have one of the required Permissions.
	 *
	 * @param interaction The {@link ModalInteraction}.
	 * @param permissions The {@link Set} of {@link Permission}s which are required to run this commands.
	 * @see com.dynxsty.dih4jda.interactions.commands.CommandRequirements#requirePermissions(Permission...)
	 */
	public void onInsufficientPermissions(CommandInteraction interaction, Set<Permission> permissions) {
	}

	/**
	 * An Event that gets fired when the user, which invoked the command, is NOT allowed to use this command.
	 *
	 * @param interaction The {@link ModalInteraction}.
	 * @param userIds     The {@link Set} of {@link Long}s (userIds) which are able to use this command.
	 * @see com.dynxsty.dih4jda.interactions.commands.CommandRequirements#requireUsers(Long...)
	 */
	public void onUserNotAllowed(CommandInteraction interaction, Set<Long> userIds) {
	}
}

