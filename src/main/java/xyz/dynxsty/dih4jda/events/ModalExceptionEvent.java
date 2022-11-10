package xyz.dynxsty.dih4jda.events;

import net.dv8tion.jda.api.interactions.modals.ModalInteraction;
import xyz.dynxsty.dih4jda.DIH4JDA;

public class ModalExceptionEvent extends ThrowableDIH4JDAEvent<ModalInteraction> {
	public ModalExceptionEvent(DIH4JDA dih4jda, ModalInteraction interaction, Throwable throwable) {
		super("onModalException", dih4jda, interaction, throwable);
	}
}
