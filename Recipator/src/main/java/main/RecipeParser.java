package eswcss2016;

import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.process.Morphology;
import edu.stanford.nlp.time.TimeAnnotator;
import edu.stanford.nlp.util.CoreMap;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class RecipeParser {

	StanfordCoreNLP pipeline;

	public RecipeParser() {

		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
		props.setProperty("parse.model", "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");
		props.setProperty("ssplit.isOneSentence", "false");

		pipeline = new StanfordCoreNLP(props);

		pipeline.addAnnotator(new TimeAnnotator("sutime", new Properties()));

	}

	public JSONObject parseRecipe(ArrayList<String> ingredients, String description) {
		JSONObject recipeJsonObject = new JSONObject();
		JSONArray pairsJsonArray = new JSONArray();
		Annotation document = new Annotation(description);
		// run all annotators on this text
		pipeline.annotate(document);
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		Morphology m = new Morphology();
		for (CoreMap sentence : sentences) {
			// traversing the words in the current sentence
			// a CoreLabel is a CoreMap with additional token-specific methods
			String sentenceString = sentence.toString();
			//check if the sentence contains at an ingredient

			String verb = "";
			String ingredient = "";
			for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
				// this is the text of the token
				String word = token.get(TextAnnotation.class);
				// this is the POS tag of the token
				String pos = token.get(PartOfSpeechAnnotation.class);
				//get the lemma
				String lemma = m.lemma(word, pos);
				if(pos.startsWith("VB")) {
					verb = lemma;
				}
				if(pos.startsWith("NN") && ingredients.contains(EnglishNoun.singularOf(lemma))) {
					ingredient = EnglishNoun.singularOf(lemma);
			}
			}
			if(verb != "" && ingredient != "") {
				JSONObject pairJSONObject = new JSONObject();
				pairJSONObject.put("action", verb);
				pairJSONObject.put("ingredient", ingredient);
				pairsJsonArray.put(pairJSONObject);
			}
		}
		recipeJsonObject.put("steps", pairsJsonArray);
		return recipeJsonObject;
	}

	public static void main(String[] args) {
		RecipeParser demo = new RecipeParser();
		JSONArray datasetJson = new JSONArray();
		try {
			BufferedReader in = new BufferedReader(new FileReader("simplifiedBBC.json"));
			StringBuilder sb = new StringBuilder();
			String s = null;
			while ((s = in.readLine()) != null) {
				sb.append(s);
			}
			JSONObject jsonObject = new JSONObject(sb.toString());
			JSONArray jsonArray = jsonObject.getJSONArray("recipies");
			for(int i = 0; i < jsonArray.length(); i++) {
				JSONObject recipeObject = jsonArray.getJSONObject(i);
				JSONArray ingredientsJson = recipeObject.getJSONArray("ingredients");
				JSONArray stepsJson = recipeObject.getJSONArray("actions");
				ArrayList<String> ingredients = new ArrayList<String>();
				for(int j = 0; j < ingredientsJson.length(); j++) {
					//transform ingredients to singular form
					String singularIngredient = EnglishNoun.singularOf(ingredientsJson.get(j).toString());
					if(!ingredients.contains(singularIngredient)) {
						ingredients.add(singularIngredient);
					}
				}
				String descriptionString = "";
				for(int k = 0; k < stepsJson.length(); k++) {
					descriptionString = descriptionString + " " + stepsJson.get(k).toString();
				}
				JSONObject recipeJsonObject = demo.parseRecipe(ingredients, descriptionString);
				recipeJsonObject.put("url", recipeObject.get("url"));
				datasetJson.put(recipeJsonObject);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		try (FileWriter file = new FileWriter("steps.json")) {
			file.write(datasetJson.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
