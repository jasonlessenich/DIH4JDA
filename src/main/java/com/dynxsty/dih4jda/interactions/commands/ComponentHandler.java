package com.dynxsty.dih4jda.interactions.commands;

import com.dynxsty.dih4jda.interactions.ComponentIdBuilder;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Allows to set Component-IDs which get handled within the extending class.
 * Best used in combination with {@link ComponentIdBuilder}.
 *
 * @since v1.4
 */
public abstract class ComponentHandler {

	private final List<String> handledButtonIds = new ArrayList<>();
	private final List<String> handledSelectMenuIds = new ArrayList<>();
	private final List<String> handledModalIds = new ArrayList<>();
	private boolean handleAutoComplete = false;

	protected ComponentHandler() {
	}

	/**
	 * @return Whether the class should handle all options that have the AutoComplete functionality activated.
	 * @since v1.4
	 */
	public boolean shouldHandleAutoComplete() {
		return handleAutoComplete;
	}

	/**
	 * Enables AutoComplete handling for all options of this Slash Command.
	 * If enabled, this class must implement {@link AutoCompleteHandler} and
	 * override its method.
	 *
	 * <pre>{@code
	 * public class PingCommand extends SlashCommand implements AutoCompleteHandler {
	 *
	 *     public PingCommand() {
	 *         setCommandData(Commands.slash("ping", "Ping someone").addOption(OptionType.STRING, "user-id", "The user's id"));
	 *         enableAutoCompleteHandling();
	 *     }
	 *
	 *     @Override
	 *     public void execute(SlashCommandInteractionEvent event) {
	 *         OptionMapping mapping = event.getOption("user-id");
	 *         String userId = mapping.getAsString();
	 *         event.replyFormat("Ping! <@%s>", userId).queue();
	 *     }
	 *
	 *     @Override
	 *     public void handleAutoComplete(CommandAutoCompleteInteractionEvent event, AutoCompleteQuery target) {
	 *         if (target.getName().equals("user-id")) {
	 *             List<Member> members = event.getGuild().getMembers().stream().limit(25).collect(Collectors.toList());
	 *             List<Command.Choice> choices = new ArrayList<>(25);
	 *             for (Member member : members) {
	 *                 choices.add(new Command.Choice(member.getUser().getAsTag(), member.getId()));
	 *             }
	 *             event.replyChoices(AutoCompleteUtils.filterChoices(event, choices)).queue();
	 *         }
	 *     }
	 *
	 * }}</pre>
	 *
	 * @see AutoCompleteHandler
	 * @see com.dynxsty.dih4jda.util.AutoCompleteUtils
	 * @since v1.4
	 */
	public void enableAutoCompleteHandling() {
		handleAutoComplete = true;
	}

	/**
	 * Gets all Button identifiers that should be handled.
	 *
	 * @return All identifiers as a {@link List}.
	 * @since v1.4
	 */
	public List<String> getHandledButtonIds() {
		return handledButtonIds;
	}

	/**
	 * Allows to set a set of identifiers (usually the first element in a button id) that should be handled in
	 * this specific class.
	 *
	 * <pre>{@code
	 * public class TestCommand extends SlashCommand {
	 *
	 *     public TestCommand() {
	 *         setCommandData(Commands.slash("test", "test description"));
	 *         handleButtonIds("test-button");
	 *     }
	 *
	 *    @Override
	 *    public void execute(SlashCommandInteractionEvent event) {
	 * 		event.reply("test")
	 * 				.addActionRow(
	 * 						Button.secondary(ComponentIdBuilder.build("test-button", 1), "Click me!"),
	 * 						Button.secondary(ComponentIdBuilder.build("test-button", 2), "NO! Click me!")
	 * 				).queue();
	 *    }
	 *
	 *     @Override
	 *     public void handleButton(ButtonInteractionEvent event, Button button) {
	 * 		String[] id = ComponentIdBuilder.split(button.getId());
	 * 		String content = "";
	 * 		switch (id[1]) {
	 * 			case "1": content = "Thanks for not clicking the other button! :)"; break;
	 * 			case "2": content = "Phew, thanks for clicking me..."; break;
	 *         }
	 * 		event.reply(content).queue();
	 *    }
	 * }}</pre>
	 * <p>
	 *
	 * @param handledButtonIds An array of Strings (the id's) that should be handled.
	 * @see ComponentIdBuilder#build
	 * @since v1.4
	 */
	public void handleButtonIds(String... handledButtonIds) {
		this.handledButtonIds.addAll(Arrays.asList(handledButtonIds));
	}

