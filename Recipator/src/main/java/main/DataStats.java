package main;

import org.json.*;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Blume Till on 08.09.2016.
 */
public class DataStats {

    private static String test = "{\"show\": {\"link\": \"/food/programmes/b006vcgr\", \"name\": \"Ready Steady Cook\"}, \"url\": \"http://www.bbc.co.uk/food/recipes/10minutepizza_87314\", \"ingredients\": {\"For the pizza base\": [{\"description\": \"250g/8\\u00beoz plain flour, plus extra for dusting\", \"tags\": [{\"link\": \"/food/plain_flour\", \"name\": \"plain flour\"}]}, {\"description\": \"8 tbsp olive oil\", \"tags\": [{\"link\": \"/food/olive_oil\", \"name\": \"olive oil\"}]}, {\"description\": \"2 tsp water\"}, {\"description\": \"1 tsp sea salt\", \"tags\": [{\"link\": \"/food/salt\", \"name\": \"salt\"}]}], \"main\": [{\"description\": \"250g/8\\u00beoz plain flour, plus extra for dusting\", \"tags\": [{\"link\": \"/food/plain_flour\", \"name\": \"plain flour\"}]}, {\"description\": \"8 tbsp olive oil\", \"tags\": [{\"link\": \"/food/olive_oil\", \"name\": \"olive oil\"}]}, {\"description\": \"2 tsp water\"}, {\"description\": \"1 tsp sea salt\", \"tags\": [{\"link\": \"/food/salt\", \"name\": \"salt\"}]}], \"For the topping\": [{\"description\": \"4 tbsp tomato pur\\u00e9e\", \"tags\": [{\"link\": \"/food/tomato_puree\", \"name\": \"tomato pur\\u00e9e\"}]}, {\"description\": \"60g/2\\u00bcoz shiitake mushrooms, sliced\", \"tags\": [{\"link\": \"/food/shiitake_mushroom\", \"name\": \"shiitake mushrooms\"}]}, {\"description\": \"4 slices prosciutto\", \"tags\": [{\"link\": \"/food/prosciutto\", \"name\": \"prosciutto\"}]}, {\"description\": \"100g/3\\u00bdoz gorgonzola cheese\", \"tags\": [{\"link\": \"/food/gorgonzola_cheese\", \"name\": \"gorgonzola\"}]}, {\"description\": \"1 free-range egg\", \"tags\": [{\"link\": \"/food/egg\", \"name\": \"egg\"}]}]}, \"chef\": {\"link\": \"/food/chefs/james_tanner\", \"name\": \"James Tanner\"}, \"title\": \"Ten-minute pizza\", \"method\": [\"Preheat the oven to 200C/400F/Gas 6.\", \"For the pizza base, place the flour, oil, water and salt into a food processor and blend together until a dough is formed. Tip out onto a floured work surface and knead. Shape into a round base about 20cm/8in wide. \", \"Place into a frying pan over a high heat and brown the base, then using a mini-blowtorch, crisp the top of the pizza. (Alternatively you can do this under the grill.) \", \"For the topping, spread tomato pur\\u00e9e over the top of the base. \", \"Fry the mushrooms in a dry frying pan then scatter over the tomato pur\\u00e9e. Arrange the prosciutto and cheese on top. \", \"Crack an egg into the middle, then place into the oven for five minutes to finish cooking. \", \"Serve on a large plate, and slice into wedges to serve.\"], \"metadata\": {\"Cooking time\": \"less than 10 mins\", \"Serves\": \"Makes 1\", \"Preparation time\": \"less than 30 mins\"}}\n";

    public static void main(String[] args) throws IOException {
        List<JSONObject> allRecipes = new ArrayList<>();
        JSONObject testObject = new JSONObject(test);

        System.out.println(new JSONObject(test));
//
//        System.out.println(new JSONObject(test).get("method"));


//        names.forEach(X ->
//                System.out.println(((JSONArray)((JSONObject) testObject.get("ingredients")).get((String) X)).get("tags")));

        try {
            BufferedReader in = new BufferedReader(new FileReader("recipes.txt"));

            String s = null;
            while ((s = in.readLine()) != null) {
                allRecipes.add(new JSONObject(s));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        HashMap<String, Integer> methodStats = new HashMap<>();
        HashMap<String, Integer> timeStats = new HashMap<>();
        HashMap<String, Integer> servesStats = new HashMap<>();
        HashMap<String, Integer> ingredientsStats = new HashMap<>();


        JSONObject resultObject = new JSONObject();
        List<JSONObject> recipeList = new LinkedList<>();
        for(JSONObject recipe : allRecipes){
            JSONObject resultrecipe = new JSONObject();
//            if(recipe.has("title"))
//                resultrecipe.put("title", recipe.get("title"));
//            else
//                resultrecipe.put("title", "<NO TITLE>");

            if(recipe.has("url"))
                resultrecipe.put("url", recipe.get("url"));
            else{
                System.out.println("happens");
                resultrecipe.put("url", "<URL>");

            }



            JSONObject metadata = ((JSONObject) recipe.get("metadata"));
            if(metadata.has("Cooking time")){
                String tmpS = metadata.get("Cooking time").toString();
                if(tmpS != null){
                    timeStats.merge(tmpS, 1, (X,Y) -> X + Y);

                }
            }
            if(metadata.has("Serves")){
                String tmpS = metadata.get("Serves").toString();
                if(tmpS != null){
                    Pattern p = Pattern.compile("[0-9]+");
                    Matcher m = p.matcher(tmpS);
                    if(m.find())
                    servesStats.merge(m.group(), 1, (X,Y) -> X + Y);

                }
            }

            if(recipe.has("method")){
                List<String> actions = new LinkedList<>();
                JSONArray tmpA = (JSONArray) recipe.get("method");
                if(tmpA != null){
                    for(Object s : tmpA){
                        methodStats.merge((String) s, 1, (X,Y) -> X + Y);
                        actions.add((String) s);
                    }
                }
                resultrecipe.put("actions", actions);
            }


            if(recipe.has("ingredients")) {
                List<String> ingredients = new LinkedList<>();
                JSONArray ingredientNames = ((JSONObject) recipe.get("ingredients")).names();
                for (Object o : ingredientNames){
                    JSONArray tmpJA = (JSONArray) ((JSONObject) recipe.get("ingredients")).get((String) o);
                    tmpJA.forEach(X -> {if(((JSONObject) X).has("tags"))
                        ((JSONArray)(((JSONObject) X).get("tags"))).forEach(Y -> {ingredientsStats.merge(((JSONObject) Y).get("name").toString(), 1, (A,B) -> A + B );
                        ingredients.add(((JSONObject) Y).get("name").toString());});});
                }
                resultrecipe.put("ingredients", ingredients);
            }
            System.out.println();
            recipeList.add(resultrecipe);

        }
        resultObject.put("recipies", recipeList);
        resultObject.write(new FileWriter("simplifiedBBC.json"));
//        int i = 0;
//        for(Map.Entry<String,Integer> entry : timeStats.entrySet()){
//            i += entry.getActionValue();
//            System.out.println(entry);
//        }
//        System.out.println("Cooking Time: " + i + "/" + allRecipes.size());
//
//        i = 0;
//        int numberOfDistinctIngredients = 0;
//        for(Map.Entry<String,Integer> entry : ingredientsStats.entrySet()){
//            i += entry.getActionValue();
//            System.out.println(entry.getKey() + ";" + entry.getActionValue());
//            numberOfDistinctIngredients++;
//        }
//        System.out.println("Ingredients: " + i);
//        System.out.println("numberOfDistinctIngredients: " + i);

    }
}
