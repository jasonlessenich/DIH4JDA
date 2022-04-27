package com.dynxsty.dih4jda.interactions.components.select_menu;

import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;

import java.util.List;

/**
 * Interface that must be implemented for all Commands that handle Select Menu Interactions.
 * All select menus that match the identifier will execute this class's implementation of the {@link SelectMenuHandler#handleSelectMenu} method.
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
 * @since v1.4
 */
public interface SelectMenuHandler {
	void handleSelectMenu(SelectMenuInteractionEvent event, List<String> values);
}
