package com.dynxsty.dih4jda.message;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageAction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InteractionBuilder {

    private final WebhookMessageAction<Message> action;
    private final List<Button> buttons;

    public InteractionBuilder(WebhookMessageAction<Message> action) {
        this.buttons = new ArrayList<>();
        this.action = action;
    }

    public InteractionBuilder addButtons(Button... buttons) {
        Collections.addAll(this.buttons, buttons);
        return this;
    }

    public WebhookMessageAction<Message> getAction() {
        return action.addActionRow(buttons);
    }
}
