package com.dynxsty.dih4jda.util;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;

import java.util.List;

/**
 * Utility class that contains some useful methods regarding the AutoComplete system.
 *
 * @since v1.4
 */
public class AutoCompleteUtils {

	private AutoCompleteUtils() {
	}

	/**
	 * Filters all AutoComplete choices based on the user's current input.
	 *
	 * <pre>{@code
	 * return event.replyChoices(AutoCompleteUtils.filterChoices(event, choices));
	 * }</pre>
	 *
	 * @param event   The {@link CommandAutoCompleteInteractionEvent} that was fired.
	 * @param choices A {@link List} of {@link Command.Choice}s.
	 * @return The filtered {@link List} of {@link Command.Choice}s.
	 * @since v1.4
	 */
	public static List<Command.Choice> filterChoices(CommandAutoCompleteInteractionEvent event, List<Command.Choice> choices) {
		return AutoCompleteUtils.filterChoices(event.getFocusedOption().getValue().toLowerCase(), choices);
	}

	/**
	 * Filters all AutoComplete choices based on the user's current input.
	 *
	 * <pre>{@code
	 * return event.replyChoices(AutoCompleteUtils.filterChoices("abc", choices));
	 * }</pre>
	 *
	 * @param filter  The filter.
	 * @param choices A {@link List} of {@link Command.Choice}s.
	 * @return The filtered {@link List} of {@link Command.Choice}s.
	 * @since v1.4
	 */
	public static List<Command.Choice> filterChoices(String filter, List<Command.Choice> choices) {
		choices.removeIf(choice -> !choice.getName().toLowerCase().contains(filter.toLowerCase()));
		return choices;
	}
}
