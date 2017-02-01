package genetics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;

public class SimpleGeneticEvolution {
	
	public static String target = "cat";
	public static int population = 50; 
	public static double mutationRate = 0.3;
	
	private static char[] letters = "abcdefghijklmnopqrstuvwxyz".toCharArray();
	
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		int generation = 0;
		// 1. Start off with a random population
		Map<String, Double> startingPopulation = CreateRandomDNA(population);
		//System.out.println(startingPopulation.toString());
		
		// Loop until one population matches the target
		while(!startingPopulation.containsKey(target)){
			generation++;
			// 2. Select optimal population
			List<String> selection = GetSelection(startingPopulation);
			// 3. Reproduce based on population
			startingPopulation = Reproduce(selection);
			System.out.println("Generation: " + generation + "\n" + startingPopulation.keySet().toString());
		}
		System.out.println("Elapsed Time: " + (System.currentTimeMillis()-startTime) + "ms");
	}
	// Create initial population for the genetic algorithm to work
	public static Map<String, Double> CreateRandomDNA(int numberOfElements){
		StringBuilder dnaElement = new StringBuilder();
		// Using HashMap to keep track of the actual data and the fitness for each element
		Map<String, Double> randomDNAs = new HashMap<String, Double>(); 
		for(int count = 0; count < numberOfElements; count++){
			dnaElement = new StringBuilder();
			// Append a random character based on target's length
			for(int i = 0; i < target.length(); i++)
				dnaElement.append(letters[ThreadLocalRandom.current().nextInt(0, (122-97) + 1)]);
			
			// Add to HashMap
			randomDNAs.put(dnaElement.toString(), 0.0);
		}
		
		return randomDNAs;
	}
	// Get the selection that will produce children.
	// Selection is decided on fitness
	public static List<String> GetSelection(Map<String, Double> generationPopulation){
		// Calculate fitness for each element
		for(Entry<String, Double> e : generationPopulation.entrySet()){
			e.setValue(GetFitnessOfElement(e.getKey()));
		}
		// Create a selection, used for probability, which is based on the fitness 
		// of each element.
		// If one element is more fit than another, the element will have take
		// up more spaces in the array list. (Similar to pie chart)
		List<String> selection = new ArrayList<String>();
		for(Entry<String, Double> e : generationPopulation.entrySet()){
			//System.out.println(e.getKey() + " : " + e.getValue()*10);
			for(int i = 0; i < e.getValue()*10; i++)
				selection.add(e.getKey());
		}
		//System.out.println(selection.toString());
		return selection;
	}
	// Determine the fitness of a element.
	// Fitness determined base on the number of character matches
	// it has with the target.
	public static double GetFitnessOfElement(String element){
		double fitness = 0;
		for(int index = 0; index < target.length(); index++){
			if(element.charAt(index) == target.charAt(index))
				fitness+=1;
		}
		return fitness/target.length();
	}
	// Reproduce based on selected elements
	public static Map<String, Double> Reproduce(List<String> selection){
		Map<String, Double> children = new HashMap<String, Double>();
		// Reproduce until the population is the same as the previous population
		for(int count = 0; count < population; count++){
			// Select random parents
			String parentA = selection.get(ThreadLocalRandom.current().nextInt(0, (selection.size())));
			String parentB = selection.get(ThreadLocalRandom.current().nextInt(0, (selection.size())));
			// Create child
			String child = CreateChild(parentA, parentB);
			//System.out.println(child + " : " + MutateChild(child));
			// Mutate child
			children.put(MutateChild(child), 0.0);
		}
		return children;
	}
	// Create child from parents
	public static String CreateChild(String parentA, String parentB){
		StringBuilder child = new StringBuilder();
		for(int cycle = 0; cycle < target.length(); cycle++){
			// Decide which parent to take a character from
			int parentInheritance = ThreadLocalRandom.current().nextInt(0, 1+1);
			child.append((parentInheritance == 0? parentA.charAt(cycle) : parentB.charAt(cycle)));
		}
		return child.toString();
	}
	// Mutate the child based off of the Rate of Mutation
	public static String MutateChild(String child){
		StringBuilder mutatedChild = new StringBuilder();
		// The fixed mutation to compare
		// Should be between 0 and child's length
		double mutationRateFixed = (mutationRate/child.length()) * 10;
		
		for(int index = 0; index < child.length(); index++){
			// Select random number between 0 and child's length (0-3)
			double randomMutationChance = ThreadLocalRandom.current().nextDouble(0.0, child.length() + 1);
			// Ex. mutationRate = 0.8
			// child's length = 3
			// mutationRateFixed = ~2.666
			// mutation will NOT occur if 2.666 < randomMutationChance < 3
			if(mutationRateFixed > randomMutationChance)
				// Choose a random letter
				mutatedChild.append(letters[ThreadLocalRandom.current().nextInt(0, (122-97) + 1)]);
			else
				mutatedChild.append(child.charAt(index));
		}
		return mutatedChild.toString();
	}

}
