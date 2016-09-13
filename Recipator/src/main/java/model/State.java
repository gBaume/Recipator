package model;

import java.util.HashMap;
import java.util.Map;

import static utils.Utils.*;

/**
 * Created by Blume Till on 08.09.2016.
 */
public class State {
    private String description;

    private HashMap<String, Double> actionProbabilities;
    private HashMap<String, HashMap<String, Double>> transitionProbabilityMatrix;

    //for training only
    private HashMap<String, Integer> actionCounts;
    private HashMap<String, HashMap<String, Integer>> transitionCountingMatrix;

    public State(String description) {
        this.description = description;
        this.actionProbabilities = new HashMap<>();
        this.actionCounts = new HashMap<>();
        this.transitionCountingMatrix = new HashMap<>();
        this.transitionProbabilityMatrix = new HashMap<>();
    }

    public HashMap<String, Double> getActionProbabilities() {
        return actionProbabilities;
    }

    public String getNextState(String action) {
        return selectRandom(transitionProbabilityMatrix.get(action));
    }

    public void incAddActionCount(String action) {
        this.actionCounts.merge(action, 1, (X, Y) -> X + Y);
    }

    public void incAddTransactionCount(String target, String action) {
        if (!transitionCountingMatrix.containsKey(target))
            transitionCountingMatrix.put(target, new HashMap<>());

        transitionCountingMatrix.get(target).merge(action, 1, (OLD, NEW) -> OLD + NEW);
    }


    @Override
    public String toString() {
        String state = "[" + description + "]\n";
        state += "\tActions: \n";
        for (Map.Entry<String, Double> entry : actionProbabilities.entrySet()) {
            state += "\t\t-- " + entry.getValue() + " -->" + entry.getKey() + "\n";
        }

        state += "\tTransitions: \n";
        for (Map.Entry<String, HashMap<String, Double>> entrySet : transitionProbabilityMatrix.entrySet()) {
            for (Map.Entry<String, Double> entry : entrySet.getValue().entrySet()) {
                state += "\t\t" + entrySet.getKey() + " -- " + entry.getValue() + " -->" + entry.getKey() + "\n";
            }
        }
        return state;
    }


    public void updateProbabilities() {
        //calculate probabilities for actions
        int sum = 0;
        //sum everything up
        for (Map.Entry<String, Integer> entry : actionCounts.entrySet())
            sum += entry.getValue();

        //add probabilities
        for (Map.Entry<String, Integer> entry : actionCounts.entrySet())
            actionProbabilities.put(entry.getKey(), (double) entry.getValue() / (double) sum);

        ////////////////
        //Entry: Target [<Action, Count>, <Action,Count> ..]
        //for each outgoing link to another state
        //save the probability for each action
        //for each action the outgoing probabilities will sum up to 1
        ////////////////
        HashMap<String, Integer> sumForAction = new HashMap<>();
        for (Map.Entry<String, HashMap<String, Integer>> entry : transitionCountingMatrix.entrySet())
            for (Map.Entry<String, Integer> actionEntry : entry.getValue().entrySet())
                sumForAction.merge(actionEntry.getKey(), actionEntry.getValue(), (OLD, NEW) -> OLD + NEW);


        transitionProbabilityMatrix = new HashMap<>();
        for (Map.Entry<String, HashMap<String, Integer>> entry : transitionCountingMatrix.entrySet()) {
            for (Map.Entry<String, Integer> actionEntry : entry.getValue().entrySet()) {
                if (!transitionProbabilityMatrix.containsKey(actionEntry.getKey()))
                    transitionProbabilityMatrix.put(actionEntry.getKey(), new HashMap<>());

                transitionProbabilityMatrix.get(actionEntry.getKey()).put(entry.getKey(),
                        (double) actionEntry.getValue() / (double) sumForAction.get(actionEntry.getKey()));
            }
        }
    }
}
