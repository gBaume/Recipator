package main;

import cooking.Step;
import model.MarkovModel;
import model.IngredientModel;
import org.json.JSONArray;
import org.json.JSONObject;
import utils.Utils;

import java.io.*;
import java.util.*;

import static utils.Utils.*;
/**
 * Created by Blume Till on 08.09.2016.
 */
public class RecipeGenerator {
    private static boolean useTypes = true;


    public static void main(String[] args) throws FileNotFoundException {
        getRecipeGenerator().generateRecipe().forEach(STEP -> System.out.println(STEP));
    }
    //singleton
    private static RecipeGenerator recipeGenerator = null;
    /**
     * create/get singleton instance
     * @return
     */
    public static RecipeGenerator getRecipeGenerator(){
        if(recipeGenerator == null)
            recipeGenerator = new RecipeGenerator("input/steps.json",   //bbc recipes
                    "input/new-types-complete.json",                    //type mappings
                    "input/type_to_ingredients.json",                   // type probabilities
                    "input/ingredient_pairs_prob.json");                //typical ingredient pairs

        return recipeGenerator;
    }

    private MarkovModel markovModel;
    private IngredientModel ingredientModel;
    private HashMap<String, HashSet<String>> typeMappings;


    public RecipeGenerator(String trainingData, String typeData, String mappingProbs, String pairProbs){
        markovModel = new MarkovModel();
        List<List<Step>> trainingDataset = null;
        try {
            trainingDataset = loadTrainingData(trainingData);
            typeMappings = loadMappingData(typeData);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //TODO: without proper type mappings, do not use them!
        if(useTypes)
            markovModel.train(mapIngredientToType(trainingDataset));
        else
            markovModel.train(trainingDataset);

        ingredientModel = new IngredientModel(pairProbs,mappingProbs);
    }

    public List<Step> generateRecipe(){
        if(useTypes)
            return mapTypeToIngredient(markovModel.generateNewRecipe());
        else
            return markovModel.generateNewRecipe();
    }


    public JSONObject generateRecipeJSON(String... requiredIngredients){
        JSONObject jsonObject = new JSONObject();
        List<JSONObject> jsonSteps = new LinkedList<>();

        boolean found = false;
        List<Step> steps = null;
        int notPossible = 0;
        while (!found && notPossible < 100){
            steps = generateRecipe();
            found = true;
            int count = 0;
            if(requiredIngredients != null && requiredIngredients.length > 0){
                for(String required : requiredIngredients){
                    for(Step step : steps){
                        if (step.getObject().equals(required)){
                            count++;
                            break;
                        }
                    }
                }
                if(count < requiredIngredients.length)
                    found = false;
            }
            notPossible++;
        }
        if(notPossible >= 100)
            System.out.println("Was not possible!");


        for(Step step : steps){
            JSONObject jsonStep = new JSONObject();
            jsonStep.put("ingredient", step.getObject());
            jsonStep.put("action", step.getAction());
            jsonSteps.add(jsonStep);
        }
        jsonObject.put("recipe", jsonSteps);
        return jsonObject;
    }



    public List<Step> mapTypeToIngredient(List<Step> steps){
        String prevIng = null;
        for(Step step : steps){
            String ingredient = ingredientModel.getPossibleIngredient(step.getObject(), prevIng);
            if(ingredient != null) {
                step.setObject(ingredient);
                prevIng = ingredient;
            }
        }
        return steps;
    }


    public List<List<Step>> mapIngredientToType(List<List<Step>> listOfInstructions){
        for(List<Step> recipe : listOfInstructions){
            for (Step step : recipe){
                HashSet<String> typeSet = typeMappings.get(step.getObject());
                if(typeSet != null && typeSet.iterator().hasNext())
                    step.setObject(typeSet.iterator().next());
            }
        }
        return listOfInstructions;
    }



    /**
     *
     * @param filename
     */

    private List<List<Step>> loadTrainingData(String filename) throws FileNotFoundException {
        JSONArray listOfSteps = new JSONArray(readFile(filename));
        List<List<Step>> listOfInstructions = new LinkedList<>();

        for(int i=0; i < listOfSteps.length(); i++){
            List<Step> recipe = new LinkedList<>();
            ((JSONArray)((JSONObject)listOfSteps.get(i)).get("steps")).
                    forEach(ITEM -> recipe.add(
                            new Step(((JSONObject)ITEM).getString("ingredient"),
                                    ((JSONObject)ITEM).getString("action"))));
            if(recipe.size() > 0)
                listOfInstructions.add(recipe);

        }
        return listOfInstructions;
    }


    private HashMap<String, HashSet<String>> loadMappingData(String filename) throws FileNotFoundException {
        JSONObject listOfSteps = new JSONObject(Utils.readFile(filename));
        HashMap<String, HashSet<String>> typeMappings = new HashMap<>();
        for(Object name : listOfSteps.names()){
            HashSet<String> typeSet = new HashSet<>();
            typeSet.add((String) listOfSteps.get((String) name));
            typeMappings.put((String) name, typeSet);
        }

        return typeMappings;
    }

    /**
     * Debugging
     *
     * @return
     */
    public List<List<Step>> generateDummyData(){
        List<List<Step>> listOfInstructions = new LinkedList<>();
        Step step1 = new Step("onion", "chop");
        Step step2 = new Step("tomato", "fry");
        Step step3 = new Step("fries", "flip");
        Step step4 = new Step("beef", "boil");
        Step step5 = new Step("beef", "chop");
        Step step6 = new Step("fries", "fry");

        List<Step> recipe1 = new LinkedList<>();
        recipe1.add(step1);
        recipe1.add(step2);
        recipe1.add(step5);

        List<Step> recipe2 = new LinkedList<>();
        recipe2.add(step3);
        recipe2.add(step4);
        recipe2.add(step2);
        recipe2.add(step5);


        List<Step> recipe3 = new LinkedList<>();
        recipe3.add(step1);
        recipe3.add(step2);
        recipe3.add(step6);

        listOfInstructions.add(recipe1);
        listOfInstructions.add(recipe2);
        listOfInstructions.add(recipe3);
        return listOfInstructions;
    }

}
