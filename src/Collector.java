
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;


public class Collector {

    /**
     * Method {@code getAllCollections} checks the directory specified as parameter for
     * existing class'es files which implements interface {@code Collection} and has public constructor without
     * parameters. Then {@code getAllCollections} creates instances of those classes and add
     * them to the set.
     *
     * @param pathToCollections The directory to be check out
     *
     * @throws  IllegalAccessException, InstantiationException, IOException
     */

    private static Set<? extends Collection> getAllCollections(String pathToCollections) throws IllegalAccessException, InstantiationException, IOException {
        File[] listFiles = new File(pathToCollections).listFiles();
        ArrayList<File> listOfClassFiles = new ArrayList<>();

        for (File f : listFiles) {
            if (f.isFile() && f.getName().endsWith(".class"))
                listOfClassFiles.add(f);
        }

        CustomClassLoader customClassLoader = new CustomClassLoader();
        Class clazz;
        Set set = new HashSet<>();

        for (File f : listOfClassFiles) {
            boolean hasPublicConstructorWithoutParameters = false;
            clazz = customClassLoader.load(f.toPath());
            Constructor[] constructors = clazz.getDeclaredConstructors();

            for (Constructor constructor : constructors) {
                if (constructor.getParameterCount() == 0) {
                    int mods = constructor.getModifiers();
                    if (Modifier.isPublic(mods))
                        hasPublicConstructorWithoutParameters = true;
                }
            }

            if (Collection.class.isAssignableFrom(clazz) && hasPublicConstructorWithoutParameters) {
                set.add(clazz.newInstance());
            }
        }
        return set;
    }

    private static class CustomClassLoader extends ClassLoader {
        private Class load(Path path) throws IOException {
            byte[] b = Files.readAllBytes(path);
            return defineClass(null, b, 0, b.length);
        }
    }
}
