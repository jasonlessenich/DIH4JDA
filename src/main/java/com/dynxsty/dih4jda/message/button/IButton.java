package com.dynxsty.dih4jda.message.button;

import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;

public interface IButton {
    void handleButton(ButtonClickEvent event, String... params);
}
