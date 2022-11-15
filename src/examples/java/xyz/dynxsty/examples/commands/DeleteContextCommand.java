package xyz.dynxsty.examples.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import xyz.dynxsty.dih4jda.interactions.commands.application.ContextCommand;

public class DeleteContextCommand extends ContextCommand.Message {

    public DeleteContextCommand() {
        setCommandData(Commands.message("delete"));
        setRequiredPermissions(Permission.MESSAGE_MANAGE);
    }

    @Override
    public void execute(MessageContextInteractionEvent event) {
        long messageId = event.getTarget().getIdLong();
        event.getTarget().delete().queue();
        event.replyFormat("Deleted message with id `%s`", messageId).queue();
    }
}
