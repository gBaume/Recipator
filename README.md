# Recipator (ESWC SS 16 project)
by Chun Lu, Maria Panteli, Till Blume, Kemele Endris and Laura Koesten

##What is it about?

During the ESWC SS 16 Mini-Project we developed and implemented the Recipator, 
a Hidden Markov Model based approach to generate new cooking recipes based on 
the set of existing recipes crawled from the BBC good food recipe collection. 
In order to produce “cookable” recipes we extracted sequences of ingredients 
and their corresponding actions from the crawled dataset by applying basic 
Entity Detection and nearest verb extraction, . e.g, (cook, potato). 
To assure the novelty of the recipes, we classified the ingredients using DBpedia Spotlight. 
Those sequences of type-action tuples were used as training data for the 
Hidden Markov Model with an additional independence assumption. Each action 
only depends on the type of ingredient whereas the next type of ingredient 
depends on the last type-action tuple as a whole. By doing this we assure a 
more plausible next step, e.g. (wash, potato) -> (cut, potato) -> (cook, potato).
As final step the type is again replaced by a concrete ingredient. The replacing 
process is twofold. The first ingredient of a newly generated sequence is 
determined by the pure probability P(Ingredient | Type). For the following 
ingredients we use a probability matrix of commonly co-appearing ingredients to 
determine the next ingredient P(Ingredient_i+1 | Type, Ingredient_i). 
This probability matrix significantly boosts the “cookability” of the newly 
generated recipes compared to only using type distribution.

Link to the google presentation:
https://docs.google.com/presentation/d/1LgyJ2G30karyJK8xUk26pHKF3aSENU3KQofqp84WSRw/pub?start=false&loop=false&delayms=3000

##Usage:

 + Run Grails Application for Webfrontend
 + Call main-Method in RecipeGenerator for offline usage