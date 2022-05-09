package com.dynxsty.dih4jda.events;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.ModalInteraction;
import net.dv8tion.jda.api.interactions.commands.CommandAutoCompleteInteraction;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.components.ComponentInteraction;

import java.util.Set;

public abstract class DIH4JDAListenerAdapter {

    // Exceptions

    // TODO v1.5: Documentation
    public void onCommandException(CommandInteraction interaction, Exception e) {}

    // TODO v1.5: Documentation
    public void onComponentException(ComponentInteraction interaction, Exception e) {}

    // TODO v1.5: Documentation
    public void onAutoCompleteException(CommandAutoCompleteInteraction interaction, Exception e) {}

    // TODO v1.5: Documentation
    public void onModalException(ModalInteraction interaction, Exception e) {}

    // Other

    // TODO v1.5: Documentation
    public void onInsufficientPermissions(CommandInteraction interaction, Set<Permission> permissions) {}

    // TODO v1.5: Documentation
    public void onUserNotAllowed(CommandInteraction interaction, Set<Long> userIds) {}
}

