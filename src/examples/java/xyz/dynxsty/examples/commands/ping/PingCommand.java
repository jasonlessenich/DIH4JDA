package xyz.dynxsty.examples.commands.ping;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import xyz.dynxsty.dih4jda.interactions.commands.application.SlashCommand;

public class PingCommand extends SlashCommand {

    public PingCommand() {
        setCommandData(Commands.slash("ping", "Pings someone or something."));
        setRequiredPermissions(Permission.ADMINISTRATOR);

        //Adds a subcommand group and links the subcommand to this specific group.
        addSubcommandGroups(SubcommandGroup.of(new SubcommandGroupData("user", "Pings a user."), new PingUserSubCommand()));
        addSubcommands(new PingRoleCommand());
    }
}
