package xyz.dynxsty.examples.commands.ping;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import xyz.dynxsty.dih4jda.interactions.commands.application.SlashCommand;

public class PingUserSubCommand extends SlashCommand.Subcommand {

    public PingUserSubCommand() {
        setCommandData(new SubcommandData("from-id", "Pings user via their id.")
                .addOption(OptionType.STRING, "id", "The user id.", true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.replyFormat("<@%s>", event.getOption("id", OptionMapping::getAsLong)).setEphemeral(false).queue();
    }
}
