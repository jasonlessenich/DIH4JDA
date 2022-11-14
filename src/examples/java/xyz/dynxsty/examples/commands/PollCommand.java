package xyz.dynxsty.examples.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;
import xyz.dynxsty.dih4jda.interactions.commands.application.SlashCommand;
import xyz.dynxsty.dih4jda.interactions.components.ButtonHandler;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

// The same structure applies to all other component handlers
public class PollCommand extends SlashCommand implements ButtonHandler {

    public PollCommand() {
        setCommandData(Commands.slash("poll", "Creates a poll with 2 options."));
        setRequiredPermissions(Permission.MESSAGE_MANAGE);
        setCommandCooldown(Duration.of(1, ChronoUnit.MINUTES)); // Add cooldown to prevent spam by users
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        // Creates a message with buttons.
        event.reply("Choose between these two options!")
                .addActionRow(Button.primary("1", "Option 1"), Button.secondary("2", "Option 2"))
                .setEphemeral(false).queue();
    }

    @Override
    public void handleButton(@NotNull ButtonInteractionEvent event, @NotNull Button button) {
        // Handles buttons that were previously mapped using DIH4JDA#addButtonMappings
        if (button.getId().equals("1")) {
            event.reply("You voted for option 1!").queue();
        } else {
            event.reply("You voted for option 2!").queue();
        }
    }
}
