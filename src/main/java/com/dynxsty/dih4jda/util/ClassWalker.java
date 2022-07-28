package com.dynxsty.dih4jda.util;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashSet;
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
			URI pkg = ClassLoader.getSystemClassLoader().getResource(packagePath).toURI();
			Set<Class<?>> allClasses = new HashSet<>();

			Path root;
			if (pkg.toString().startsWith("jar:")) {
				try {
					root = FileSystems.getFileSystem(pkg).getPath(packagePath);
				} catch (FileSystemNotFoundException exception) {
					root = FileSystems.newFileSystem(pkg, Collections.emptyMap()).getPath(packagePath);
				}
			} else {
				root = Paths.get(pkg);
			}

			try (Stream<Path> allPaths = Files.walk(root)) {
				allPaths.filter(Files::isRegularFile).forEach(file -> {
					try {
						String path = file.toString().replace('/', '.');

						String name;
						try {
							name = path.substring(path.indexOf(packageName), path.length() - ".class".length());
						} catch (StringIndexOutOfBoundsException exception) {
							path = file.toString().replace('\\', '.');
							name = path.substring(path.indexOf(packageName), path.length() - ".class".length());
						}

						allClasses.add(Class.forName(name));
					} catch (ClassNotFoundException | IndexOutOfBoundsException exception) {
						exception.printStackTrace();
					}
				});
			}
			return allClasses;
		} catch (NullPointerException | URISyntaxException | IOException exception) {
			exception.printStackTrace();
			return Collections.emptySet();
		}
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
