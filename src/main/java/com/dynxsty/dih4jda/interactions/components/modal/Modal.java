package com.dynxsty.dih4jda.interactions.components.modal;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;

import java.util.List;

public interface Modal {
	void handleModal(ModalInteractionEvent event, List<ModalMapping> values);
}
