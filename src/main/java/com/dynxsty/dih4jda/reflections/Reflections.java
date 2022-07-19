package com.dynxsty.dih4jda.reflections;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.stream.Collectors;

public class Reflections {

    private final String packageName;

    public Reflections(String packageName) {
        this.packageName = packageName;
    }

    /**
     * @return All the classes inside the package.
     */
    public Set<Class<?>> getAllClasses() {
        InputStream is = ClassLoader.getSystemClassLoader()
                .getResourceAsStream(packageName.replaceAll("[.]", "/"));
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        return reader.lines()
                .filter(line -> line.endsWith(".class"))
                .map(this::getClass)
                .collect(Collectors.toSet());
    }

    /**
     * @param className The class name.
     * @return The class with the given name.
     */
    private Class<?> getClass(String className) {
        try {
            return Class.forName(packageName + "."
                    + className.substring(0, className.lastIndexOf('.')));
        } catch (ClassNotFoundException exception) {
            exception.printStackTrace();
        }
        return null;
    }


    /**
     * @param type The type of the class.
     * @return All classes in the package that are assignable to the given type.
     */
    public <T> Set<Class<? extends T>> getSubTypesOf(Class<T> type) {
        return getAllClasses()
                .stream()
                .filter(type::isAssignableFrom)
                .map(clazz -> (Class<? extends T>) clazz)
                .collect(Collectors.toSet());
    }
}
