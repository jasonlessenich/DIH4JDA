package com.dynxsty.dih4jda.util;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;

/**
 * Utility class for finding classes on the classpath.
 */
public class ClasspathHelper {
	private ClasspathHelper() {
	}

	/**
	 * Gets all {@link URL}s in the specified package.
	 *
	 * @param packageName The package name to search for.
	 * @return A {@link Collection} of {@link URL}s, based on the given package name.
	 */
	public static @Nonnull Collection<URL> forPackage(@Nonnull String packageName) {
		List<URL> results = new ArrayList<>();
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		try {
			Enumeration<URL> urls = loader.getResources(getResourceName(packageName));
			while (urls.hasMoreElements()) {
				results.add(urls.nextElement());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return results;
	}

	private static @Nonnull String getResourceName(@Nonnull String name) {
		String resource = name.replace(".", "/")
				.replace("\\", "/");
		if (resource.startsWith("/")) {
			resource = resource.substring(1);
		}
		return resource;
	}
}
