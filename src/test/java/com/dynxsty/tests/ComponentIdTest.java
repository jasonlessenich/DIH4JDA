package com.dynxsty.tests;

import org.junit.jupiter.api.Test;
import xyz.dynxsty.dih4jda.util.ComponentIdBuilder;

public class ComponentIdTest {
	@Test
	public static void main(String[] args) {
		System.out.println(ComponentIdBuilder.build("component-identifier", "1", 2L, 3.0, '4', 5f));
		ComponentIdBuilder.setDefaultSeparator("!");
		System.out.println(ComponentIdBuilder.build("component-identifier", "1", 2L, 3.0, '4', 5f));
	}
}
