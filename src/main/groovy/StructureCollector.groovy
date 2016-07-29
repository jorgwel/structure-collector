import java.lang.reflect.Field

/**
 * Created by jorgwel on 24/07/16.
 */
class StructureCollector {

    def ArrayList<Class> collect(String classPath, String fullClassName){
        Class<?> loadedClass = loadClass(classPath, fullClassName)
        def classesStack = new ArrayList<Class>()

        addDepedencies(loadedClass, classesStack, classPath)
        classesStack.add(loadedClass)

        return classesStack
    }

    private Class<?> loadClass(String classPath, String fullClassName) {
        URLClassLoader urlCl = getClassLoader(classPath)
        Class clazz = urlCl.loadClass(fullClassName);
        clazz
    }

    private ArrayList bringDependencies(Class clazz) {
        def dependenciesFields = []
        clazz.declaredFields.each { Field field ->
            if (isFieldInManagedPackage(field, clazz))
                dependenciesFields.add(field)
        }
        sortFields(dependenciesFields)
        return dependenciesFields
    }

    private boolean isFieldInManagedPackage(Field field, Class clazz) {
        field.type.package == clazz.package
    }

    private void addDepedencies(Class loadedClass, List<Class> classes, String classPath) {
        ArrayList<Field> dependencyFields = bringDependencies(loadedClass)
        dependencyFields.each { Field field ->
            ArrayList<Class> dependencies = collect(classPath, field.type.canonicalName)
            while (!dependencies.empty)
                classes.add(dependencies.remove(0))
        }
    }

    private sortFields(List<Field> dependencyFields) {
        Collections.sort(dependencyFields, new Comparator<Field>() {
            @Override
            public int compare(Field f2, Field f1) {
                return  f1.name.compareTo(f2.name);
            }
        });

    }

    private URLClassLoader getClassLoader(String classPath) {
        File f = new File(classPath);
        URL[] cp = [f.toURI().toURL()];
        URLClassLoader urlcl = new URLClassLoader(cp);
        urlcl
    }

}
