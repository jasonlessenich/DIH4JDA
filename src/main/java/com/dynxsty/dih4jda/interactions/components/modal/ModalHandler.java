package com.dynxsty.dih4jda.interactions.components.modal;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;

import java.util.List;

/**
 * Interface that must be implemented for all Commands that handle Modal Interactions.
 * All modals that match the identifier will execute this class's implementation of the {@link ModalHandler#handleModal} method.
 * This is best used with the {@link com.dynxsty.dih4jda.interactions.components.ComponentIdBuilder}.
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
 */
public interface ModalHandler {
	void handleModal(ModalInteractionEvent event, List<ModalMapping> values);
}
