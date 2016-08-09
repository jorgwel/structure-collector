package descriptor

import spock.lang.Specification
import structure.Structure

class PlantUmlDescriptorSpec extends Specification {

    PlantUmlDescriptor newInstance

    def setup(){
        newInstance = new PlantUmlDescriptor()
    }

    def cleanup(){
    }




    def "Has a method named \"describe\" which receives a Structure iterator"(){
        when:
            def method = PlantUmlDescriptor.getMethod("describe", Iterator)
        then:
            notThrown(NoSuchMethodException)
    }



    def "Has a method named \"describe\" which returns a string"(){
        when:
            String description = newInstance.describe(new ArrayList<Structure>().iterator())
        then:
            description != null
    }



}
