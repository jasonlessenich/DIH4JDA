package com.dynxsty.dih4jda.interactions.components;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ComponentIdBuilder {

	private static String separator = ":";

	public static void setDefaultSeparator(String separator) {
		ComponentIdBuilder.separator = separator;
	}

	public static String getSeparator() {
		return separator;
	}

	public static String build(String identifier, Object... args) {
		return identifier + separator + Arrays.stream(args).map(Object::toString).collect(Collectors.joining(separator));
	}
}
