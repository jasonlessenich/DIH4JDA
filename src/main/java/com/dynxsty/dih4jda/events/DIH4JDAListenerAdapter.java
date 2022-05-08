package com.dynxsty.dih4jda.events;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.ModalInteraction;
import net.dv8tion.jda.api.interactions.commands.CommandAutoCompleteInteraction;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.components.ComponentInteraction;

import java.util.Set;

public abstract class DIH4JDAListenerAdapter {

    // Exceptions
    public void onCommandException(CommandInteraction interaction, Exception e) {}
    public void onComponentException(ComponentInteraction interaction, Exception e) {}
    public void onAutoCompleteException(CommandAutoCompleteInteraction interaction, Exception e) {}
    public void onModalException(ModalInteraction interaction, Exception e) {}

    // Other
    public void onInsufficientPermission(CommandInteraction interaction, Set<Permission> permissions) {}
}

