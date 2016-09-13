package model;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static utils.Utils.*;

/**
 * Created by Blume Till on 09.09.2016.
 */
public class IngredientModel {

    private HashMap<String, HashMap<String, Double>> typeProbabilities;

    private HashMap<String, HashMap<String, Double>> ingredientProbabilities;

    public IngredientModel(String ingredientPairProbabilities, String typeIngProbabilities){
        ingredientProbabilities = new HashMap<>();
        JSONObject input = new JSONObject(readFile(ingredientPairProbabilities));

        for(Object name : input.names()){
            HashMap<String, Double> probabilities = new HashMap<>();
            JSONObject connectedNIngredients = input.getJSONObject((String) name);
            for(Object ingredient : connectedNIngredients.names()){
                String ingName = (String) ingredient;
                double prob = (double) connectedNIngredients.get(ingName);
                if(prob > 0.0)
                    probabilities.put(ingName, prob);

            }
            if(!probabilities.isEmpty())
                ingredientProbabilities.put((String) name, probabilities);
        }
        typeProbabilities = new HashMap<>();

        input = new JSONObject(readFile(typeIngProbabilities));
        for(Object name : input.names()){
            HashMap<String, Double> probabilities = new HashMap<>();
            JSONObject connectedNIngredients = input.getJSONObject((String) name);
            for(Object ingredient : connectedNIngredients.names()){
                String ingName = (String) ingredient;
                double prob = (double) connectedNIngredients.get(ingName);
                if(prob > 0.0)
                    probabilities.put(ingName, prob);

            }
            if(!probabilities.isEmpty())
                typeProbabilities.put((String) name, probabilities);
        }
    }


    /**
     * TODO:
     * @param previousIngredient
     * @return
     */
    public String generateIngredient(String previousIngredient){
        return selectRandom(ingredientProbabilities.get(previousIngredient));
    }


    public String getPossibleIngredient(String type) {
        return getPossibleIngredient(type, null);
    }

        /**
         *
         * @param type
         * @param previousIngredient
         * @return
         */
    public String getPossibleIngredient(String type, String previousIngredient){
        if(previousIngredient != null){
            //merge probabilities
            //p(Ing_i | Type_i) * p(Ing_i | Ing_i-1)
            HashMap<String, Double> ingDistr = ingredientProbabilities.get(previousIngredient);
            if (typeProbabilities.get(type) != null && ingDistr != null) {
                for(Map.Entry<String, Double> entry : typeProbabilities.get(type).entrySet()){
                    //for each type prob
                    ingDistr.merge(entry.getKey(), entry.getValue(), (OLD,NEW) -> (OLD + NEW)/2);
                }
            }

            return selectRandom(ingDistr);
        }else
            return selectRandom(typeProbabilities.get(type));
    }

}
