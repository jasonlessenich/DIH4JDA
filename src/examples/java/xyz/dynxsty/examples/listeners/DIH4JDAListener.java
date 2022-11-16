package xyz.dynxsty.examples.listeners;

import xyz.dynxsty.dih4jda.events.CommandCooldownEvent;
import xyz.dynxsty.dih4jda.events.CommandExceptionEvent;
import xyz.dynxsty.dih4jda.events.DIH4JDAEventListener;

import javax.annotation.Nonnull;
import java.time.ZoneId;

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
        event.getInteraction().replyFormat("You are on cooldown. Next use <t:%s:R>",
                event.getCooldown().getNextUse().atZone(ZoneId.systemDefault()).toEpochSecond()).queue();
    }

    // add more events if you need to
}
