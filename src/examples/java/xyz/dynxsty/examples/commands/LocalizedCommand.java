package xyz.dynxsty.examples.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import xyz.dynxsty.dih4jda.interactions.commands.application.SlashCommand;

public class LocalizedCommand extends SlashCommand {

    public LocalizedCommand() {
        setCommandData(Commands.slash("locale", "Gives you the language that we use.")
                .setNameLocalization(DiscordLocale.GERMAN, "sprache")
                .setDescriptionLocalization(DiscordLocale.GERMAN, "Gibt dir die Sprache die wir nutzen."));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.reply("ENGLISH!").queue();
    }
}
