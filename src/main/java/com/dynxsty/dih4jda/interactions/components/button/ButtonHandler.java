package com.dynxsty.dih4jda.interactions.components.button;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

/**
 * Interface that must be implemented for all Commands that handle Button Interactions.
 * All buttons that match the identifier will execute this class's implementation of the {@link ButtonHandler#handleButton} method.
 * This is best used with the {@link com.dynxsty.dih4jda.interactions.components.ComponentIdBuilder}.
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
 */
public interface ButtonHandler {
	void handleButton(ButtonInteractionEvent event, net.dv8tion.jda.api.interactions.components.buttons.Button button);
}
