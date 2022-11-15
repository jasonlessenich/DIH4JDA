package xyz.dynxsty.examples.listeners;

import xyz.dynxsty.dih4jda.events.CommandCooldownEvent;
import xyz.dynxsty.dih4jda.events.CommandExceptionEvent;
import xyz.dynxsty.dih4jda.events.DIH4JDAEventListener;

import javax.annotation.Nonnull;

public class DIH4JDAListener implements DIH4JDAEventListener {

    @Override
    public void onCommandException(@Nonnull CommandExceptionEvent event) {
        //sends a message in the channel the user executed the command in that something went wrong + the name of the
        // exception.
        event.getInteraction().getMessageChannel().sendMessageFormat("Something went wrong when executing a command.\n" +
                "Exception: %s", event.getThrowable().getMessage()).queue();
    }

    @Override
    public void onCommandCooldown(@Nonnull CommandCooldownEvent event) {
        event.getInteraction().getMessageChannel().sendMessageFormat("Seems like you have to wait before you use the " +
                "command again.\n You can try again in: <t:%s:R>", event.getCooldown().getNextUse().toEpochMilli()).queue();
    }

    // add more events if you need to
}
