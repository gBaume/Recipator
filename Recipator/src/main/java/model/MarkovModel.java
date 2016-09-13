package model;

import cooking.Step;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static utils.Utils.*;

/**
 * Created by Blume Till on 08.09.2016.
 */
public class MarkovModel {
    private HashMap<String, Double> startingProbabilities = new HashMap<>();
    private HashMap<String, State> states = new HashMap<>();


    public void train(List<List<Step>> trainingData) {
        HashMap<String, Integer> startingCounts = new HashMap<>();

        //create a distinct set of typeStates and actionStates
        for (List<Step> steps : trainingData) {
            //create starting probabilities
            Step lastStep = null;
            for (int i = 0; i < steps.size(); i++) {

                //save locally
                String object = steps.get(i).getObject();
                String action = steps.get(i).getAction();

                //create state if not already existing
                if (!states.containsKey(object))
                    states.put(object, new State(object));

                //increment action counter
                states.get(object).incAddActionCount(action);
                //creating starting action of sequence
                if (i == 0) {
                    //increment starting counter
                    startingCounts.merge(steps.get(i).getObject(), 1, (X, Y) -> X + Y);
                } else { //continue sequence
                    //add transition from previous step to this step
                    states.get(lastStep.getObject()).incAddTransactionCount(steps.get(i).getObject(),lastStep.getAction());
                }
                lastStep = steps.get(i);
            }
            //last step always points to static end-state
            states.get(steps.get(steps.size()-1).getObject()).incAddTransactionCount(FINAL_OBJECT,lastStep.getAction());
        }

        //calculate starting probabilities
        int sum = 0;
        for (Map.Entry<String, Integer> entry : startingCounts.entrySet())
            sum += entry.getValue();

        for (Map.Entry<String, Integer> entry : startingCounts.entrySet())
            startingProbabilities.put(entry.getKey(), (double) entry.getValue() / (double) sum);

        //updateProbabilities
        for (Map.Entry<String, State> entry : states.entrySet())
            entry.getValue().updateProbabilities();
    }

    public void printModel(){
        System.out.println("Starting Ingredients:");
        for(Map.Entry<String, Double> entry : startingProbabilities.entrySet()){
            System.out.println(entry);
        }

        for(Map.Entry<String, State> entry : states.entrySet()) {
            System.out.println(entry.getValue());
            System.out.println("-----------");
        }
    }



    public List<Step> generateNewRecipe(){
        int maxLength = 20;
        int breakCounter = maxLength;
        List<Step> instructions = new LinkedList<>();
        String start = generateStart();
        HashMap<String, Double> actionProbabilities = states.get(start).getActionProbabilities();
        String action = selectRandom(actionProbabilities);

        String nextState = states.get(start).getNextState(action);
        instructions.add(new Step(start,action));


        //next until finished
        while(!nextState.equals(FINAL_OBJECT) && breakCounter > 0){
            //go to next state
            String currentState = nextState;
            action = selectRandom(states.get(currentState).getActionProbabilities());
            instructions.add(new Step(currentState,action));

            int endlessLoop = 100;
            nextState = states.get(currentState).getNextState(action);
            //rejection sampling
            while((breakCounter > maxLength/2 && nextState.equals(FINAL_OBJECT))  && endlessLoop > 0){
                nextState = states.get(currentState).getNextState(action);
                endlessLoop--;
            }
            breakCounter--;
        }

        return instructions;
    }


    public String generateStart(){
        return selectRandom(startingProbabilities);
    }
}
