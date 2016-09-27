package testhelpers

import groovy.io.FileType
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.tools.FileSystemCompiler

/**
 * Created by jorgwel on 10/08/16.
 */
class CommonMethods {

    private static final String EXAMPLE_SOURCE_FILES_FOLDER = "src/test/resources/exampleclasses"
    private static final String BUILD_FOLDER = "src/test/resources/build"
    private static final List VALID_PACKAGES = [
            "package exampleclasses.vowels",
            "package exampleclasses.consonants"
    ]


    static Class<?> loadClass(String noDependenciesClassFullName) {
        URLClassLoader urlCl = getClassLoader(BUILD_FOLDER)
        Class loadedClass = urlCl.loadClass(noDependenciesClassFullName)
        return loadedClass
    }

    static URLClassLoader getClassLoader(String build_folder) {
        File f = new File(build_folder)
        URL[] cp = [f.toURI().toURL()]
        URLClassLoader urlcl = new URLClassLoader(cp)
        urlcl
    }


    static boolean deleteFiles() {
        new File(BUILD_FOLDER).deleteDir()
    }

    static void compileTestFiles() {
        println "Compiling files"
        File[] fSorted = bringArrayOfFilesInFolder()
        def configuration = new CompilerConfiguration()
        configuration.setTargetDirectory(BUILD_FOLDER)
        def compiler = new FileSystemCompiler(configuration)
        compiler.compile(fSorted)
    }

    static File[] bringArrayOfFilesInFolder() {
        def dir = new File(EXAMPLE_SOURCE_FILES_FOLDER)
        def files = []
        dir.eachFileRecurse(FileType.FILES) { file ->
            files << file
        }
        File[] fileArray = files.toArray(new File[files.size()]);
        fileArray
    }

}
