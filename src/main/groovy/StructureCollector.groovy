import java.lang.reflect.Field

/**
 * Created by jorgwel on 24/07/16.
 */
class StructureCollector {

    final String classPath
    final List validPackages

    StructureCollector(String classPath, List validPackages) {
        this.classPath = classPath
        this.validPackages = validPackages
    }

    def ArrayList<Class> collect(String fullClassName){
        Class<?> loadedClass = loadClass(fullClassName)
        def classesStack = new ArrayList<Class>()

        addDepedencies(loadedClass, classesStack)
        classesStack.add(loadedClass)

        return classesStack
    }

    private Class<?> loadClass(String fullClassName) {
        URLClassLoader urlCl = getClassLoader()
        Class clazz = urlCl.loadClass(fullClassName);
        clazz
    }

    private ArrayList bringDependencies(Class clazz) {
        def dependenciesFields = []
        clazz.declaredFields.each { Field field ->
            if (isFieldInManagedPackage(field))
                dependenciesFields.add(field)
        }
        sortFields(dependenciesFields)
        return dependenciesFields
    }

    private boolean isFieldInManagedPackage(Field field) {
        println field.type.package
        this.validPackages.contains(field.type.package?.toString())
    }

    private void addDepedencies(Class loadedClass, List<Class> classes) {
        ArrayList<Field> dependencyFields = bringDependencies(loadedClass)
        dependencyFields.each { Field field ->
            ArrayList<Class> dependencies = collect(field.type.canonicalName)
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

    private URLClassLoader getClassLoader() {
        File f = new File(classPath);
        URL[] cp = [f.toURI().toURL()];
        URLClassLoader urlcl = new URLClassLoader(cp);
        urlcl
    }

}
