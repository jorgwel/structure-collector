package descriptor

import groovy.text.SimpleTemplateEngine
import structure.Structure

class PlantUmlDescriptor {


    def static FILE_TEMPLATE =  '''@startuml
${setsOfTransitions}
@enduml'''

    def static STATE_TEMPLATE =  '''state ${newState}
${transitions}

'''

    def static STATE_TRANSITION_TEMPLATE = '''${firstState} --> ${secondState}\n'''

    def describe(Iterator<Structure> structureIterator){
        return buildPlantUmlString(structureIterator)

    }

    private String buildPlantUmlString(Iterator<Structure> structureIterator) {
        def statesString = bringStates(structureIterator)
        return renderTemplate(FILE_TEMPLATE, [setsOfTransitions: statesString])

    }

    def bringStates(Iterator<Structure> structureIterator) {
        def statesBuffer = new StringBuilder()
        while (structureIterator.hasNext()) {
            def nextStructure = structureIterator.next()
            def newStateName = nextStructure.clazz.simpleName
            def transitionsString = buildTransitions(nextStructure)

            def templateParams = [newState: newStateName, transitions: transitionsString]
            def stateAndTransitions = renderTemplate(STATE_TEMPLATE, templateParams)

            statesBuffer.append(stateAndTransitions)
        }
        statesBuffer.toString()
    }

    def buildTransitions(Structure structure) {
        def stateAndReferencesString = new StringBuilder()
        Iterator<Class> references = structure.references.iterator()
        while (references.hasNext()) {
            Class nextReference = references.next()
            String transitionString = buildTransitionString(structure.clazz, nextReference)
            stateAndReferencesString.append(transitionString)
        }
        stateAndReferencesString.toString()
    }

    private String buildTransitionString(Class origin, Class reference) {
        def templateParams = [firstState: origin.simpleName, secondState: reference.simpleName]
        String transitionString = renderTemplate(STATE_TRANSITION_TEMPLATE, templateParams)
        transitionString
    }

    private String renderTemplate(templateString, Map templateParams) {
        def engine = new SimpleTemplateEngine()
        def template = engine.createTemplate(templateString).make(templateParams)
        def transitionString = template.toString()
        transitionString
    }


}
