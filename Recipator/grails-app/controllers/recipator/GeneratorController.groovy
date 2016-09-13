package recipator

import main.RecipeGenerator
import org.json.JSONObject

class GeneratorController {

    def index() {}


    def getNewRecipe() {
        JSONObject recipe
        String[] ingredients = params.get("ingredients[]");

        if(ingredients != null && ingredients.length > 0)
            recipe = RecipeGenerator.getRecipeGenerator().generateRecipeJSON(params.get("ingredients[]"));
        else
            recipe = RecipeGenerator.getRecipeGenerator().generateRecipeJSON();

        render(text: recipe.toString(), contentType: "text/json", encoding: "UTF-8")
    }
}

