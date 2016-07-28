import groovy.io.FileType
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.tools.FileSystemCompiler
import spock.lang.Specification

class Test extends Specification {

    private final static String BUILD_FOLDER = "src/test/resources/build"
    StructureCollector newInstance
    def setup(){
        println "Setting up"
        newInstance = new StructureCollector()
        compileTestFiles()
    }

    def cleanup(){
        println "Cleaning up"
        deleteFiles()
    }

//    def "Exists a class StructureCollector"(){
//        when:
//            def newInstance = new StructureCollector()
//        then:
//            newInstance != null
//    }


    def "StructureCollector has a method collect which receives a classPath and a class name "(){
        when:
            def method = StructureCollector.getMethod("collect", String, String)
        then:
            method != null
    }

    def "If the full class name of a class without dependencies is sent, the structure contains only that class"(){

        given:
            def noDependenciesClassFullName = "exampleclasses.consonants.J"

        when:
            LinkedList list = newInstance.collect(BUILD_FOLDER, noDependenciesClassFullName)

        and:
            def sizeBeforeRemoval = list.size()
            Object classContainedInList = list.removeFirst()

        and:
            Class<?> loadedClass = loadClass(noDependenciesClassFullName)

        then:
            sizeBeforeRemoval == 1
            list.size() == 0
            classContainedInList.getName() == loadedClass.getName()
    }

    def "If the full class name of a class with only 1 dependency is sent, the structure contains 2 classes, the sent one and the dependency"(){

        given:
            def oneDependencyClassFullName = "exampleclasses.consonants.K"

        when:
            LinkedList<Class> list = newInstance.collect(BUILD_FOLDER, oneDependencyClassFullName)

        then:
            Class k = list.removeFirst()
            k.canonicalName == "exampleclasses.consonants.J"
            Class j = list.removeFirst()
            j.canonicalName == "exampleclasses.consonants.K"
            list.size() == 0

    }

    private Class<?> loadClass(String noDependenciesClassFullName) {
        URLClassLoader urlCl = getClassLoader(BUILD_FOLDER)
        Class loadedClass = urlCl.loadClass(noDependenciesClassFullName)
        return loadedClass
    }

    private URLClassLoader getClassLoader(String build_folder) {
        File f = new File(build_folder)
        URL[] cp = [f.toURI().toURL()]
        URLClassLoader urlcl = new URLClassLoader(cp)
        urlcl
    }


    private boolean deleteFiles() {
        new File(BUILD_FOLDER).deleteDir()
    }

    private void compileTestFiles() {
        println "Compiling files"
        File[] fSorted = bringArrayOfFilesInFolder()
        def configuration = new CompilerConfiguration()
        configuration.setTargetDirectory(BUILD_FOLDER)
        def compiler = new FileSystemCompiler(configuration)
        compiler.compile(fSorted)
    }

    private File[] bringArrayOfFilesInFolder() {
        def dir = new File("src/test/resources/exampleclasses")
        def files = []
        dir.eachFileRecurse(FileType.FILES) { file ->
            files << file
        }
        File[] fileArray = files.toArray(new File[files.size()]);
        fileArray
    }

}
