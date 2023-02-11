package xyz.dynxsty.examples.commands;

import org.jetbrains.annotations.NotNull;
import xyz.dynxsty.dih4jda.events.interactions.TextCommandEvent;
import xyz.dynxsty.dih4jda.interactions.commands.text.TextCommand;
import xyz.dynxsty.dih4jda.interactions.commands.text.TextOptionData;
import xyz.dynxsty.dih4jda.interactions.commands.text.TextOptionMapping;

public class TextSayCommand extends TextCommand {
    public TextSayCommand() {
        setName("say");
        setDescription("Say something");
        setOptions(
                new TextOptionData("text", "The text the bot should say", true)
        );
    }

    @Override
    public void execute(@NotNull TextCommandEvent event) {
        final TextOptionMapping textMapping = event.getOption("text");
        if (textMapping == null) {
            event.getChannel().sendMessage("mapping null").queue();
            return;
        }
        String text = textMapping.getAsString();
        event.getChannel().sendMessage(text == null ? "null" : text).queue();
    }
}
