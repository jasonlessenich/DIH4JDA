package com.dynxsty.dih4jda.util;

import com.dynxsty.dih4jda.DIH4JDALogger;

public class Checks {

	private Checks() {
	}

	public static boolean checkImplementation(Class<?> base, Class<?> implementation) {
		boolean doesImplement = ClassUtils.doesImplement(base, implementation);
		if (!doesImplement) {
			DIH4JDALogger.warn(String.format("Class %s does not implement %s. It will be ignored.",
					base.getSimpleName(), implementation.getSimpleName()));
		}
		return doesImplement;
	}
}
