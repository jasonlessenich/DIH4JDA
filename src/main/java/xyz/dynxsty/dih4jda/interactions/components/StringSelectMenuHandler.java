package xyz.dynxsty.dih4jda.interactions.components;


import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import xyz.dynxsty.dih4jda.DIH4JDA;
import xyz.dynxsty.dih4jda.util.ComponentIdBuilder;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * An interface that contains the method that should be executed when a user interacts with a
 * {@link net.dv8tion.jda.api.components.selections.StringSelectMenu}.
 */
public interface StringSelectMenuHandler {

	/**
	 * Method that must be overridden for all classes that handle
	 * {@link net.dv8tion.jda.api.interactions.components.selections.StringSelectInteraction}.
	 * All select menus that match the identifier will execute this class's implementation of the
	 * handleStringSelectMenu method.
	 * This is best used with the {@link ComponentIdBuilder}.
	 *
	 * <pre>{@code
	 * public class TestCommand extends SlashCommand implements StringSelectMenuHandler {
	 *
	 *     public TestCommand() {
	 *         setCommandData(Commands.slash("test", "test description"));
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
	 * }}
	 * </pre>
     *  In order for this to work, you manually have to configure the corresponding string-select menu mappings like that:<br>
	 * <pre>{@code
	 * dih4JDA.addStringSelectMenuMappings(IdMapping.of(new TestCommand(), "test-string-select-menu"));
	 * }</pre>
	 *
	 * @param event the provided {@link StringSelectInteractionEvent}.
	 * @param values the provided selections.
	 * @see DIH4JDA#addStringSelectMenuMappings(IdMapping[])
	 * @since v1.4
	 */
	void handleStringSelectMenu(@Nonnull StringSelectInteractionEvent event, @Nonnull List<String> values);
}
