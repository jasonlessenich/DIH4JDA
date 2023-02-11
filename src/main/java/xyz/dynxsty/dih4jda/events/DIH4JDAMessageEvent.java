package xyz.dynxsty.dih4jda.events;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import xyz.dynxsty.dih4jda.DIH4JDA;

import javax.annotation.Nonnull;

// TODO: Docs
public abstract class DIH4JDAMessageEvent extends DIH4JDAEvent {
	private final MessageReceivedEvent event;

	protected DIH4JDAMessageEvent(@Nonnull String eventName, @Nonnull DIH4JDA dih4jda, @Nonnull MessageReceivedEvent event) {
		super(eventName, dih4jda);
		this.event = event;
	}

	// TODO: Docs
	public MessageReceivedEvent getParentEvent() {
		return event;
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
	public MessageCreateAction reply(CharSequence content) {
		return getMessage().reply(content);
	}

	// TODO: Docs
	public MessageCreateAction reply(MessageCreateData msg) {
		return getMessage().reply(msg);
	}

	// TODO: Add more shortcuts
}
