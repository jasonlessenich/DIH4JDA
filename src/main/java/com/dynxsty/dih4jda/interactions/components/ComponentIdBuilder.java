package com.dynxsty.dih4jda.interactions.components;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Utility class that allows for easy and consistent component-id building.
 */
public class ComponentIdBuilder {

	private ComponentIdBuilder() {}

	private static String separator = ":";

	/**
	 * Changes the default component-id separator.
	 *
	 * @param separator The string that should act as the separator.
	 */
	public static void setDefaultSeparator(String separator) {
		ComponentIdBuilder.separator = separator;
	}

	/**
	 * Gets the current separator.
	 *
	 * @return The separator.
	 */
	public static String getSeparator() {
		return separator;
	}

	/**
	 * Builds a component-id using the (set) separator and the given identifier and arguments.
	 * <pre>{@code
	 * Button.secondary(ComponentIdBuilder.build("self-role", roleId), "Click me!");
	 * }</pre>
	 *
	 * @param identifier The component's identifier.
	 * @param args       An optional parameter for arguments.
	 * @return The built component-id, as a {@link String}.
	 */
	public static String build(String identifier, Object... args) {
		StringBuilder sb = new StringBuilder(identifier);
		if (args.length > 0) {
			sb.append(separator).append(Arrays.stream(args).map(Object::toString).collect(Collectors.joining(separator)));
		}
		return sb.toString();
	}

	//TODO-v1.4: Documentation
	public static String[] splitBySeparator(String id) {
		return id.split(separator);
	}
}
