package xyz.dynxsty.dih4jda.interactions.components;

import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import xyz.dynxsty.dih4jda.util.ComponentIdBuilder;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * An interface that contains the method that should be executed when a user interacts with a
 * {@link net.dv8tion.jda.api.components.selections.EntitySelectMenu}.
 */
public interface EntitySelectMenuHandler {

    /**
     * Method that must be overridden for all classes that handle
     * {@link net.dv8tion.jda.api.interactions.components.selections.EntitySelectInteraction}
     * All select menus that match the identifier will execute this class's implementation of the
     * handleEntitySelectMenu method.
     * This is best used with the {@link ComponentIdBuilder}.
     *
     * <pre>
     * {@code
     *     public class TestCommand extends SlashCommand implements EntitySelectMenuHandler {
     *
     *     public TestCommand() {
     *         setCommandData(Commands.slash("test", "test description"));
     *     }
     *
     *    @Override
     *    public void execute(SlashCommandInteractionEvent event) {
     * 		//Build your select menu here.
     *    }
     *
     *    @Override
     *    public void handleEntitySelectMenu(EntitySelectInteractionEvent event, List<IMentionable> values) {
     *        for (IMentionable entity : values) {
     *            event.getChannel().sendMessage(String.format("Mention: %s", entity.getAsMention())
     *        }
     *   }
     * }
     *}
     * </pre>
     *  In order for this to work, you manually have to configure the corresponding entity-select menu mappings like that:<br>
     * <pre>{@code
     * dih4JDA.addEntitySelectMenuMappings(IdMapping.of(new TestCommand(), "test-entity-select-menu"));
     * }</pre>
     *
     * @param event the {@link EntitySelectInteractionEvent} instance.
     * @param values the values that you could select.
     */
    void handleEntitySelectMenu(@Nonnull EntitySelectInteractionEvent event, @Nonnull List<IMentionable> values);
}
