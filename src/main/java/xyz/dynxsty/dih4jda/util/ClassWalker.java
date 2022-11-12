package xyz.dynxsty.dih4jda.util;

import xyz.dynxsty.dih4jda.exceptions.DIH4JDAException;
import xyz.dynxsty.dih4jda.exceptions.DIH4JDAReflectionException;
import xyz.dynxsty.dih4jda.exceptions.InvalidPackageException;

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

/**
 * A class that provides you with the ability to get all classes inside a specific package.
 *
 * @since v1.6
 */
public class ClassWalker {
    
	/**
	 * The name of the package.
	 * Example: com.java.example
	 */
	private final String packageName;
    
	public ClassWalker(@Nonnull String packageName) {
		this.packageName = packageName;
	}

	/**
	 * Gets all classes inside the given package and sub packages.
	 *
	 * @return An unmodifiable {@link Set} of classes inside the given package.
	 * @since v1.6
	 */
	public @Nonnull Set<Class<?>> getAllClasses() throws DIH4JDAException {
		try {
			String packagePath = packageName.replace('.', '/');
			ClassLoader classLoader = IOUtils.getClassLoaderForClass(ClassWalker.class);

			URL resourceUrl = classLoader.getResource(packagePath);
			if (resourceUrl == null) {
				throw new InvalidPackageException(String.format("%s package not found in ClassLoader", packagePath));
			}
			URI pkg = resourceUrl.toURI();

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
			try (Stream<Path> allPaths = Files.walk(root)) {
				return allPaths.filter(Files::isRegularFile)
						.filter(file -> file.toString().endsWith(".class"))
						.map(this::mapFileToName)
						.map(clazz -> {
							try {
								return classLoader.loadClass(clazz);
							} catch (ClassNotFoundException exception) {
								throw new UncheckedClassLoadException(exception);
							}
						})
						.collect(Collectors.toSet());
			} catch (UncheckedClassLoadException exception) {
				throw new DIH4JDAReflectionException(exception.getCause());
			} finally {
				if (fileSystem != null) {
					fileSystem.close();
				}
			}
		} catch (URISyntaxException | IOException exception) {
			throw new DIH4JDAReflectionException(exception);
		}
	}
    
	/**
	 * Gets you the name of the given .class file.
	 *
	 * @param file the path to the .class file.
	 * @return the name plus the .class file extension.
	 * @since v1.6
	 */
	private @Nonnull String mapFileToName(@Nonnull Path file) {
		String path = file.toString().replace(file.getFileSystem().getSeparator(), ".");
		return path.substring(path.indexOf(packageName), path.length() - ".class".length());
	}

	/**
	 * Gets all classes in the given package and sub packages which extend the specified class.
	 *
	 * @param type The parent class to search for.
	 * @return An unmodifiable {@link Set} of classes which are assignable to the given type.
	 * @since v1.6
	 */
	@SuppressWarnings("unchecked") //Is actually checked because we filter out the un-assignable classes.
	public @Nonnull <T> Set<Class<? extends T>> getSubTypesOf(@Nonnull Class<T> type) throws DIH4JDAException {
		return getAllClasses()
				.stream()
				.filter(type::isAssignableFrom)
				.map(clazz -> (Class<? extends T>) clazz)
				.collect(Collectors.toSet());
	}
    
	/**
	 * A runtime exception that is thrown for everything related to class loading in this class.
	 *
	 * @since v1.6
	 */
	private static class UncheckedClassLoadException extends RuntimeException {
		public UncheckedClassLoadException(Throwable cause) {
			super(cause);
		}
	}
}
