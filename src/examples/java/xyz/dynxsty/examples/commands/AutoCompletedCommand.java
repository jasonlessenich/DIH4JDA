package xyz.dynxsty.examples.commands;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.AutoCompleteQuery;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import xyz.dynxsty.dih4jda.interactions.AutoCompletable;
import xyz.dynxsty.dih4jda.interactions.commands.application.SlashCommand;

import java.util.List;

public class AutoCompletedCommand extends SlashCommand implements AutoCompletable {

    public AutoCompletedCommand() {
        setCommandData(Commands.slash("auto-complete", "Auto completed command.")
                .addOption(OptionType.INTEGER, "number", "Choose a number!", true, true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.replyFormat("You chose the number: %s", event.getOption("number", OptionMapping::getAsInt)).queue();
    }

    @Override
    public void handleAutoComplete(@NotNull CommandAutoCompleteInteractionEvent event, @NotNull AutoCompleteQuery target) {
        List<Command.Choice> choices = List.of(
                new Command.Choice("1", 1),
                new Command.Choice("2", 2),
                new Command.Choice("3", 3),
                new Command.Choice("4", 4),
                new Command.Choice("5", 5)
        );
        event.replyChoices(choices).queue();
    }
}
