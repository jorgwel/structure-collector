package descriptor

import structure.Structure

class PlantUmlDescriptor {

    def describe(Iterator<Structure> structure){
        def buffer = new StringBuilder()
        buffer.append("@startuml")
        buffer.append("\n")
        while(structure.hasNext()){
            Structure s = structure.next()
//            buffer.append("state " + s.clazz.name)
            Iterator<Class> references = s.references.iterator()
            while(references.hasNext()){
                Class c = references.next()
                buffer.append(s.clazz.simpleName)
                buffer.append(" --> ")
                buffer.append(c.simpleName)
                buffer.append("\n")
            }
        }
        buffer.append("@enduml")
        return buffer.toString()
    }

}
