package com.dynxsty.dih4jda.util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;

// TODO v1.5: Docs
public class ResponseBuilder {

	private ResponseBuilder() {}

	private static final Pattern REPLACEMENT_REGEX = Pattern.compile("\\{(\\w+)}");

	private static final Map<String, Function<Exception, String>> exceptionReplacements = new HashMap<>();
	private static final Map<String, Function<Permission, String>> permissionReplacements = new HashMap<>();

	private static MessageEmbed exceptionEmbed = buildDefaultExceptionEmbed();

	private static MessageEmbed permissionEmbed = buildDefaultPermissionEmbed();

	static {
		exceptionReplacements.put("exception.name", ex -> ex.getClass().getName());
		exceptionReplacements.put("exception.simple_name", ex -> ex.getClass().getSimpleName());
		exceptionReplacements.put("exception.message", Throwable::getMessage);
		exceptionReplacements.put("exception.localized_message", Throwable::getLocalizedMessage);

		permissionReplacements.put("permission.name", Permission::getName);
		permissionReplacements.put("permission.rawname", Permission::name);
	}

	private static MessageEmbed buildDefaultExceptionEmbed() {
		return new EmbedBuilder()
				.setTitle("Well, this is awkward...")
				.setDescription("I've encountered following exception: {exception.message}")
				.setTimestamp(Instant.now())
				.build();
	}

	private static MessageEmbed buildDefaultPermissionEmbed() {
		return new EmbedBuilder()
				.setTitle("Insufficient Permissions")
				.setDescription("You need `{permission.name}` to execute this command!")
				.setTimestamp(Instant.now())
				.build();
	}

	private static MessageEmbed replaceExceptionVariables(MessageEmbed embed, Exception e) {
		if (embed.getDescription() == null) return embed;
		EmbedBuilder builder = new EmbedBuilder(embed);
		String replaced = REPLACEMENT_REGEX
				.matcher(embed.getDescription())
				.replaceAll(r -> exceptionReplacements.getOrDefault(r.group(1), ex -> r.group()).apply(e));
		builder.setDescription(replaced);
		return builder.build();
	}

	private static MessageEmbed replacePermissionVariables(MessageEmbed embed, Permission permission) {
		if (embed.getDescription() == null) return embed;
		EmbedBuilder builder = new EmbedBuilder(embed);
		String replaced = REPLACEMENT_REGEX
				.matcher(embed.getDescription())
				.replaceAll(r -> permissionReplacements.getOrDefault(r.group(1), ex -> r.group()).apply(permission));
		builder.setDescription(replaced);
		return builder.build();
	}

	public static MessageEmbed getExceptionEmbed(Exception e) {
		return replaceExceptionVariables(exceptionEmbed, e);
	}

	// TODO v1.5: Docs
	public static void setExceptionEmbed(MessageEmbed exceptionEmbed) {
		ResponseBuilder.exceptionEmbed = exceptionEmbed;
	}

	public static MessageEmbed getPermissionEmbed(Permission permission) {
		return replacePermissionVariables(permissionEmbed, permission);
	}

	// TODO v1.5: Docs
	public static void setPermissionEmbed(MessageEmbed permissionEmbed) {
		ResponseBuilder.permissionEmbed = permissionEmbed;
	}
}
