package structure

import java.lang.reflect.Field

class StructureCollector {

    final String classPath
    final List validPackages

    StructureCollector(String classPath, List validPackages) {
        this.classPath = classPath
        this.validPackages = validPackages
    }

    def Iterator bringCollection(String fullClassName){
        def it = collect(fullClassName).reverse().iterator()
        return it
    }

    def ArrayList<Structure> collect(String fullClassName){
        def structuresStack = new ArrayList<Structure>()

        Structure structure = createStructure(fullClassName)

        addStructureTail(structure, structuresStack)

        structuresStack.add(structure)

        return structuresStack
    }

    private void addStructureTail(Structure structure, ArrayList<Structure> structuresStack) {
        structure.references.each { Class clazz ->
            ArrayList<Structure> dependencies = collect(clazz.canonicalName)
            while (!dependencies.empty)
                structuresStack.add(dependencies.remove(0))
        }
    }

    private Structure createStructure(String fullClassName) {
        Class<?> loadedClass = loadClass(fullClassName)
        ArrayList<Class> references = bringSortedReferences(loadedClass)
        Structure structure = new Structure(loadedClass, references);
        structure
    }

    private Class<?> loadClass(String fullClassName) {
        URLClassLoader urlCl = getClassLoader()
        Class loadedClass = urlCl.loadClass(fullClassName);
        loadedClass
    }

    private ArrayList<Class> bringSortedReferences(Class clazz) {
        def referencedFields = []
        clazz.declaredFields.each { Field field ->
            if (isFieldInManagedPackage(field.type))
                referencedFields.add(field.type)
        }
        sortClasses(referencedFields)
        return referencedFields
    }

    private boolean isFieldInManagedPackage(Class clazz) {
        this.validPackages.contains(clazz.package?.toString())
    }

    private static sortClasses(List<Class> dependencyFields) {
        Collections.sort(dependencyFields, new Comparator<Class>() {
            @Override
            public int compare(Class c2, Class c1) {
                return  c1.name.compareTo(c2.name);
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
