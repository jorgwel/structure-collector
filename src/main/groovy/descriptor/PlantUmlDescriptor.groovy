package descriptor

import groovy.text.SimpleTemplateEngine
import structure.Structure

class PlantUmlDescriptor {


    def static FILE_TEMPLATE =  '''@startuml
${states}
${setOfTransitions}
@enduml'''

    def static STATE_TEMPLATE =  '''state ${newState}
'''

    def static STATE_TRANSITION_TEMPLATE = '''${firstState} --> ${secondState}\n'''

    def describe(Iterator<Structure> structureIterator){
        return buildPlantUmlString(structureIterator)

    }

    def buildPlantUmlString(Iterator<Structure> structures) {

        def fileContents = bringContentsOfPlantUmlFile(structures)
        return renderTemplate(FILE_TEMPLATE, [states: fileContents.stateDeclarations, setOfTransitions: fileContents.transitions])

    }

    def bringContentsOfPlantUmlFile(Iterator<Structure> structures) {
        def statesDeclarationsBuffer = new StringBuilder()
        def transitionsBuffer = new StringBuilder()

        while (structures.hasNext()) {
            def struct = structures.next()
            def state = renderTemplate(STATE_TEMPLATE, [newState: struct.clazz.simpleName])
            statesDeclarationsBuffer.append(state)
            def transition = bringTransitions(struct)
            transitionsBuffer.append(transition)
        }
        [stateDeclarations: statesDeclarationsBuffer.toString(), transitions: transitionsBuffer.toString()]
    }

    def bringTransitions(Structure structure) {
        def stateAndReferencesString = new StringBuilder()
        Iterator<Class> references = structure.references.iterator()
        while (references.hasNext()) {
            Class nextReference = references.next()
            String transitionString = buildTransitionString(structure.clazz, nextReference)
            stateAndReferencesString.append(transitionString)
        }
        stateAndReferencesString.append("\n")
        stateAndReferencesString.toString()
    }

    private String buildTransitionString(Class origin, Class reference) {
        String transitionString = renderTemplate(STATE_TRANSITION_TEMPLATE, [firstState: origin.simpleName, secondState: reference.simpleName])
        transitionString
    }

    private String renderTemplate(templateString, Map templateParams) {
        def engine = new SimpleTemplateEngine()
        def template = engine.createTemplate(templateString).make(templateParams)
        def transitionString = template.toString()
        transitionString
    }


}