	/**
	 * Interface that must be implemented for all Commands that handle Button Interactions.
	 * All buttons that match the identifier will execute this class's implementation of the handleButton method.
	 * This is best used with the {@link ComponentIdBuilder}.
	 *
	 * <pre>{@code
	 * public class TestCommand extends SlashCommand {
	 *
	 *     public TestCommand() {
	 *         setCommandData(Commands.slash("test", "test description"));
	 *         handleButtonIds("test-button");
	 *     }
	 *
	 *    @Override
	 *    public void execute(SlashCommandInteractionEvent event) {
	 * 		event.reply("test")
	 * 				.addActionRow(
	 * 						Button.secondary(ComponentIdBuilder.build("test-button", 1), "Click me!"),
	 * 						Button.secondary(ComponentIdBuilder.build("test-button", 2), "NO! Click me!")
	 * 				).queue();
	 *    }
	 *
	 *     @Override
	 *     public void handleButton(ButtonInteractionEvent event, Button button) {
	 * 		String[] id = ComponentIdBuilder.split(button.getId());
	 * 		String content = "";
	 * 		switch (id[1]) {
	 * 			case "1": content = "Thanks for not clicking the other button! :)"; break;
	 * 			case "2": content = "Phew, thanks for clicking me..."; break;
	 *         }
	 * 		event.reply(content).queue();
	 *    }
	 * }}</pre>
	 *
	 * @since v1.4
	 */
	public void handleButton(ButtonInteractionEvent event, Button button) {
	}

	/**
	 * Gets all SelectMenu identifiers that should be handled.
	 *
	 * @return All identifiers as a {@link List}.
	 * @since v1.4
	 */
	public List<String> getHandledSelectMenuIds() {
		return handledSelectMenuIds;
	}

	/**
	 * Allows to set a set of identifiers (usually the first element in a select menu id) that should be handled
	 * in this specific class.
	 *
	 * <pre>{@code
	 * public class TestCommand extends GuildSlashCommand implements SelectMenuHandler {
	 *
	 *     public TestCommand() {
	 *         setCommandData(Commands.slash("test", "test description"));
	 *         handleSelectMenuIds("test-select-menu");
	 *     }
	 *
	 *    @Override
	 *    public void execute(SlashCommandInteractionEvent event) {
	 * 		List<Role> roles = [...]
	 * 		SelectMenu.Builder menu = SelectMenu.create("test-select-menu");
	 * 		for (Role role : roles) {
	 * 			menu.addOption(role.getName(), role.getId());
	 *        }
	 * 		event.reply("Choose your rank!").addActionRow(menu.build()).queue();
	 *    }
	 *
	 *    @Override
	 *    public void handleSelectMenu(SelectMenuInteractionEvent event, List<String> values) {
	 * 		for (String roleId : values) {
	 * 			event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRoleById(roleId)).queue();
	 *        }
	 * 		event.reply("Successfully added " + String.join(", ", event.getValues())).queue();
	 *    }
	 * }}</pre>
	 *
	 * @param handledSelectMenuIds An array of Strings (the id's) that should be handled.
	 * @see ComponentIdBuilder#build
	 * @see com.dynxsty.dih4jda.interactions.components.select_menu.SelectMenuHandler
	 * @since v1.4
	 */
	public void handleSelectMenuIds(String... handledSelectMenuIds) {
		this.handledSelectMenuIds.addAll(Arrays.asList(handledSelectMenuIds));
	}

	/**
	 * Method that must be overridden for all Classes that handle Select Menu Interactions.
	 * All select menus that match the identifier will execute this class's implementation of the handleSelectMenu method.
	 *
	 * <pre>{@code
	 * public class TestCommand extends SlashCommand {
	 *
	 *     public TestCommand() {
	 *         setCommandData(Commands.slash("test", "test description"));
	 *         handleSelectMenuIds("test-select-menu");
	 *     }
	 *
	 *    @Override
	 *    public void execute(SlashCommandInteractionEvent event) {
	 * 		List<Role> roles = [...]
	 * 		SelectMenu.Builder menu = SelectMenu.create("test-select-menu");
	 * 		for (Role role : roles) {
	 * 			menu.addOption(role.getName(), role.getId());
	 *        }
	 * 		event.reply("Choose your rank!").addActionRow(menu.build()).queue();
	 *    }
	 *
	 *    @Override
	 *    public void handleSelectMenu(SelectMenuInteractionEvent event, List<String> values) {
	 * 		for (String roleId : values) {
	 * 			event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRoleById(roleId)).queue();
	 *        }
	 * 		event.reply("Successfully added " + String.join(", ", event.getValues())).queue();
	 *    }
	 * }}</pre>
	 *
	 * @since v1.4
	 */
	public void handleSelectMenu(SelectMenuInteractionEvent event, List<String> values) {
	}

