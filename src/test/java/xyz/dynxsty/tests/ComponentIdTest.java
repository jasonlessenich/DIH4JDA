package xyz.dynxsty.tests;

import org.junit.jupiter.api.Test;
import xyz.dynxsty.dih4jda.util.ComponentIdBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ComponentIdTest {
	@Test
	void testComponentIdBuilder() {
		assertEquals("component-identifier:1:2:3.0:4:5.0", ComponentIdBuilder.build("component-identifier", "1", 2L, 3.0, '4', 5f));
		ComponentIdBuilder.setDefaultSeparator("!");
		assertEquals("component-identifier!1!2!3.0!4!5.0", ComponentIdBuilder.build("component-identifier", "1", 2L, 3.0, '4', 5f));
	}
}
