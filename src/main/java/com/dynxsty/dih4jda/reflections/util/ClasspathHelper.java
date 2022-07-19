package com.dynxsty.dih4jda.reflections.util;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;

//TODO add javadocs
public class ClasspathHelper {

    public static Collection<URL> forPackage(@Nonnull String packageName) {
        List<URL> result = new ArrayList<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try {
            Enumeration<URL> urls = classLoader.getResources(resourceName(packageName));
            while (urls.hasMoreElements()) {
                result.add(urls.nextElement());
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return result;
    }

    private static String resourceName(@Nonnull String name) {
        String resourceName = name.replace(".", "/");
        resourceName = resourceName.replace("\\", "/");
        if (resourceName.startsWith("/")) {
            resourceName = resourceName.substring(1);
        }
        return resourceName;
    }
}
