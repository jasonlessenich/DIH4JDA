package xyz.dynxsty.examples;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import xyz.dynxsty.dih4jda.DIH4JDA;
import xyz.dynxsty.dih4jda.DIH4JDABuilder;
import xyz.dynxsty.dih4jda.exceptions.DIH4JDAException;
import xyz.dynxsty.dih4jda.interactions.commands.application.RegistrationType;
import xyz.dynxsty.dih4jda.interactions.components.IdMapping;
import xyz.dynxsty.examples.commands.PollCommand;

public class BotExample {

    public static void main(String[] args) throws DIH4JDAException {
        JDA jda = JDABuilder.createLight("YOUR BOT TOKEN").build();

        //Sets the default registration type to guild only.
        DIH4JDA.setDefaultRegistrationType(RegistrationType.GUILD);
        DIH4JDA dih4JDA = DIH4JDABuilder
                .setJDA(jda)
                .setCommandPackages("xyz.dynxsty.examples.commands") //The package where all of your commands are
                // located.
                .build();

        //Added the PollCommand class to handle buttons with an id of 1 or 2.
        dih4JDA.addButtonMappings(IdMapping.of(new PollCommand(), "1", "2"));
    }
}
