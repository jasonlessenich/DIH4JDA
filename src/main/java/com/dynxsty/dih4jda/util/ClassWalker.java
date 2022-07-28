package com.dynxsty.dih4jda.util;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClassWalker {

	private final String packageName;

	public ClassWalker(String packageName) {
		this.packageName = packageName;
	}

	/**
	 * Gets all classes inside the given package and sub packages.
	 *
	 * @return An unmodifiable {@link Set} of classes inside the given package.
	 */
	public @Nonnull Set<Class<?>> getAllClasses() {
		try {
			String packagePath = packageName.replace('.', '/');
			ClassLoader classLoader;
			if (Thread.currentThread().getContextClassLoader() != null) classLoader = Thread.currentThread().getContextClassLoader();
			 else classLoader = ClassWalker.class.getClassLoader();


			URL resourceURL = classLoader.getResource(packagePath);
			if (resourceURL == null) {
				return Collections.emptySet();
			}
			URI pkg = resourceURL.toURI();

			Path root;
			FileSystem fileSystem = null;
			if (pkg.toString().startsWith("jar:")) {
				try {
					root = FileSystems.getFileSystem(pkg).getPath(packagePath);
				} catch (FileSystemNotFoundException exception) {
					fileSystem = FileSystems.newFileSystem(pkg, Collections.emptyMap());
					root = fileSystem.getPath(packagePath);
				}
			} else {
				root = Paths.get(pkg);
			}

			Set<Class<?>> classes;
			try (Stream<Path> allPaths = Files.walk(root)) {
				classes = allPaths.filter(Files::isRegularFile)
						.filter(file -> file.toString().endsWith(".class"))
						.map(this::mapFileToName)
						.map(clazz -> {
							try {
								return classLoader.loadClass(clazz);
							} catch (ClassNotFoundException e) {
								return null;
							}
						})
						.collect(Collectors.toSet());
			}
			if (fileSystem != null) fileSystem.close();
			return classes;
		} catch (URISyntaxException | IOException exception) {
			exception.printStackTrace();
			return Collections.emptySet();
		}
	}

	private String mapFileToName(Path file) {
		String path = file.toString().replace('/', '.');
		if (!path.contains(packageName)) {
			path = file.toString().replace('\\', '.');
		}
		return path.substring(path.indexOf(packageName), path.length() - ".class".length());
	}

	/**
	 * Gets all classes in the given package and sub packages which extend the specified class.
	 *
	 * @param type The parent class to search for.
	 * @return An unmodifiable {@link Set} of classes which are assignable to the given type.
	 */
	public @Nonnull <T> Set<Class<? extends T>> getSubTypesOf(@Nonnull Class<T> type) {
		return getAllClasses()
				.stream()
				.filter(type::isAssignableFrom)
				.map(clazz -> (Class<? extends T>) clazz)
				.collect(Collectors.toSet());
	}
}
