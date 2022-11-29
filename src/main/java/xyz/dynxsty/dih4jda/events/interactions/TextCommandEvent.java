package xyz.dynxsty.dih4jda.events.interactions;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;
import xyz.dynxsty.dih4jda.DIH4JDA;
import xyz.dynxsty.dih4jda.events.DIH4JDAEvent;

import javax.annotation.Nonnull;

// TODO: Docs
public class TextCommandEvent extends DIH4JDAEvent {

	private final MessageReceivedEvent event;

	public TextCommandEvent(@NotNull String eventName, @NotNull DIH4JDA dih4jda, MessageReceivedEvent event) {
		super(eventName, dih4jda);
		this.event = event;
	}

	// TODO: Docs
	@Nonnull
	public Message getMessage() {
		return event.getMessage();
	}

	// TODO: Docs
	@Nonnull
	public User getAuthor() {
		return event.getAuthor();
	}

	// TODO: Docs
	@Nonnull
	public MessageChannelUnion getChannel() {
		return event.getChannel();
	}

	// TODO: Docs
	@Nonnull
	public Guild getGuild() {
		return event.getGuild();
	}

	// TODO: Docs
	@Nonnull
	public MessageReceivedEvent getParentEvent() {
		return event;
	}
}
