package descriptor

import spock.lang.Specification
import structure.Structure
import structure.StructureCollector

import static testhelpers.ClassFullNames.*
import static testhelpers.CommonMethods.*

class PlantUmlDescriptorSpec extends Specification {

    PlantUmlDescriptor newInstance

    def setup(){
        newInstance = new PlantUmlDescriptor()
        compileTestFiles()
    }

    def cleanup(){
        deleteFiles()
    }




    def "Has a method named \"describe\" which receives a Structure iterator"(){
        when:
            def method = PlantUmlDescriptor.getMethod("describe", Iterator)
        then:
            notThrown(NoSuchMethodException)
    }



    def "Has a method named \"describe\" which returns a string"(){
        given:
            def structureCollector = new StructureCollector(BUILD_FOLDER, VALID_PACKAGES)
            Iterator<Structure> structureIterator = structureCollector.bringCollection(Qclass.fullName)
        when:
            String description = newInstance.describe(structureIterator)
            println "description: " + description
        then:
            description == """@startuml
state Q
state P
state N
state J
state O

Q --> O
Q --> P

P --> N

N --> J




@enduml"""

    }



}
