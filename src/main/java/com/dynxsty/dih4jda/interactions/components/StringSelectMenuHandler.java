package com.dynxsty.dih4jda.interactions.components;


import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

//TODO docs
public interface StringSelectMenuHandler {

	/**
	 * Method that must be overridden for all classes that handle
	 * {@link net.dv8tion.jda.api.interactions.components.selections.StringSelectInteraction}.
	 * All select menus that match the identifier will execute this class's implementation of the
	 * {@link StringSelectMenuHandler#handleStringSelectMenu(StringSelectInteractionEvent, List)} method.
	 *
	 * <pre>{@code
	 * public class TestCommand extends SlashCommand implements StringSelectMenuHandler {
	 *
	 *     public TestCommand() {
	 *         setSlashCommandData(Commands.slash("test", "test description"));
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
	 *    public void handleStringSelectMenu(StringSelectInteractionEvent event, List<String> values) {
	 * 		for (String roleId : values) {
	 * 			event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRoleById(roleId)).queue();
	 *        }
	 * 		event.reply("Successfully added " + String.join(", ", event.getValues())).queue();
	 *    }
	 * }}</pre>
	 *
	 * @see com.dynxsty.dih4jda.DIH4JDA#addStringSelectMenuHandlers(Map)
	 * @since v1.4
	 */
	void handleStringSelectMenu(@Nonnull StringSelectInteractionEvent event, @Nonnull List<String> values);
}