	/**
	 * Gets all Modal identifiers that should be handled.
	 *
	 * @return All identifiers as a {@link List}.
	 * @since v1.4
	 */
	public List<String> getHandledModalIds() {
		return handledModalIds;
	}

	/**
	 * Allows to set a set of identifiers (usually the first element in a modal id) that should be handled
	 * in this specific class.
	 *
	 * <pre>{@code
	 * public class TestCommand extends SlashCommand {
	 *
	 *     public TestCommand() {
	 *         setCommandData(Commands.slash("test", "test description"));
	 *         handleModalIds("test-modal");
	 *     }
	 *
	 *     @Override
	 *     public void execute(SlashCommandInteractionEvent event) {
	 * 		Role applied = [...]
	 * 		TextInput email = TextInput.create("email", "Email", TextInputStyle.SHORT)
	 * 				.setPlaceholder("Enter your E-mail")
	 * 				.setMinLength(10)
	 * 				.setMaxLength(100)
	 * 				.build();
	 *
	 * 		TextInput body = TextInput.create("body", "Body", TextInputStyle.PARAGRAPH)
	 * 				.setPlaceholder("Your application goes here")
	 * 				.setMinLength(30)
	 * 				.setMaxLength(1000)
	 * 				.build();
	 *
	 * 		Modal modal = Modal.create(ComponentIdBuilder.build("test-modal", applied.getIdLong()), "Apply for " + applied.getName())
	 * 				.addActionRows(ActionRow.of(email), ActionRow.of(body))
	 * 				.build();
	 * 		event.replyModal(modal).queue();
	 *    }
	 *
	 *    @Override
	 *    public void handleModal(ModalInteractionEvent event, List<ModalMapping> values) {
	 *      Role role = event.getGuild().getRoleById(ComponentIdBuilder.split(event.getModalId())[1]);
	 * 		String email = event.getValue("email").getAsString();
	 * 		String body = event.getValue("body").getAsString();
	 *
	 * 		createApplication(role, email, body);
	 *
	 * 		event.reply("Thanks for your application!").queue();
	 *    }
	 * }}</pre>
	 *
	 * @param handledModalIds An array of Strings (the id's) that should be handled.
	 * @see ComponentIdBuilder#build
	 * @since v1.4
	 */
	public void handleModalIds(String... handledModalIds) {
		this.handledModalIds.addAll(Arrays.asList(handledModalIds));
	}

	/**
	 * Method that must be overridden for all Commands that handle Modal Interactions.
	 * All modals that match the identifier will execute this class's implementation of the handleModal method.
	 * This is best used with the {@link ComponentIdBuilder}.
	 *
	 * <pre>{@code
	 * public class TestCommand extends SlashCommand {
	 *
	 *     public TestCommand() {
	 *         setCommandData(Commands.slash("test", "test description"));
	 *         handleModalIds("test-modal");
	 *     }
	 *
	 *     @Override
	 *     public void execute(SlashCommandInteractionEvent event) {
	 * 		Role applied = [...]
	 * 		TextInput email = TextInput.create("email", "Email", TextInputStyle.SHORT)
	 * 				.setPlaceholder("Enter your E-mail")
	 * 				.setMinLength(10)
	 * 				.setMaxLength(100)
	 * 				.build();
	 *
	 * 		TextInput body = TextInput.create("body", "Body", TextInputStyle.PARAGRAPH)
	 * 				.setPlaceholder("Your application goes here")
	 * 				.setMinLength(30)
	 * 				.setMaxLength(1000)
	 * 				.build();
	 *
	 * 		Modal modal = Modal.create(ComponentIdBuilder.build("test-modal", applied.getIdLong()), "Apply for " + applied.getName())
	 * 				.addActionRows(ActionRow.of(email), ActionRow.of(body))
	 * 				.build();
	 * 		event.replyModal(modal).queue();
	 *    }
	 *
	 *    @Override
	 *    public void handleModal(ModalInteractionEvent event, List<ModalMapping> values) {
	 *      Role role = event.getGuild().getRoleById(ComponentIdBuilder.split(event.getModalId())[1]);
	 * 		String email = event.getValue("email").getAsString();
	 * 		String body = event.getValue("body").getAsString();
	 *
	 * 		createApplication(role, email, body);
	 *
	 * 		event.reply("Thanks for your application!").queue();
	 *    }
	 * }}</pre>
	 *
	 * @since v1.4
	 */
	public void handleModal(ModalInteractionEvent event, List<ModalMapping> values) {
	}
}
