package com.dynxsty.dih4jda.interactions.commands.slash_command.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Abstract class that represents an executable Slash Command (excluding Subcommand Groups).
 */
public abstract class ExecutableCommand {

	private final List<String> handledButtonIds = new ArrayList<>();
	private final List<String> handledSelectMenuIds = new ArrayList<>();
	private final List<String> handledModalIds = new ArrayList<>();
	private boolean handleAutoComplete = false;

	protected ExecutableCommand() {
	}

	/**
	 * @return Whether the class should handle all options that have the AutoComplete functionality activated.
	 */
	public boolean shouldHandleAutoComplete() {
		return handleAutoComplete;
	}

	/**
	 * Enables AutoComplete handling for all
	 */
	public void enableAutoCompleteHandling() {
		handleAutoComplete = true;
	}

	/**
	 * Gets all Button identifiers that should be handled.
	 *
	 * @return All identifiers as a {@link List}.
	 */
	public List<String> getHandledButtonIds() {
		return handledButtonIds;
	}

	/**
	 * Allows to set a set of identifiers (usually the first element in a button id) that should be handled in this specific class.
	 * If set, the class must extend {@link com.dynxsty.dih4jda.interactions.components.button.ButtonHandler} and
	 * implement its method.
	 *
	 * <pre>{@code
	 * public class TestCommand extends GuildSlashCommand implements SlashCommand, ButtonHandler {
	 *
	 *     public TestCommand(Guild guild) {
	 *         setCommandData(Commands.slash("test", "test description"));
	 *         handleButtonIds("test-button");
	 *     }
	 *
	 *    @Override
	 *    public void handleSlashCommand(SlashCommandInteractionEvent event) {
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
	 * * @see com.dynxsty.dih4jda.interactions.components.ComponentIdBuilder#build
	 *
	 * @param handledButtonIds An array of Strings (the id's) that should be handled.
	 */
	public void handleButtonIds(String... handledButtonIds) {
		this.handledButtonIds.addAll(Arrays.asList(handledButtonIds));
	}

	/**
	 * Gets all SelectMenu identifiers that should be handled.
	 *
	 * @return All identifiers as a {@link List}.
	 */
	public List<String> getHandledSelectMenuIds() {
		return handledSelectMenuIds;
	}

	/**
	 * Allows to set a set of identifiers (usually the first element in a select menu id) that should be handled in this specific class.
	 * If set, the class must extend {@link com.dynxsty.dih4jda.interactions.components.select_menu.SelectMenuHandler} and
	 * implement its method.
	 *
	 * <pre>{@code
	 * public class TestCommand extends GuildSlashCommand implements SlashCommand, SelectMenuHandler {
	 *
	 *     public TestCommand(Guild guild) {
	 *         setCommandData(Commands.slash("test", "test description"));
	 *         handleSelectMenuIds("test-select-menu");
	 *     }
	 *
	 *    @Override
	 *    public void handleSlashCommand(SlashCommandInteractionEvent event) {
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
	 * @see com.dynxsty.dih4jda.interactions.components.ComponentIdBuilder#build
	 */
	public void handleSelectMenuIds(String... handledSelectMenuIds) {
		this.handledSelectMenuIds.addAll(Arrays.asList(handledSelectMenuIds));
	}

	/**
	 * Gets all Modal identifiers that should be handled.
	 *
	 * @return All identifiers as a {@link List}.
	 */
	public List<String> getHandledModalIds() {
		return handledModalIds;
	}

	/**
	 * Allows to set a set of identifiers (usually the first element in a modal id) that should be handled in this specific class.
	 * If set, the class must extend {@link com.dynxsty.dih4jda.interactions.components.modal.ModalHandler} and
	 * implement its method.
	 *
	 * <pre>{@code
	 * public class TestCommand extends GuildSlashCommand implements SlashCommand, ModalHandler {
	 *
	 *     public TestCommand(Guild guild) {
	 *         setCommandData(Commands.slash("test", "test description"));
	 *         handleModalIds("test-modal");
	 *     }
	 *
	 *     @Override
	 *     public void handleSlashCommand(SlashCommandInteractionEvent event) {
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
	 * @see com.dynxsty.dih4jda.interactions.components.ComponentIdBuilder#build
	 */
	public void handleModalIds(String... handledModalIds) {
		this.handledModalIds.addAll(Arrays.asList(handledModalIds));
	}
}
