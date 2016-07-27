import java.lang.reflect.Field

/**
 * Created by jorgwel on 24/07/16.
 */
class StructureCollector {

    def Stack<Class> collect(String classPath, String fullClassName){

        URLClassLoader urlcl = getClassLoader(classPath)
        Class clazz = urlcl.loadClass(fullClassName);
        def classesStack = new Stack<Class>()
        def classFields = clazz.declaredFields

        classFields.each { Field field ->
            if(isFieldInManagedPackage(field, clazz)){
                Stack<Class> dependencies = collect(classPath, field.type.canonicalName)
                pushDependenciesToStack(dependencies, classesStack)
            }
        }

        classesStack.push(clazz)
        return classesStack
    }

    private boolean isFieldInManagedPackage(Field field, Class clazz) {
        field.type.package == clazz.package
    }

    private void pushDependenciesToStack(Stack<Class> dependencies, Stack<Class> classStack) {
        while (!dependencies.empty())
            classStack.push(dependencies.pop())
    }

    private URLClassLoader getClassLoader(String classPath) {
        File f = new File(classPath);
        URL[] cp = [f.toURI().toURL()];
        URLClassLoader urlcl = new URLClassLoader(cp);
        urlcl
    }

}