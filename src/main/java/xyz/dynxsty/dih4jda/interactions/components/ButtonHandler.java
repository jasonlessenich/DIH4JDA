package xyz.dynxsty.dih4jda.interactions.components;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import xyz.dynxsty.dih4jda.DIH4JDA;
import xyz.dynxsty.dih4jda.util.ComponentIdBuilder;

import javax.annotation.Nonnull;

/**
 * An interface that contains the method that should be executed when a user interacts with a
 * {@link Button}.
 */
public interface ButtonHandler {
	/**
	 * Method that must be overridden for all commands that handle button interactions.
	 * All buttons that match the identifier will execute this class's implementation of the handleButton method.
	 * This is best used with the {@link ComponentIdBuilder}.
	 *
	 * <pre>{@code
	 * public class TestCommand extends SlashCommand implements ButtonHandler {
	 *
	 *     public TestCommand() {
	 *         setCommandData(Commands.slash("test", "test description"));
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
	 * <br>
	 * Please do not forget to add the button mappings to the {@link DIH4JDA} instance.<br>
	 * Only needed if you used the {@link ComponentIdBuilder} to create the button's id.
	 * <pre>{@code
	 * dih4JDA.addButtonMappings(IdMapping.of(new TestCommand(), "test-button"));
	 * }</pre>
	 *
	 * @param event the {@link ButtonInteractionEvent}.
	 * @param button the {@link Button} that the user interacted with.
	 * @see DIH4JDA#addButtonMappings(IdMapping[])
	 * @since v1.4
	 */
	void handleButton(@Nonnull ButtonInteractionEvent event, @Nonnull Button button);
}
