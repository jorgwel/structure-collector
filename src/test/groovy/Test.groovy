import clases.Structure
import clases.StructureCollector
import groovy.io.FileType
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.tools.FileSystemCompiler
import spock.lang.Specification
import static helpers.ClassFullNames.*

class Test extends Specification {
    private static final String EXAMPLE_SOURCE_FILES_FOLDER = "src/test/resources/exampleclasses"
    private static final String BUILD_FOLDER = "src/test/resources/build"
    private static final List VALID_PACKAGES = [
        "package exampleclasses.vowels",
        "package exampleclasses.consonants"
    ]

    StructureCollector newInstance
    def setup(){
        println "Setting up"
        newInstance = new StructureCollector(BUILD_FOLDER, VALID_PACKAGES)
        compileTestFiles()
    }

    def cleanup(){
        println "Cleaning up"
        deleteFiles()
    }

    def "clases.StructureCollector has a method collect which receives a class name "(){
        when:
            def method = StructureCollector.getMethod("collect", String)
        then:
            method != null
    }

    def "If the full class name of a class without dependencies is sent, the structure contains only that class"(){

        given:
            def noDependenciesClassFullName = Jclass.fullName

        when:
            ArrayList list = newInstance.collect(noDependenciesClassFullName)

        and:
            def sizeBeforeRemoval = list.size()
            Object classContainedInList = list.remove(0).clazz

        and:
            Class<?> loadedClass = loadClass(noDependenciesClassFullName)

        then:
            sizeBeforeRemoval == 1
            list.size() == 0
            classContainedInList.getName() == loadedClass.getName()
    }

    def "If the full class name of a class with only 1 dependency is sent, the structure contains 2 classes, the sent one and the dependency"(){

        given:
            def oneDependencyClassFullName = Nclass.fullName

        when:
            ArrayList<Structure> list = newInstance.collect(oneDependencyClassFullName)

        then:
            list.size() == 2
            list.remove(0).clazz.canonicalName == Jclass.fullName
            list.remove(0).clazz.canonicalName == Nclass.fullName
            list.size() == 0

    }

    def "If the full class name of a class with 2 dependencies is sent, the structure contains 3 classes"(){

        given:
            def twoDependenciesClassFullName = Kclass.fullName

        when:
            ArrayList<Structure> list = newInstance.collect(twoDependenciesClassFullName)

        then:
            list.size() == 3
            Class m = list.remove(0).clazz
            m.canonicalName == Mclass.fullName
            Class j = list.remove(0).clazz
            j.canonicalName == Jclass.fullName
            Class k = list.remove(0).clazz
            k.canonicalName == Kclass.fullName
            list.size() == 0

    }

    def "If the full class name of a class with 4 dependencies is sent, the structure contains 5 classes"(){

        given:
            def threeDependenciesClassFullName = Qclass.fullName

        when:
            ArrayList<Structure> list = newInstance.collect(threeDependenciesClassFullName)

        then:
            list.size() == 5
            list.remove(0).clazz.canonicalName == Oclass.fullName
            list.remove(0).clazz.canonicalName == Jclass.fullName
            list.remove(0).clazz.canonicalName == Nclass.fullName
            list.remove(0).clazz.canonicalName == Pclass.fullName
            list.remove(0).clazz.canonicalName == Qclass.fullName
    }

    private static Class<?> loadClass(String noDependenciesClassFullName) {
        URLClassLoader urlCl = getClassLoader(BUILD_FOLDER)
        Class loadedClass = urlCl.loadClass(noDependenciesClassFullName)
        return loadedClass
    }

    private static URLClassLoader getClassLoader(String build_folder) {
        File f = new File(build_folder)
        URL[] cp = [f.toURI().toURL()]
        URLClassLoader urlcl = new URLClassLoader(cp)
        urlcl
    }


    private static boolean deleteFiles() {
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

    private static File[] bringArrayOfFilesInFolder() {
        def dir = new File(EXAMPLE_SOURCE_FILES_FOLDER)
        def files = []
        dir.eachFileRecurse(FileType.FILES) { file ->
            files << file
        }
        File[] fileArray = files.toArray(new File[files.size()]);
        fileArray
    }

}
