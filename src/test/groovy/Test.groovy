
import groovy.io.FileType
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.tools.FileSystemCompiler
import spock.lang.Specification
import static helpers.ClassFullNames.*

class Test extends Specification {

    private final static String BUILD_FOLDER = "src/test/resources/build"
    private final static List VALID_PACKAGES = [
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

    def "StructureCollector has a method collect which receives a class name "(){
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
            Object classContainedInList = list.remove(0)

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
            ArrayList<Class> list = newInstance.collect(oneDependencyClassFullName)

        then:
            list.size() == 2
            list.remove(0).canonicalName == Jclass.fullName
            list.remove(0).canonicalName == Nclass.fullName
            list.size() == 0

    }

    def "If the full class name of a class with 2 dependencies is sent, the structure contains 3 classes"(){

        given:
            def twoDependenciesClassFullName = Kclass.fullName

        when:
            ArrayList<Class> list = newInstance.collect(twoDependenciesClassFullName)

        then:
            list.size() == 3
            Class m = list.remove(0)
            m.canonicalName == Mclass.fullName
            Class j = list.remove(0)
            j.canonicalName == Jclass.fullName
            Class k = list.remove(0)
            k.canonicalName == Kclass.fullName
            list.size() == 0

    }

    def "If the full class name of a class with 4 dependencies is sent, the structure contains 5 classes"(){

        given:
            def threeDependenciesClassFullName = Qclass.fullName

        when:
            ArrayList<Class> list = newInstance.collect(threeDependenciesClassFullName)

        then:
            list.size() == 5
            list.remove(0).canonicalName == Jclass.fullName
            list.remove(0).canonicalName == Nclass.fullName
            list.remove(0).canonicalName == Pclass.fullName
            list.remove(0).canonicalName == Oclass.fullName
            list.remove(0).canonicalName == Qclass.fullName
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
