package xyz.dynxsty.examples;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import xyz.dynxsty.dih4jda.DIH4JDA;
import xyz.dynxsty.dih4jda.DIH4JDABuilder;
import xyz.dynxsty.dih4jda.exceptions.DIH4JDAException;
import xyz.dynxsty.dih4jda.interactions.commands.application.RegistrationType;
import xyz.dynxsty.dih4jda.interactions.components.IdMapping;
import xyz.dynxsty.examples.commands.PollCommand;
import xyz.dynxsty.examples.commands.TextSayCommand;
import xyz.dynxsty.examples.listeners.DIH4JDAListener;

public class BotExample {

    public static void main(String[] args) throws DIH4JDAException {
        JDA jda = JDABuilder.createLight("MTA0MDM4NTk2MTkzNjI5MzkzOA.GbUhYA.XyEAuRpH5zWM3LB--SQ5kZP_Of0enIouaFUZ0M")
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .build();

        //Sets the default registration type to guild only.
        DIH4JDA.setDefaultRegistrationType(RegistrationType.GUILD);
        DIH4JDA dih4JDA = DIH4JDABuilder
                .setJDA(jda)
                .build();

        dih4JDA.addTextCommands(new TextSayCommand());
        // Maps the PollCommand class to handle buttons with an id of 1 or 2.
        // dih4JDA.addButtonMappings(IdMapping.of(new PollCommand(), "button"));

        // Adds the DIH4JDAListener class as an event listener.
        // dih4JDA.addEventListener(new DIH4JDAListener());
    }
}
