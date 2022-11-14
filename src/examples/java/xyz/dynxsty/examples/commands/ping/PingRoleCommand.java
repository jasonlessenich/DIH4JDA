package xyz.dynxsty.examples.commands.ping;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import xyz.dynxsty.dih4jda.interactions.commands.application.SlashCommand;

public class PingRoleCommand extends SlashCommand.Subcommand {

    public PingRoleCommand() {
        setCommandData(new SubcommandData("role", "Pings a role.")
                .addOption(OptionType.ROLE, "role", "The role you want to ping", true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Role role = event.getOption("role", OptionMapping::getAsRole);
        event.reply(role.getAsMention()).setEphemeral(false).queue();
    }
}
