package com.bytedance.fresco.showsample.utils;


import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.util.SimpleArrayMap;

import java.lang.reflect.InvocationTargetException;

public class FragmentFactory {
    private static final SimpleArrayMap<String, Class<?>> sClassMap = new SimpleArrayMap<>();

    /**
     * Determine if the given fragment name is a support library fragment class.
     *
     * @param classLoader The default classloader to use for loading the Class
     * @param className   Class name of the fragment to load
     * @return Returns the parsed Class
     */
    @NonNull
    private static Class<?> loadClass(@NonNull ClassLoader classLoader,
                                      @NonNull String className) throws ClassNotFoundException {
        Class<?> clazz = sClassMap.get(className);
        if (clazz == null) {
            // Class not found in the cache, see if it's real, and try to add it
            clazz = Class.forName(className, false, classLoader);
            sClassMap.put(className, clazz);
        }
        return clazz;
    }

    /**
     * Determine if the given fragment name is a valid Fragment class.
     *
     * @param classLoader The default classloader to use for loading the Class
     * @param className   Class name of the fragment to test
     * @return true if <code>className</code> is <code>androidx.fragment.app.Fragment</code>
     * or a subclass, false otherwise.
     */
    static boolean isFragmentClass(@NonNull ClassLoader classLoader,
                                   @NonNull String className) {
        try {
            Class<?> clazz = loadClass(classLoader, className);
            return Fragment.class.isAssignableFrom(clazz);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * Parse a Fragment Class from the given class name. The resulting Class is kept in a global
     * cache, bypassing the {@link Class#forName(String)} calls when passed the same
     * class name again.
     *
     * @param classLoader The default classloader to use for loading the Class
     * @param className   The class name of the fragment to parse.
     * @return Returns the parsed Fragment Class
     * @throws Fragment.InstantiationException If there is a failure in parsing
     *                                         the given fragment class.  This is a runtime exception; it is not
     *                                         normally expected to happen.
     */
    @SuppressWarnings("unchecked")
    @NonNull
    public static Class<? extends Fragment> loadFragmentClass(@NonNull ClassLoader classLoader,
                                                              @NonNull String className) {
        try {
            Class<?> clazz = loadClass(classLoader, className);
            return (Class<? extends Fragment>) clazz;
        } catch (ClassNotFoundException e) {
            throw new Fragment.InstantiationException("Unable to instantiate fragment " + className
                    + ": make sure class name exists", e);
        } catch (ClassCastException e) {
            throw new Fragment.InstantiationException("Unable to instantiate fragment " + className
                    + ": make sure class is a valid subclass of Fragment", e);
        }
    }

    /**
     * Create a new instance of a Fragment with the given class name. This uses
     * {@link #loadFragmentClass(ClassLoader, String)} and the empty
     * constructor of the resulting Class by default.
     *
     * @param classLoader The default classloader to use for instantiation
     * @param className   The class name of the fragment to instantiate.
     * @return Returns a new fragment instance.
     * @throws Fragment.InstantiationException If there is a failure in instantiating
     *                                         the given fragment class.  This is a runtime exception; it is not
     *                                         normally expected to happen.
     */
    @NonNull
    public Fragment instantiate(@NonNull ClassLoader classLoader, @NonNull String className) {
        try {
            Class<? extends Fragment> cls = loadFragmentClass(classLoader, className);
            return cls.getConstructor().newInstance();
        } catch (java.lang.InstantiationException e) {
            throw new Fragment.InstantiationException("Unable to instantiate fragment " + className
                    + ": make sure class name exists, is public, and has an"
                    + " empty constructor that is public", e);
        } catch (IllegalAccessException e) {
            throw new Fragment.InstantiationException("Unable to instantiate fragment " + className
                    + ": make sure class name exists, is public, and has an"
                    + " empty constructor that is public", e);
        } catch (NoSuchMethodException e) {
            throw new Fragment.InstantiationException("Unable to instantiate fragment " + className
                    + ": could not find Fragment constructor", e);
        } catch (InvocationTargetException e) {
            throw new Fragment.InstantiationException("Unable to instantiate fragment " + className
                    + ": calling Fragment constructor caused an exception", e);
        }
    }
}