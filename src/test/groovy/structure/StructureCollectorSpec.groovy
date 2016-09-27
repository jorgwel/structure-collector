package structure

import spock.lang.Specification
import static testhelpers.CommonMethods.*
import static testhelpers.ClassFullNames.*

class StructureCollectorSpec extends Specification {

    StructureCollector newInstance

    def setup(){
        newInstance = new StructureCollector(BUILD_FOLDER, VALID_PACKAGES)
        compileTestFiles()
    }

    def cleanup(){
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

    def "Method bring collection returns the structure as an iterator (in reverse order)"(){

        given:
            def threeDependenciesClassFullName = Qclass.fullName

        when:
            Iterator<Structure> structureIterator = newInstance.bringCollection(threeDependenciesClassFullName)

        then:

            structureIterator.next().clazz.canonicalName == Qclass.fullName
            structureIterator.next().clazz.canonicalName == Pclass.fullName
            structureIterator.next().clazz.canonicalName == Nclass.fullName
            structureIterator.next().clazz.canonicalName == Jclass.fullName
            structureIterator.next().clazz.canonicalName == Oclass.fullName
            structureIterator.hasNext() == false
    }

}
