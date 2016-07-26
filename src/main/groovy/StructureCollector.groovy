/**
 * Created by jorgwel on 24/07/16.
 */
class StructureCollector {

    def Stack collect(String classPath, String fullClassName){

        File f = new File(classPath);
        URL[] cp = [f.toURI().toURL()];
        URLClassLoader urlcl = new URLClassLoader(cp);
        Class clazz = urlcl.loadClass(fullClassName);
        def classesStack = new Stack()
        classesStack.push(clazz)
        return classesStack
    }

}
